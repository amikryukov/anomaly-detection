package ru.spbsu.amik.timeseries.api;


/**
 * Fuzzy comparison of 2 numbers
 */
public interface FuzzyComparison {

    public double compare(double a, double b);
}
