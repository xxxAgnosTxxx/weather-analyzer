package com.prokhorovgm.weather_analyzer.service;

import com.prokhorovgm.weather_analyzer.model.ActionType;
import com.prokhorovgm.weather_analyzer.model.CellType;
import com.prokhorovgm.weather_analyzer.model.DangerousResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.StringUtil;
import org.javatuples.Quintet;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class WeatherService {
    private static final double NOT_EXPECTED = 9999;
    private static final String DATE_FORMAT = "dd.MM.yyyy";
    private static final String SIMPLE_DATE_FORMAT = "dd.MM";

    DataReader dataReader;

    public List<String> getRegions() {
        return dataReader.getValues(CellType.PLACE.getNumber());
    }

    public List<String> getByRegion(String region) {
        return getAllInformation(getRowsByRegion(region)).stream()
            .map(DangerousResponse::getDay)
            .toList();
    }

    public List<String> getWeatherFacts() {
        return dataReader.getValues(CellType.NAME.getNumber());
    }

    public List<DangerousResponse> getWorstActionDaysByRegion(String region, ActionType action) {
        return getAllInformation(getRowsByRegion(region)
            .filter(row -> action.getFact().contains(
                row.getCell(CellType.NAME.getNumber()).getRichStringCellValue().getString()
            )));
    }

    private Stream<Row> getRowsByRegion(String region) {
        return dataReader.getRows().stream()
            .filter(row -> !Objects.equals(row.getCell(CellType.RANGE.getNumber()).getNumericCellValue(), NOT_EXPECTED))
            .filter(row -> row.getCell(CellType.PLACE.getNumber()).getRichStringCellValue().getString().contains(region));
    }

    private List<DangerousResponse> getAllInformation(Stream<Row> rows) {
        return rows.map(row -> Quintet.with(
                row.getCell(CellType.DATE_START.getNumber()).getRichStringCellValue().getString(),
                row.getCell(CellType.DATE_END.getNumber()).getRichStringCellValue().getString(),
                (int) row.getCell(CellType.COUNT.getNumber()).getNumericCellValue(),
                row.getCell(CellType.NAME.getNumber()).getRichStringCellValue().getString(),
                row.getCell(CellType.PLACE.getNumber()).getRichStringCellValue().getString()
            ))
            .map(quintet -> quintet.setAt0(LocalDate.parse(quintet.getValue0(), DateTimeFormatter.ofPattern(DATE_FORMAT))))
            .map(quintet -> quintet.setAt1(LocalDate.parse(quintet.getValue1(), DateTimeFormatter.ofPattern(DATE_FORMAT))))
            .flatMap(quintet -> {
                List<LocalDate> dates = new ArrayList<>();
                LocalDate temp = quintet.getValue0();

                do {
                    dates.add(temp);
                    temp = temp.plusDays(1);
                } while (!temp.isAfter(quintet.getValue1()));

                return dates.stream()
                    .map(this::getDayFormatMap)
                    .map(pair -> DangerousResponse.create(
                        pair.getRight(), pair.getLeft(), quintet.getValue2(), Set.of(quintet.getValue3()), quintet.getValue4()
                    ));
            })
            .collect(Collectors.groupingBy(DangerousResponse::getDay)).entrySet().stream()
            .map(e -> {
                String day = e.getKey();
                int frequency = e.getValue().stream()
                    .map(DangerousResponse::getYearsInARow)
                    .reduce(Integer::sum)
                    .orElse(0);
                Set<String> facts = e.getValue().stream()
                    .map(DangerousResponse::getFacts)
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());
                String region = e.getValue().stream()
                    .map(DangerousResponse::getRegion)
                    .filter(StringUtil::isNotBlank)
                    .findFirst()
                    .orElse(null);
                int dayNumInYear = e.getValue().stream()
                    .map(DangerousResponse::getDayNumberInAYear)
                    .max(Integer::compare)
                    .orElse(0);
                return DangerousResponse.create(day, dayNumInYear, frequency, facts, region);
            })
            .filter(dr -> dr.getYearsInARow() > 1)
            .sorted(Comparator.comparingInt(DangerousResponse::getDayNumberInAYear))
            .toList();
    }

    private Pair<Integer, String> getDayFormatMap(LocalDate localDate) {
        String formattedDate = localDate.format(DateTimeFormatter.ofPattern(SIMPLE_DATE_FORMAT));
        return Pair.of(localDate.getDayOfYear(), formattedDate);
    }
}
