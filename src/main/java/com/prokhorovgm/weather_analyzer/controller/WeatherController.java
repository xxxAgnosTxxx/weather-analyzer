package com.prokhorovgm.weather_analyzer.controller;

import com.prokhorovgm.weather_analyzer.model.ActionType;
import com.prokhorovgm.weather_analyzer.model.DangerousResponse;
import com.prokhorovgm.weather_analyzer.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Все регионы из статистики")
    List<String> getRegions() {
        return weatherService.getRegions().stream().distinct().filter(StringUtil::isNotBlank).sorted().toList();
    }

    @GetMapping("days")
    @Operation(summary = "Наиболее опасные дни по региону")
    List<String> getByRegion(@RequestParam String region) {
        return weatherService.getByRegion(region);
    }

    @GetMapping
    @Operation(summary = "Все опасные погодные явления из статистики")
    List<String> getWeatherFacts() {
        return weatherService.getWeatherFacts().stream().distinct().filter(StringUtil::isNotBlank).sorted().toList();
    }

    @GetMapping("days/action")
    @Operation(summary = "Дни региона с наблюдаемым погодным явлением")
    List<DangerousResponse> getWorstActionDaysByRegion(@RequestParam String region, @RequestParam ActionType action) {
        return weatherService.getWorstActionDaysByRegion(region, action);
    }
}
