package com.prokhorovgm.weather.analyzer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prokhorovgm.weather.analyzer.model.GeocoderResult;
import com.prokhorovgm.weather.analyzer.model.GeocoderResultBody;
import com.prokhorovgm.weather.analyzer.configuration.GeocodeConfiguration;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.util.UriEncoder;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class GeocoderCallService {
    public static String url = "https://api.opencagedata.com/geocode/v1/json?q=%s&key=%s";

    GeocodeConfiguration configuration;
    ObjectMapper objectMapper;

    public Map<String, double[]> getCoordinates(String query) {
        RestTemplate restTemplate = new RestTemplate();

        URI uri = URI.create(String.format(url, UriEncoder.encode(query), configuration.getApiKey()));

        ResponseEntity<GeocoderResultBody> result = restTemplate.getForEntity(uri, GeocoderResultBody.class);
        try {
            log.info("Get geocode body: {}", objectMapper.writeValueAsString(result));
        } catch (JsonProcessingException e) {
            log.error("Cannot convert json to string for query: {}", query);
        }

        return Optional.ofNullable(result.getBody())
            .map(GeocoderResultBody::getResults).stream()
            .flatMap(Collection::stream)
            .findFirst()
            .map(GeocoderResult::getGeometry)
            .map(g -> Map.of(query, new double[]{g.getLat(), g.getLng()}))
            .orElse(Map.of(query, new double[]{60, 100}));
    }
}
