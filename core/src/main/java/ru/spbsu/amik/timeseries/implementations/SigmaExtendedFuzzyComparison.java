package ru.spbsu.amik.timeseries.implementations;


import ru.spbsu.amik.timeseries.api.ExtendedFuzzyComparison;
import ru.spbsu.amik.timeseries.api.FuzzyComparison;
import ru.spbsu.amik.timeseries.model.Point;

import java.util.Map;

public class SigmaExtendedFuzzyComparison implements ExtendedFuzzyComparison {

    private FuzzyComparison fuzzyComparison;

    public SigmaExtendedFuzzyComparison(FuzzyComparison fuzzyComparison) {
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

        double ql = 0;
        double qr = 0;

        for (Point point : weightedSet.keySet()) {
            double val = point.getValue();
            if (val > value) {
                qr += (val - value) * weightedSet.get(point);
            } else if (val < value) {
                ql += (value - val) * weightedSet.get(point);
            }
        }

        return more ? fuzzyComparison.compare(qr, ql) : fuzzyComparison.compare(ql, qr);
    }
}
