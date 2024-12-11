package com.prokhorovgm.weather.analyzer.controller;

import com.prokhorovgm.weather.analyzer.aop.GeoValid;
import com.prokhorovgm.weather.analyzer.service.WeatherService;
import com.prokhorovgm.weather.analyzer.model.ActionType;
import com.prokhorovgm.weather.analyzer.model.DangerousResponse;
import com.prokhorovgm.weather.analyzer.service.LogisticService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    LogisticService logisticService;

    @GetMapping("regions")
    @Operation(summary = "Все регионы из статистики")
    List<String> getRegions() {
        return weatherService.getRegions();
    }

    @GetMapping("days")
    @Operation(summary = "Наиболее опасные дни по региону")
    List<String> getByRegion(@RequestParam String region) {
        return weatherService.getByRegion(region);
    }

    @GetMapping
    @Operation(summary = "Все опасные погодные явления из статистики")
    List<String> getWeatherFacts() {
        return weatherService.getWeatherFacts();
    }

    @GetMapping("days/action")
    @Operation(summary = "Дни региона с наблюдаемым погодным явлением")
    List<DangerousResponse> getWorstActionDaysByRegion(@RequestParam String region, @RequestParam ActionType action) {
        return weatherService.getWorstActionDaysByRegion(region, action);
    }

    @GetMapping("dataset")
    @Operation(summary = "Информация для датасета")
    List<DangerousResponse> getDataSetInfo() {
        return weatherService.getDataSetInfo();
    }

    @PostMapping("predict")
    @Operation(summary = "Предсказать погоду")
    @GeoValid
    double predictWeather(@RequestParam String region, @RequestParam String date) {
        return logisticService.getWeather(region, date);
    }

    @PostMapping("predict/check")
    @Operation(summary = "Проверить погоду")
    DangerousResponse checkPredictWeather(@RequestParam String region, @RequestParam String date) {
        return weatherService.checkPredictWeather(region, date);
    }

    @PostMapping("predict/v2")
    @Operation(summary = "Предсказать погоду v2")
    @GeoValid
    double predictWeatherV2(@RequestParam String region, @RequestParam String date) {
        return logisticService.getWeatherV2(region, date);
    }
}
