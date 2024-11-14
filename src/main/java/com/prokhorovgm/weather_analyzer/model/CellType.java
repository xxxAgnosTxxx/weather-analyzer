package com.prokhorovgm.weather_analyzer.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Getter
public enum CellType {
    DATE_START(1),
    DATE_END(2),
    COUNT(3),
    NAME(5),
    RANGE(6),
    PLACE(7);

    int number;
}
