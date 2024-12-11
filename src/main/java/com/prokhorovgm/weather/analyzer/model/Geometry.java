package com.prokhorovgm.weather.analyzer.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Geometry {
    double lat;
    double lng;
}
