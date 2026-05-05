package com.TAstanov.MyChat_Recomendation.web.controller;

import com.TAstanov.MyChat_Recomendation.domain.user.Profile;
import com.TAstanov.MyChat_Recomendation.service.SelectPartnerStackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/recommendation")
@RequiredArgsConstructor
public class RecommendationController {

    private final SelectPartnerStackService selectPartnerStackService;

    @PostMapping("/selectPartnerStack")
    public List<Profile> selectPartnerStack(@RequestParam("tag") String requestingUserTag){
        return selectPartnerStackService.formPartnerStack(requestingUserTag);
    }

}
