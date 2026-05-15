package com.TAstanov.MyChat_LoadedTest.controller;

import com.TAstanov.MyChat_LoadedTest.service.PerformanceTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/test")
public class PerformanceTestController {

    private final PerformanceTestService performanceTestService;

    @GetMapping
    public void test(@RequestParam Long co){
        performanceTestService.performanceTest(retrieves);
    }

}
