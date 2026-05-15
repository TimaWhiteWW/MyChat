package com.TAstanov.MyChat_Notification.server.Impl;

import com.TAstanov.MyChat_Notification.domain.ChatMessage;
import com.TAstanov.MyChat_Notification.domain.DemoUser;
import com.TAstanov.MyChat_Notification.web.dto.ChatMessageDto;
import com.TAstanov.MyChat_Notification.web.dto.DemoLoginRequest;
import com.TAstanov.MyChat_Notification.web.dto.DemoRegisterRequest;
import com.TAstanov.MyChat_Notification.web.dto.VerifyEmailRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DemoAppService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final JdbcTemplate jdbcTemplate;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostConstruct
    public void init() {
        createTables();
        seedUsers();
    }

    public void register(DemoRegisterRequest request) {
        require(request.getName(), "name");
        require(request.getEmail(), "email");
        require(request.getPassword(), "password");

        if (findByEmail(request.getEmail()) != null) {
            throw new IllegalArgumentException("Email already registered");
        }

        String id = UUID.randomUUID().toString();
        String code = String.format("%06d", RANDOM.nextInt(1_000_000));
        jdbcTemplate.update("""
                INSERT INTO demo_users
                    (id, name, email, password_hash, email_verified, verification_code, verification_expires_at,
                     age, city, gender, interests, bio, photo)
                VALUES (?, ?, ?, ?, false, ?, ?, 21, 'Moscow', 'MALE', '', '', '')
                """,
                id,
                request.getName().trim(),
                request.getEmail().trim().toLowerCase(),
                passwordEncoder.encode(request.getPassword()),
                code,
                Timestamp.valueOf(LocalDateTime.now().plusMinutes(15)));

        sendCode(request.getEmail(), code);
    }

    public DemoUser verify(VerifyEmailRequest request) {
        require(request.getEmail(), "email");
        require(request.getCode(), "code");

        DemoUser user = findByEmail(request.getEmail());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        Verification verification = getVerification(user.getId());
        if (verification.code == null || verification.expiresAt == null) {
            throw new IllegalArgumentException("Verification code not found");
        }
        if (verification.expiresAt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Verification code expired");
        }
        if (!verification.code.equals(request.getCode().trim())) {
            throw new IllegalArgumentException("Invalid verification code");
        }

        jdbcTemplate.update("""
                UPDATE demo_users
                SET email_verified = true, verification_code = null, verification_expires_at = null
                WHERE id = ?
                """, user.getId());
        return findUser(user.getId());
    }

    public DemoUser login(DemoLoginRequest request) {
        require(request.getEmail(), "email");
        require(request.getPassword(), "password");

        DemoUser user = findByEmail(request.getEmail());
        if (user == null || !passwordEncoder.matches(request.getPassword(), getPasswordHash(user.getId()))) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        if (!Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new IllegalArgumentException("Email is not verified");
        }
        return user;
    }

    public DemoUser updateProfile(String userId, DemoUser profile) {
        findUser(userId);
        jdbcTemplate.update("""
                UPDATE demo_users
                SET name = ?, age = ?, city = ?, gender = ?, interests = ?, bio = ?, photo = ?
                WHERE id = ?
                """,
                profile.getName(),
                profile.getAge(),
                profile.getCity(),
                profile.getGender(),
                profile.getInterests(),
                profile.getBio(),
                profile.getPhoto(),
                userId);
        return findUser(userId);
    }

    public DemoUser uploadPhoto(String userId, MultipartFile photo) {
        findUser(userId);
        if (photo == null || photo.isEmpty()) {
            throw new IllegalArgumentException("Photo is required");
        }
        String contentType = photo.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }
        try {
            String dataUrl = "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(photo.getBytes());
            jdbcTemplate.update("UPDATE demo_users SET photo = ? WHERE id = ?", dataUrl, userId);
            return findUser(userId);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Could not read uploaded photo");
        }
    }

    public List<DemoUser> discover(String userId) {
        findUser(userId);
        return jdbcTemplate.query("""
                SELECT * FROM demo_users
                WHERE id <> ?
                  AND email_verified = true
                  AND NOT EXISTS (SELECT 1 FROM demo_likes WHERE user_id = ? AND target_id = demo_users.id)
                  AND NOT EXISTS (SELECT 1 FROM demo_passes WHERE user_id = ? AND target_id = demo_users.id)
                ORDER BY name
                """, userMapper(), userId, userId, userId);
    }

    public boolean like(String userId, String targetId) {
        findUser(userId);
        findUser(targetId);
        jdbcTemplate.update("""
                INSERT INTO demo_likes(user_id, target_id)
                VALUES (?, ?)
                ON CONFLICT DO NOTHING
                """, userId, targetId);
        return isMatch(userId, targetId);
    }

    public void pass(String userId, String targetId) {
        findUser(userId);
        findUser(targetId);
        jdbcTemplate.update("""
                INSERT INTO demo_passes(user_id, target_id)
                VALUES (?, ?)
                ON CONFLICT DO NOTHING
                """, userId, targetId);
    }

    public List<DemoUser> matches(String userId) {
        findUser(userId);
        return jdbcTemplate.query("""
                SELECT u.* FROM demo_users u
                WHERE u.id <> ?
                  AND EXISTS (SELECT 1 FROM demo_likes l WHERE l.user_id = ? AND l.target_id = u.id)
                  AND EXISTS (SELECT 1 FROM demo_likes l WHERE l.user_id = u.id AND l.target_id = ?)
                ORDER BY u.name
                """, userMapper(), userId, userId, userId);
    }

    public ChatMessage send(ChatMessageDto dto) {
        require(dto.getFromTag(), "fromTag");
        require(dto.getToTag(), "toTag");
        require(dto.getText(), "text");
        findUser(dto.getFromTag());
        findUser(dto.getToTag());
        if (!isMatch(dto.getFromTag(), dto.getToTag())) {
            throw new IllegalArgumentException("Chat is available only after match");
        }

        LocalDateTime createdAt = LocalDateTime.now();
        jdbcTemplate.update("""
                INSERT INTO demo_chat_messages(from_tag, to_tag, text, created_at)
                VALUES (?, ?, ?, ?)
                """, dto.getFromTag(), dto.getToTag(), dto.getText(), Timestamp.valueOf(createdAt));
        return new ChatMessage(dto.getFromTag(), dto.getToTag(), dto.getText(), createdAt);
    }

    public List<ChatMessage> dialog(String userId, String partnerId) {
        findUser(userId);
        findUser(partnerId);
        return jdbcTemplate.query("""
                SELECT from_tag, to_tag, text, created_at
                FROM demo_chat_messages
                WHERE (from_tag = ? AND to_tag = ?) OR (from_tag = ? AND to_tag = ?)
                ORDER BY created_at
                """, chatMapper(), userId, partnerId, partnerId, userId);
    }

    private void createTables() {
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS demo_users (
                    id VARCHAR(64) PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    email VARCHAR(150) NOT NULL UNIQUE,
                    password_hash TEXT NOT NULL,
                    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
                    verification_code VARCHAR(6),
                    verification_expires_at TIMESTAMP,
                    age INT,
                    city VARCHAR(100),
                    gender VARCHAR(20),
                    interests TEXT,
                    bio TEXT,
                    photo TEXT
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS demo_likes (
                    user_id VARCHAR(64) NOT NULL REFERENCES demo_users(id) ON DELETE CASCADE,
                    target_id VARCHAR(64) NOT NULL REFERENCES demo_users(id) ON DELETE CASCADE,
                    PRIMARY KEY (user_id, target_id)
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS demo_passes (
                    user_id VARCHAR(64) NOT NULL REFERENCES demo_users(id) ON DELETE CASCADE,
                    target_id VARCHAR(64) NOT NULL REFERENCES demo_users(id) ON DELETE CASCADE,
                    PRIMARY KEY (user_id, target_id)
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS demo_chat_messages (
                    id BIGSERIAL PRIMARY KEY,
                    from_tag VARCHAR(64) NOT NULL REFERENCES demo_users(id) ON DELETE CASCADE,
                    to_tag VARCHAR(64) NOT NULL REFERENCES demo_users(id) ON DELETE CASCADE,
                    text TEXT NOT NULL,
                    created_at TIMESTAMP NOT NULL
                )
                """);
    }

    private void seedUsers() {
        if (countUsers() > 0) {
            return;
        }
        addSeed("Anna", "anna@demo.local", "FEMALE", 21, "Moscow", "music, walks, cinema",
                "I like evening walks, concerts and calm conversations.",
                "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=900&q=80");
        addSeed("Maxim", "max@demo.local", "MALE", 23, "Kazan", "sport, games, travel",
                "Looking for someone to laugh with and discuss plans.",
                "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=900&q=80");
        addSeed("Lena", "lena@demo.local", "FEMALE", 22, "Saint Petersburg", "books, coffee, design",
                "I study, work on projects, and like good stories.",
                "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?auto=format&fit=crop&w=900&q=80");
    }

    private void addSeed(String name, String email, String gender, Integer age, String city, String interests, String bio, String photo) {
        jdbcTemplate.update("""
                INSERT INTO demo_users
                    (id, name, email, password_hash, email_verified, age, city, gender, interests, bio, photo)
                VALUES (?, ?, ?, ?, true, ?, ?, ?, ?, ?, ?)
                """,
                UUID.randomUUID().toString(),
                name,
                email,
                passwordEncoder.encode("demo123"),
                age,
                city,
                gender,
                interests,
                bio,
                photo);
    }

    private DemoUser findUser(String id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM demo_users WHERE id = ?", userMapper(), id);
        } catch (EmptyResultDataAccessException exception) {
            throw new IllegalArgumentException("User not found");
        }
    }

    private DemoUser findByEmail(String email) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM demo_users WHERE lower(email) = lower(?)", userMapper(), email);
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    private String getPasswordHash(String userId) {
        return jdbcTemplate.queryForObject("SELECT password_hash FROM demo_users WHERE id = ?", String.class, userId);
    }

    private Verification getVerification(String userId) {
        return jdbcTemplate.queryForObject("""
                SELECT verification_code, verification_expires_at
                FROM demo_users
                WHERE id = ?
                """, (rs, rowNum) -> new Verification(
                rs.getString("verification_code"),
                rs.getTimestamp("verification_expires_at") == null
                        ? null
                        : rs.getTimestamp("verification_expires_at").toLocalDateTime()), userId);
    }

    private int countUsers() {
        Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM demo_users", Integer.class);
        return count == null ? 0 : count;
    }

    private boolean isMatch(String firstId, String secondId) {
        Integer count = jdbcTemplate.queryForObject("""
                SELECT count(*) FROM demo_likes first_like
                WHERE first_like.user_id = ? AND first_like.target_id = ?
                  AND EXISTS (
                    SELECT 1 FROM demo_likes second_like
                    WHERE second_like.user_id = ? AND second_like.target_id = ?
                  )
                """, Integer.class, firstId, secondId, secondId, firstId);
        return count != null && count > 0;
    }

    private RowMapper<DemoUser> userMapper() {
        return (rs, rowNum) -> {
            DemoUser user = new DemoUser();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            user.setPassword(null);
            user.setEmailVerified(rs.getBoolean("email_verified"));
            user.setAge((Integer) rs.getObject("age"));
            user.setCity(rs.getString("city"));
            user.setGender(rs.getString("gender"));
            user.setInterests(rs.getString("interests"));
            user.setBio(rs.getString("bio"));
            user.setPhoto(rs.getString("photo"));
            return user;
        };
    }

    private RowMapper<ChatMessage> chatMapper() {
        return (ResultSet rs, int rowNum) -> new ChatMessage(
                rs.getString("from_tag"),
                rs.getString("to_tag"),
                rs.getString("text"),
                rs.getTimestamp("created_at").toLocalDateTime());
    }

    private void sendCode(String email, String code) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.info("Email verification code for {} is {}", email, code);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("MyChat email verification");
            message.setText("Your MyChat verification code: " + code);
            mailSender.send(message);
        } catch (Exception exception) {
            log.warn("Could not send verification email to {}. Code: {}", email, code, exception);
        }
    }

    private void require(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
    }

    private record Verification(String code, LocalDateTime expiresAt) {
    }
}
