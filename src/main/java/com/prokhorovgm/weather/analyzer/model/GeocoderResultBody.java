package com.prokhorovgm.weather.analyzer.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GeocoderResultBody {
    List<GeocoderResult> results;
}
