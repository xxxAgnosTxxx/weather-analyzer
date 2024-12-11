package com.prokhorovgm.weather.analyzer.aop;

import com.prokhorovgm.weather.analyzer.service.WeatherService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GeoHandler {
    WeatherService weatherService;

    @Around("@annotation(com.prokhorovgm.weather.analyzer.aop.GeoValid) && args(region, date)")
    public double validate(ProceedingJoinPoint joinPoint, String region, String date) throws Throwable {
        joinPoint.proceed();
        return weatherService.getUtils(region, date);
    }
}
