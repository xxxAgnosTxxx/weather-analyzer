package com.prokhorovgm.weather_analyzer.controller;

import com.prokhorovgm.weather_analyzer.model.ActionType;
import com.prokhorovgm.weather_analyzer.model.DangerousResponse;
import com.prokhorovgm.weather_analyzer.service.WeatherService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.util.StringUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/weather")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class WeatherController {
    WeatherService weatherService;

    @GetMapping("regions")
    List<String> getRegions() {
        return weatherService.getRegions().stream().distinct().filter(StringUtil::isNotBlank).sorted().toList();
    }

    @GetMapping("days")
    List<String> getByRegion(@RequestParam String region) {
        return weatherService.getByRegion(region);
    }

    @GetMapping
    List<String> getDangerous() {
        return weatherService.getDangerous().stream().distinct().filter(StringUtil::isNotBlank).sorted().toList();
    }

    @GetMapping("days/action")
    List<DangerousResponse> getWorstActionDaysByRegion(@RequestParam String region, @RequestParam ActionType action) {
        return weatherService.getWorstActionDaysByRegion(region, action);
    }
}
