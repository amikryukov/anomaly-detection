package ru.spbsu.amik.timeseries.implementations;

import ru.spbsu.amik.timeseries.model.Point;

import java.util.List;

public class DrasAnomalyDetector extends AbstractDrasAnomalyDetector<Integer> {

    @Override
    protected int startIndexLeftSideMeasure(int currentPoint, Integer globalOverview, List<Point> rectification) {

        int delta = globalOverview;
        if (currentPoint < delta)
            delta = currentPoint;

        return currentPoint - delta;
    }

    @Override
    protected int endIndexRightSideMeasure(int currentPoint, Integer globalOverview, List<Point> rectification) {

        int delta = globalOverview;
        if (rectification.size() - delta <= currentPoint)
            delta = rectification.size() - currentPoint - 1;

        return currentPoint + delta;
    }

    @Override
    protected double weightFunction(Integer globalOverview, int currentPoint, int point, List<Point> rectification) {
        return globalOverview + 1 - Math.abs(point - currentPoint)
                / (globalOverview + 1);
    }
}
