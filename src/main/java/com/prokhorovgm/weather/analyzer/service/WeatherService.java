package com.prokhorovgm.weather.analyzer.service;

import com.prokhorovgm.weather.analyzer.model.ActionType;
import com.prokhorovgm.weather.analyzer.model.CellType;
import com.prokhorovgm.weather.analyzer.model.DangerousResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.StringUtil;
import org.javatuples.Quintet;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class WeatherService {
    private static final double NOT_EXPECTED = 9999;
    private static final double DS_END_YEAR = 2015;
    public static final String DATE_FORMAT = "dd.MM.yyyy";
    private static final String SIMPLE_DATE_FORMAT = "dd.MM";

    DataReader dataReader;

    public List<String> getRegions() {
        return distinct(dataReader.getValues(CellType.PLACE.getNumber()));
    }

    public List<String> getByRegion(String region) {
        return getAllInformation(getRowsByRegion(region)).stream()
            .map(DangerousResponse::getDay)
            .toList();
    }

    public List<String> getWeatherFacts() {
        return distinct(dataReader.getValues(CellType.NAME.getNumber()));
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
        return prepareAllInfo(rows.map(this::formQuintet));
    }

    private Pair<Integer, String> getDayFormatMap(LocalDate localDate) {
        String formattedDate = localDate.format(DateTimeFormatter.ofPattern(SIMPLE_DATE_FORMAT));
        return Pair.of(localDate.getDayOfYear(), formattedDate);
    }

    private List<String> distinct(List<String> data) {
        return data.stream().distinct().filter(StringUtil::isNotBlank).sorted().toList();
    }

    public List<DangerousResponse> getDataSetInfo() {
        return prepareAllInfo(dataReader.getRows().stream()
            .filter(row -> !Objects.equals(row.getCell(CellType.RANGE.getNumber()).getNumericCellValue(), NOT_EXPECTED))
            .map(this::formQuintet)
            .filter(Objects::nonNull)
        );
    }

    private List<DangerousResponse> prepareAllInfo(Stream<Quintet<LocalDate, LocalDate, Integer, String, String>> rows) {
        return rows
            .filter(q -> q.getValue1().getYear() < DS_END_YEAR)
            .flatMap(this::responseByDate)
            .collect(Collectors.groupingBy(DangerousResponse::getDay)).entrySet().stream()
            .flatMap(e -> {
                String day = e.getKey();
                int frequency = e.getValue().stream()
                    .map(DangerousResponse::getYearsInARow)
                    .reduce(Integer::sum)
                    .orElse(0);
                Set<String> facts = e.getValue().stream()
                    .map(DangerousResponse::getFacts)
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());
                int dayNumInYear = e.getValue().stream()
                    .map(DangerousResponse::getDayNumberInAYear)
                    .max(Integer::compare)
                    .orElse(0);

                return e.getValue().stream()
                    .map(DangerousResponse::getRegion)
                    .filter(StringUtil::isNotBlank)
                    .map(r -> DangerousResponse.create(day, dayNumInYear, frequency, facts, r));
            })
            .sorted(Comparator.comparingInt(DangerousResponse::getDayNumberInAYear))
            .toList();
    }

    private Quintet<LocalDate, LocalDate, Integer, String, String> formQuintet(Row row) {
        try {
            return Quintet.with(
                LocalDate.parse(row.getCell(CellType.DATE_START.getNumber()).getRichStringCellValue().getString(), DateTimeFormatter.ofPattern(DATE_FORMAT)),
                LocalDate.parse(row.getCell(CellType.DATE_END.getNumber()).getRichStringCellValue().getString(), DateTimeFormatter.ofPattern(DATE_FORMAT)),
                (int) row.getCell(CellType.COUNT.getNumber()).getNumericCellValue(),
                row.getCell(CellType.NAME.getNumber()).getRichStringCellValue().getString(),
                row.getCell(CellType.PLACE.getNumber()).getRichStringCellValue().getString()
            );
        } catch (Exception e) {
            return null;
        }
    }

    public DangerousResponse checkPredictWeather(String region, String date) {
        LocalDate predictDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(DATE_FORMAT));
        return dataReader.getRows().stream()
            .filter(row -> !Objects.equals(row.getCell(CellType.RANGE.getNumber()).getNumericCellValue(), NOT_EXPECTED))
            .map(this::formQuintet)
            .filter(Objects::nonNull)
            .filter(q -> StringUtils.containsIgnoreCase(q.getValue4(), region))
            .filter(q -> Objects.nonNull(q.getValue0()))
            .filter(q -> Objects.equals(q.getValue0().getYear(), predictDate.getYear()))
            .filter(q -> Objects.equals(q.getValue1().getYear(), predictDate.getYear()))
            .flatMap(this::responseByDate)
            .filter(res -> Objects.equals(res.getDayNumberInAYear(), predictDate.getDayOfYear()))
            .findFirst()
            .orElse(null);
    }

    private Stream<DangerousResponse> responseByDate(Quintet<LocalDate, LocalDate, Integer, String, String> quintet) {
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
    }

    public double getUtils(String region, String date) {
        if (checkPredictWeather(region, date) != null)  return 1;
        else return 0;
    }
}
