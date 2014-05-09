package ru.spbsu.amik.timeseries.api;

import ru.spbsu.amik.timeseries.model.Point;

import java.util.Map;

/**
 * Fuzzy comparison of number and weighted set
 */
public interface ExtendedFuzzyComparison {

    /**
     * n(a, A)
     * @param value value to compare to set
     * @param weightedSet each value(key in map) have some weight(value in map)
     * @return result of comparison
     */
    public double compare(double value, Map<Point, Double> weightedSet);

    /**
     * n(A, a)
     * @param value value to compare to set
     * @param weightedSet each value(key in map) have some weight(value in map)
     * @return result of comparison
     */
    public double compare(Map<Point, Double> weightedSet, double value);
}
