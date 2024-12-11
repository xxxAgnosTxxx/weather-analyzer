package com.prokhorovgm.weather.analyzer.service;

import com.prokhorovgm.weather.analyzer.model.CoordinatesUtils;
import com.prokhorovgm.weather.analyzer.model.DecisionTree;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class LogisticService {
    GeocoderCallService geocoderCallService;
    WeatherService weatherService;
    DecisionTree decisionTree = new DecisionTree();

    double[] weights = new double[4];//{-3.016039497997957, -1.2223692714771357, -0.9296898816208209, -1.1307379458979652};
    double learningRate = 0.0001;
    int epochs = 10_000;

    private void learn() {
        if (weights[0] == 0) {
            initializeWeights();
        }
        if (CoordinatesUtils.regionCoordinates.isEmpty()) {
            weatherService.getRegions().forEach(this::getCoordinates);
        }
        Pair<double[][], int[]> ds = prepareDataSet();
        train(ds.getLeft(), ds.getRight());

    }

    private void getCoordinates(String region) {
        CoordinatesUtils.regionCoordinates.putAll(geocoderCallService.getCoordinates(region));
    }

    private double normalizeDayOfYear(int dayOfYear) {
        int min = 1;
        double max = 365.25;
        return (double) (dayOfYear - min) / (max - min);
    }

    private static double normalizeLat(double latitude) {
        double latMin = 41.2;
        double latMax = 81.9;
        return (latitude - latMin) / (latMax - latMin);
    }

    private static double normalizeLon(double longitude) {
        double lonMin = 19.6;
        double lonMax = 180.0;
        return (longitude - lonMin) / (lonMax - lonMin);
    }

    private void initializeWeights() {
        Random rand = new Random();
        for (int i = 0; i < weights.length; i++) {
            weights[i] = rand.nextDouble() - 0.5;
        }
    }

    private Pair<double[][], int[]> prepareDataSet() {
        List<double[]> emptyDataSet = new ArrayList<>();
        for (int i = 0; i <= 365; i++) {
            int finalI = i;
            CoordinatesUtils.regionCoordinates.forEach((key, value) -> emptyDataSet.add(new double[]{value[0], value[1], finalI}));
        }

        //double[]{lat, lon, day}
        List<double[]> wetDataSet = weatherService.getDataSetInfo().stream()
            .distinct()
            .map(dr -> {
                double[] preparedDS = new double[3];
                preparedDS[0] = CoordinatesUtils.regionCoordinates.get(dr.getRegion())[0];
                preparedDS[1] = CoordinatesUtils.regionCoordinates.get(dr.getRegion())[1];
                preparedDS[2] = dr.getDayNumberInAYear();
                return preparedDS;
            })
            .toList();

        int[] res = new int[emptyDataSet.size()];
        double[][] ds = new double[emptyDataSet.size()][3];
        for (int i = 0; i < emptyDataSet.size(); i++) {
            double[] tmp = emptyDataSet.get(i);
            if (wetDataSet.stream().anyMatch(darr -> darr[0] == tmp[0] && darr[1] == tmp[1] && darr[2] == tmp[2])) {
                res[i] = 1;
            } else res[i] = 0;

            ds[i][0] = normalizeLat(tmp[0]);
            ds[i][1] = normalizeLon(tmp[1]);
            ds[i][2] = normalizeDayOfYear((int) tmp[2]);
        }

        for (int i = 1; i < res.length; i++) {
            if (res[i] == 1) {
                res[i-1] = 1;
            }
        }

        return Pair.of(ds, res);
    }

    private double sigmoid(double z) {
        return  1.0 / (1.0 + Math.exp(-z));
    }

    private double predict(double[] features) {
        double z = weights[0];
        for (int i = 0; i < features.length; i++) {
            z += weights[i + 1] * features[i];
        }
        return sigmoid(z);
    }

    private void train(double[][] X, int[] y) {
        int m = X.length;

        for (int epoch = 0; epoch < epochs; epoch++) {
            double[] gradients = new double[weights.length];

            for (int i = 0; i < m; i++) {
                double[] features = X[i];
                int label = y[i];

                double prediction = predict(features);
                double error = prediction - label;

                gradients[0] += error;
                for (int j = 0; j < features.length; j++) {
                    gradients[j + 1] += error * features[j];
                }
            }

            for (int j = 0; j < weights.length; j++) {
                weights[j] -= learningRate * gradients[j] / m;
            }
        }
    }

    public double getWeather(String region, String date) {
        double dayOfYear = normalizeDayOfYear(LocalDate.parse(date, DateTimeFormatter.ofPattern(WeatherService.DATE_FORMAT)).getDayOfYear());
        String refRegion = weatherService.getRegions().stream()
            .filter(r -> r.contains(region))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Регион не найден"));

        learn();

        double[] coords = CoordinatesUtils.regionCoordinates.get(refRegion);
        double[] inputData = new double[]{normalizeLat(coords[0]), normalizeLon(coords[1]), dayOfYear};
        return predict(inputData);
    }

    public double getWeatherV2(String region, String date) {
        double dayOfYear = normalizeDayOfYear(LocalDate.parse(date, DateTimeFormatter.ofPattern(WeatherService.DATE_FORMAT)).getDayOfYear());
        String refRegion = weatherService.getRegions().stream()
            .filter(r -> r.contains(region))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Регион не найден"));

        Pair<double[][], int[]> ds = prepareDataSet();
        decisionTree.train(ds.getLeft(), ds.getRight());

        double[] coords = CoordinatesUtils.regionCoordinates.get(refRegion);
        double[] inputData = new double[]{normalizeLat(coords[0]), normalizeLon(coords[1]), dayOfYear};
        return decisionTree.predict(inputData);
    }
}
