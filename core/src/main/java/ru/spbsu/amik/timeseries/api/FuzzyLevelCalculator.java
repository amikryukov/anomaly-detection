package ru.spbsu.amik.timeseries.api;

import java.util.Map;

/**
 * Class that can solve equation as n(a, A) = b or n(A, a) = -b which is the same
 */
public interface FuzzyLevelCalculator {

    /**
     * find value(a) that is solution of equation n(a, A) = extremalLevel;
     * @param weightedSet Set, each element has weight
     * @param extremalLevel extremal level
     * @return a -value.
     */
    public double calculate (Map<? extends Number, ? extends Number> weightedSet, double extremalLevel);
}
