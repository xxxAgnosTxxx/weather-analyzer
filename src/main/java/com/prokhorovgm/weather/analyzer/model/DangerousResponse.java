package com.prokhorovgm.weather.analyzer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class DangerousResponse {
    String day;
    @JsonIgnore
    int dayNumberInAYear;
    int yearsInARow;
    Set<String> facts;
    String region;

    public static DangerousResponse create(String day, int dayNumberInAYear, int frequency, Set<String> facts, String region) {
        return new DangerousResponse(day, dayNumberInAYear, frequency, facts, region);
    }
}
