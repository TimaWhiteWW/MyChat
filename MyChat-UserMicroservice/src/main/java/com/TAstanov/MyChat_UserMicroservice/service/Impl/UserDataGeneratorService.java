package com.TAstanov.MyChat_UserMicroservice.service.Impl;

import com.TAstanov.MyChat_UserMicroservice.domain.profiles.Profile;
import com.TAstanov.MyChat_UserMicroservice.domain.profiles.Profile.GENDER;
import com.TAstanov.MyChat_UserMicroservice.repository.LocationRepository;
import com.TAstanov.MyChat_UserMicroservice.repository.UserRepository;
import com.TAstanov.MyChat_UserMicroservice.service.KafkaService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

//@Service
@RequiredArgsConstructor
@Slf4j
public class UserDataGeneratorService {

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final KafkaService kafkaService;

    private static final int PROFILES_PER_GENDER = 15_000;
    private static final int BATCH_SIZE = 1_000;
    private final Random random = new Random();

    // Выбираем случайную центральную точку (широта, долгота)
    private final double[] centerPoint = {randomDouble(-10, 10), randomDouble(-10, 10)};

    @PostConstruct
    @Transactional
    public void generateUserProfiles() {
        List<Profile> profiles = new ArrayList<>(BATCH_SIZE);

        for (int i = 0; i < PROFILES_PER_GENDER; i++) {
            profiles.add(createProfile(GENDER.MALE, i));

            if ((i + 1) % BATCH_SIZE == 0) {
                saveBatch(profiles);
            }
        }

        for (int i = 0; i < PROFILES_PER_GENDER; i++) {
            profiles.add(createProfile(GENDER.FEMALE, i));

            if ((i + 1) % BATCH_SIZE == 0) {
                saveBatch(profiles);
            }
        }

        log.info("Сгенерировано {} мужских и {} женских профилей (всего {}) и отправлено в Kafka.",
                PROFILES_PER_GENDER, PROFILES_PER_GENDER, PROFILES_PER_GENDER * 2);
    }


    private void saveBatch(List<Profile> profiles) {
        userRepository.saveAll(profiles);
        profiles.forEach(this::saveLocation);
        profiles.forEach(kafkaService::send);
        profiles.clear();
    }

    private Profile createProfile(GENDER gender, int index) {
        Profile profile = new Profile();
        String tag = gender.name().toLowerCase() + "_" + index;
        profile.setTag(tag + UUID.randomUUID());
        profile.setName("Name" + index);
        profile.setSurname("Surname" + index);
        profile.setEmail(tag + UUID.randomUUID() + "@example.com");
        profile.setPhoneNumber(generatePhoneNumber());
        profile.setWeight(randomInt(50, 100));
        profile.setHeight(randomDouble(1.50, 2.00));
        profile.setHobby(List.of());
        profile.setProfession(List.of());
        profile.setEarnings(randomLong(20000, 200000));
        profile.setAge(randomInt(18, 70));
        profile.setGender(gender);
        profile.setAboutMe("About me " + index);
        profile.setCity("City" + randomInt(1, 100));
        profile.setCountry("Country" + randomInt(1, 50));
        profile.setPersonalityExtraversion(randomDouble(0, 10));
        profile.setPersonalityOpenness(randomDouble(0, 10));
        profile.setPersonalityConscientiousness(randomDouble(0, 10));
        profile.setLifeValueFamily(randomDouble(0, 10));
        profile.setLifeValueCareer(randomDouble(0, 10));
        profile.setActivityLevel(randomDouble(0, 10));
        profile.setBeauty(randomInt(0, 10));
        return profile;
    }

    private void saveLocation(Profile profile) {
        double[] point = generatePointNear(centerPoint[0], centerPoint[1], 300); // Генерируем в пределах 300 км
        String locationWKT = String.format("POINT(%f %f)", point[1], point[0]);
        locationRepository.saveUserLocation(profile.getTag(), profile.getCity(), profile.getCountry(), locationWKT);
    }

    private static double[] generatePointNear(double lat, double lon, double maxDistanceKm) {
        double earthRadius = 6371.0; // Радиус Земли в км
        double maxDistRad = maxDistanceKm / earthRadius; // Перевод в радианы

        double randomAngle = Math.random() * 2 * Math.PI; // Угол (0–360°)
        double randomDist = Math.sqrt(Math.random()) * maxDistRad; // Равномерное распределение

        double newLat = Math.asin(Math.sin(Math.toRadians(lat)) * Math.cos(randomDist) +
                Math.cos(Math.toRadians(lat)) * Math.sin(randomDist) * Math.cos(randomAngle));

        double newLon = Math.toRadians(lon) + Math.atan2(
                Math.sin(randomAngle) * Math.sin(randomDist) * Math.cos(Math.toRadians(lat)),
                Math.cos(randomDist) - Math.sin(Math.toRadians(lat)) * Math.sin(newLat));

        return new double[]{Math.toDegrees(newLat), Math.toDegrees(newLon)};
    }

    private int randomInt(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    private double randomDouble(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    private long randomLong(long min, long max) {
        return min + (long) ((max - min) * random.nextDouble());
    }

    private String generatePhoneNumber() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
