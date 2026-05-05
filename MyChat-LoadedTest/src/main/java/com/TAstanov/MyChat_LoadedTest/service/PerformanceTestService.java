package com.TAstanov.MyChat_LoadedTest.service;

import com.TAstanov.MyChat_LoadedTest.entity.LikeDto;
import com.TAstanov.MyChat_LoadedTest.entity.Profile;
import com.TAstanov.MyChat_LoadedTest.entity.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


@Service
@RequiredArgsConstructor
public class PerformanceTestService {

    private final ApplicationContext applicationContext;
    private static final AtomicLong completed = new AtomicLong(0);

    public void performanceTest(Long retrieves){
        for(long i = 0L; i < retrieves * 3; i++){
            WebClient webClient = applicationContext.getBean(WebClient.class);
            PerformanceTestRunnable performanceTestRunnable = new PerformanceTestRunnable(webClient, retrieves, completed);
            new Thread(performanceTestRunnable).start();
        }
    }

}

@Data
@AllArgsConstructor
@Slf4j
class PerformanceTestRunnable implements Runnable{

    private final WebClient webClient;
    private final Long retrieves;
    private AtomicLong completed;

    @Override
    public void run() {

        for(long i = 0L; i < retrieves; i++) {


            UserProfile userProfile = UserProfile.generateSingleUserProfile();

            // 3) Построение multipart/form-data
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            // простые поля
            builder.part("tag",           userProfile.getTag());
            builder.part("email",         userProfile.getEmail());
            builder.part("name",          userProfile.getName());
            builder.part("surname",       userProfile.getSurname());
            builder.part("phoneNumber",   userProfile.getPhoneNumber());
            builder.part("age",           userProfile.getAge().toString());
            builder.part("gender",        userProfile.getGender().name());
            builder.part("aboutMe",       userProfile.getAboutMe());
            builder.part("city",          userProfile.getCity());
            builder.part("country",       userProfile.getCountry());
            builder.part("height",        userProfile.getHeight().toString());
            builder.part("weight",        userProfile.getWeight().toString());
            builder.part("location",      userProfile.getLocation().toString());
            builder.part("earnings", userProfile.getEarnings().toString());


            // списки enum'ов
            for (var h : userProfile.getHobby()) {
                builder.part("hobby", h.name());
            }
            for (var p : userProfile.getProfession()) {
                builder.part("profession", p.name());
            }

            // числовые шкалы
            builder.part("personalityExtraversion",  userProfile.getPersonalityExtraversion().toString());
            builder.part("personalityOpenness",      userProfile.getPersonalityOpenness().toString());
            builder.part("personalityConscientiousness", userProfile.getPersonalityConscientiousness().toString());
            builder.part("lifeValueFamily",          userProfile.getLifeValueFamily().toString());
            builder.part("lifeValueCareer",          userProfile.getLifeValueCareer().toString());
            builder.part("activityLevel",            userProfile.getActivityLevel().toString());

            // картинка(и)
            byte[] imageBytes = userProfile.getPictureBytes(); // метод в вашем UserProfile
            ByteArrayResource bar = new ByteArrayResource(imageBytes) {
                @Override
                public String getFilename() {
                    return "download.jpg";
                }
            };
            builder.part("pictures", bar)
                    .header("Content-Disposition", "form-data; name=pictures; filename=download.jpg");


            webClient.post()
                    .uri("http://localhost:8082/api/v1/self/create")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            try{
                Thread.sleep(10000);
            }catch (Exception e){}


            List<Profile> profileList = webClient.post()
                    .uri("http://localhost:8083/api/v1/recommendation/selectPartnerStack?tag=" + userProfile.getTag())
                    .retrieve()
                    .bodyToFlux(Profile.class)
                    .collectList()
                    .block();
            assert profileList != null;


            for(int f = 0; f < 10; f++){
                assert profileList != null;
                LikeDto likeDto = new LikeDto(userProfile.getTag(), profileList.get(f).getTag());
                String like = i % 2 == 0 ? "like" : "pass";
                webClient.post()
                        .uri("http://localhost:8084/api/v1/swipe/"+like)
                        .bodyValue(likeDto)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
            };

            long c = completed.incrementAndGet();
            System.out.println(c);

        }

    }
}