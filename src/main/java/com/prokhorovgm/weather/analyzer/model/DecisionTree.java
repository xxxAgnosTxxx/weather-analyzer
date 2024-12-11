package com.prokhorovgm.weather.analyzer.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DecisionTree {

    static class Node {
        int featureIndex;
        double threshold;
        Node left;
        Node right;
        Integer predictedClass;

        public Node(int featureIndex, double threshold) {
            this.featureIndex = featureIndex;
            this.threshold = threshold;
        }

        public Node(int predictedClass) {
            this.predictedClass = predictedClass;
        }

        public boolean isLeaf() {
            return predictedClass != null;
        }
    }

    private Node root;

    public void train(double[][] X, int[] y) {
        if (Objects.isNull(root))   root = buildTree(X, y, 0);
    }

    private Node buildTree(double[][] X, int[] y, int depth) {
        if (allSame(y) || depth >= 15 || X.length == 0) {
            int predictedClass = mostCommonClass(y);
            return new Node(predictedClass);
        }

        int bestFeature = -1;
        double bestThreshold = Double.MAX_VALUE;
        double bestGini = Double.MAX_VALUE;

        for (int featureIndex = 0; featureIndex < X[0].length; featureIndex++) {
            double[] values = getUniqueValues(X, featureIndex);
            for (double threshold : values) {
                double gini = calculateGini(X, y, featureIndex, threshold);
                if (gini < bestGini) {
                    bestGini = gini;
                    bestFeature = featureIndex;
                    bestThreshold = threshold;
                }
            }
        }

        Object[][] splitData = split(X, y, bestFeature, bestThreshold);
        double[][] leftX = (double[][]) splitData[0][0];
        int[] leftY = (int[]) splitData[0][1];
        double[][] rightX = (double[][]) splitData[1][0];
        int[] rightY = (int[]) splitData[1][1];

        if (leftX.length == 0 || rightX.length == 0) {
            int predictedClass = mostCommonClass(y);
            return new Node(predictedClass);
        }

        Node node = new Node(bestFeature, bestThreshold);
        node.left = buildTree(leftX, leftY, depth + 1);
        node.right = buildTree(rightX, rightY, depth + 1);
        return node;
    }

    public int predict(double[] x) {
        Node node = root;
        while (!node.isLeaf()) {
            if (x[node.featureIndex] < node.threshold) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        return node.predictedClass;
    }

    private Object[][] split(double[][] X, int[] y, int featureIndex, double threshold) {
        List<double[]> leftX = new ArrayList<>();
        List<double[]> rightX = new ArrayList<>();
        List<Integer> leftY = new ArrayList<>();
        List<Integer> rightY = new ArrayList<>();

        for (int i = 0; i < X.length; i++) {
            if (X[i][featureIndex] < threshold) {
                leftX.add(X[i]);
                leftY.add(y[i]);
            } else {
                rightX.add(X[i]);
                rightY.add(y[i]);
            }
        }

        double[][] leftXArray = leftX.toArray(new double[0][]);
        double[][] rightXArray = rightX.toArray(new double[0][]);
        int[] leftYArray = leftY.stream().mapToInt(Integer::intValue).toArray();
        int[] rightYArray = rightY.stream().mapToInt(Integer::intValue).toArray();

        return new Object[][] { { leftXArray, leftYArray }, { rightXArray, rightYArray } };
    }

    private boolean allSame(int[] y) {
        for (int i = 1; i < y.length; i++) {
            if (y[i] != y[0]) return false;
        }
        return true;
    }

    private int mostCommonClass(int[] y) {
        int count0 = 0, count1 = 0;
        for (int label : y) {
            if (label == 0) count0++;
            else count1++;
        }
        return count0 > count1 ? 0 : 1;
    }

    private double[] getUniqueValues(double[][] X, int featureIndex) {
        List<Double> uniqueValues = new ArrayList<>();
        for (double[] row : X) {
            if (!uniqueValues.contains(row[featureIndex])) {
                uniqueValues.add(row[featureIndex]);
            }
        }
        return uniqueValues.stream().mapToDouble(Double::doubleValue).toArray();
    }

    private double calculateGini(double[][] X, int[] y, int featureIndex, double threshold) {
        Object[][] splitData = split(X, y, featureIndex, threshold);
        int[] leftY = (int[]) splitData[0][1];
        int[] rightY = (int[]) splitData[1][1];

        double leftGini = giniImpurity(leftY);
        double rightGini = giniImpurity(rightY);

        return leftGini + rightGini;
    }

    private double giniImpurity(int[] y) {
        int total = y.length;
        int countClass0 = 0;
        int countClass1 = 0;

        for (int label : y) {
            if (label == 0) countClass0++;
            else countClass1++;
        }

        double p0 = (double) countClass0 / total;
        double p1 = (double) countClass1 / total;

        return 1.0 - (p0 * p0 + p1 * p1);
    }
}
