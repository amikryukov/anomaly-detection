package ru.spbsu.amik.timeseries.api;

import ru.spbsu.amik.timeseries.model.Point;

import java.util.List;

/**
 * Get fragments of timeSeries and return double value in interval [0,1]
 */
public interface Rectifier {

    public double rectify(List<Point> survey);
}
