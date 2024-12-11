package com.prokhorovgm.weather.analyzer.configuration;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("geocode")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GeocodeConfiguration {
    String apiKey;
}
