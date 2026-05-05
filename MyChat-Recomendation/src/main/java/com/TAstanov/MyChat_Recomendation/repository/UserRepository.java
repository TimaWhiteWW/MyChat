package com.TAstanov.MyChat_Recomendation.repository;

import com.TAstanov.MyChat_Recomendation.domain.user.Profile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository {

    @Value("${spring.datasource.host}")
    private String host;

    @Value("${spring.datasource.port}")
    private String port;

    private static final String USER = "postgres";
    private static final String PASSWORD = "Rts28022007";
    private static final String DB_NAME = "mychat";

    private String getDatabaseUrl() {
        return "jdbc:postgresql://" + host + ":" + port + "/" + DB_NAME;
    }

    public Profile getProfileByTag(String tag) {
        Profile profile = null;
        String sql = "SELECT * FROM user_profiles WHERE tag = ?";

        try (Connection connection = DriverManager.getConnection(getDatabaseUrl(), USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, tag);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    profile = mapResultSetToProfile(resultSet);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return profile;
    }

    public List<Profile> getProfilesByTags(List<String> tags) {
        List<Profile> profiles = new ArrayList<>();

        if (tags == null || tags.isEmpty()) {
            return profiles;
        }

        String placeholders = String.join(", ", tags.stream().map(t -> "?").toArray(String[]::new));
        String sql = "SELECT * FROM user_profiles WHERE tag IN (" + placeholders + ")";

        try (Connection connection = DriverManager.getConnection(getDatabaseUrl(), USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (int i = 0; i < tags.size(); i++) {
                statement.setString(i + 1, tags.get(i));
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    profiles.add(mapResultSetToProfile(resultSet));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return profiles;
    }

    private Profile mapResultSetToProfile(ResultSet resultSet) throws SQLException {
        Profile profile = new Profile();
        profile.setId(resultSet.getLong("id"));
        profile.setTag(resultSet.getString("tag"));
        profile.setName(resultSet.getString("name"));
        profile.setSurname(resultSet.getString("surname"));
        profile.setEmail(resultSet.getString("email"));
        profile.setPhoneNumber(resultSet.getString("phone_number"));
        profile.setWeight(resultSet.getInt("weight"));
        profile.setHeight(resultSet.getDouble("height"));
        profile.setEarnings(resultSet.getLong("earnings"));
        profile.setAge(resultSet.getInt("age"));
        profile.setGender(Profile.GENDER.valueOf(resultSet.getString("gender")));
        profile.setAboutMe(resultSet.getString("about_me"));
        profile.setCity(resultSet.getString("city"));
        profile.setCountry(resultSet.getString("country"));
        profile.setPersonalityExtraversion(resultSet.getDouble("personality_extraversion"));
        profile.setPersonalityOpenness(resultSet.getDouble("personality_openness"));
        profile.setPersonalityConscientiousness(resultSet.getDouble("personality_conscientiousness"));
        profile.setLifeValueFamily(resultSet.getDouble("life_value_family"));
        profile.setLifeValueCareer(resultSet.getDouble("life_value_career"));
        profile.setActivityLevel(resultSet.getDouble("activity_level"));
        profile.setBeauty(resultSet.getInt("beauty"));
        return profile;
    }
}
