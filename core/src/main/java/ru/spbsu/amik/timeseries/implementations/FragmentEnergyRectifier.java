package ru.spbsu.amik.timeseries.implementations;

import ru.spbsu.amik.timeseries.api.Rectifier;
import ru.spbsu.amik.timeseries.model.Point;

import java.util.List;

/**
 * Should be used only with the same time steps for all values
 */
public class FragmentEnergyRectifier implements Rectifier {

    // tell how many points fet for local fragment;
    private int fragmentSize;
    double multiplier;

    public FragmentEnergyRectifier(int fragmentSize) {
        this.fragmentSize = fragmentSize;
        this.multiplier = 1d / (2 * fragmentSize + 1d);
    }

    public int getFragmentSize() {
        return fragmentSize;
    }

    public void setFragmentSize(int fragmentSize) {
        this.fragmentSize = fragmentSize;
        this.multiplier = 1d / (2 * fragmentSize + 1d);
    }

    @Override
    public double rectify(List<Point> survey) {

        double weightedSum = getWeightedSum(survey);
        double result = 0;
        for (Point point : survey) {
            result += Math.pow(point.getValue() - weightedSum, 2);
        }

        return result;
    }

    private double getWeightedSum(List<Point> survey) {

        double sum = 0;
        for (Point point : survey) {
            sum += point.getValue();
        }

        return multiplier * sum;
    }
}
