package com.prokhorovgm.weather.analyzer.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DataSetRaw {
    double lat;
    double lon;
    boolean isCataclysm;
    double normalizeDay;
}
