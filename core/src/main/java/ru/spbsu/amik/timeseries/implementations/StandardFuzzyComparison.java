package ru.spbsu.amik.timeseries.implementations;

import ru.spbsu.amik.timeseries.api.FuzzyComparison;

/**
 * n2(a, b) = (b - a) / (b^v + a^v)^(1/v)
 */
public class StandardFuzzyComparison implements FuzzyComparison {

    // by default v = 2;
    private double v = 2;

    public void setV(double v) {
        this.v = v;
    }

    @Override
    public double compare(double a, double b) {
        return (b - a) /
            Math.pow(Math.pow(b, v)  + Math.pow(a, v), 1 / v);
    }
}
