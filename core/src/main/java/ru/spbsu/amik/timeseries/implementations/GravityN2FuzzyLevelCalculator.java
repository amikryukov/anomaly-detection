package ru.spbsu.amik.timeseries.implementations;

import ru.spbsu.amik.timeseries.api.FuzzyLevelCalculator;

import java.util.Map;

/**
 * Simple implementation of FuzzyLevelCalculator that implements equation n2(a, grA) = b
 * where grA is gravitation of A, b - level of extremal, a - value to find out
 * b = 1/2 in current implementation
 * todo : make b configurable
 */
public class GravityN2FuzzyLevelCalculator implements FuzzyLevelCalculator {

    @Override
    public double calculate(Map<? extends Number, ? extends Number> weightedSet, double extremalLevel) {

        assert extremalLevel >= -1 && extremalLevel <= 1;

        // используя гравитационное расширение нечетких сравнений найдем центр тяжести всей совокупности.
        double sumOfRectifications = 0;
        for (Map.Entry<? extends Number, ? extends Number> entry : weightedSet.entrySet()) {
            sumOfRectifications += entry.getKey().doubleValue() * entry.getValue().doubleValue();
        }
        double mediana = sumOfRectifications / weightedSet.size();

        // сравнивая медиану с искомым вертикальным уровнем, должны получить, что уровень сильно больше ,
        // тоесть n ( mediana. verticalLevel) = 0.5
        // возьмем простое сравнение n(a, b) = (b - a) / (a^2 + b^2)^0.5
        // есть решение на бумажке
        return mediana * (8 + Math.sqrt(28)) / 6;
    }
}
