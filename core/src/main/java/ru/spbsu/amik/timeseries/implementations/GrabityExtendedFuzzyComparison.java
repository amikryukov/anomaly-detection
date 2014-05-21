package ru.spbsu.amik.timeseries.implementations;

import ru.spbsu.amik.timeseries.api.ExtendedFuzzyComparison;
import ru.spbsu.amik.timeseries.api.FuzzyComparison;
import ru.spbsu.amik.timeseries.model.Point;

import java.util.Map;

public class GrabityExtendedFuzzyComparison implements ExtendedFuzzyComparison {
    private FuzzyComparison fuzzyComparison;

    public GrabityExtendedFuzzyComparison(FuzzyComparison fuzzyComparison) {
        this.fuzzyComparison = fuzzyComparison;
    }

    public void setFuzzyComparison(FuzzyComparison fuzzyComparison) {
        this.fuzzyComparison = fuzzyComparison;
    }

    @Override
    public double compare(double value, Map<Point, Double> weightedSet) {

        return compare(value, weightedSet, false);
    }

    @Override
    public double compare(Map<Point, Double> weightedSet, double value) {
        return compare(value, weightedSet, true);
    }


    private double compare(double value, Map<Point, Double> weightedSet, boolean more) {

        double val = 0;
        double tt = 0;

        for (Point point : weightedSet.keySet()) {
            val += (point.getValue() * weightedSet.get(point));
            tt += weightedSet.get(point);
        }
        val = val / tt;

        return more ? fuzzyComparison.compare(val, value) : fuzzyComparison.compare(value, val);
    }
}