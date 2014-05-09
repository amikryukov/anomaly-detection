package ru.spbsu.amik.timeseries.implementations;

import ru.spbsu.amik.timeseries.model.Point;

import java.util.List;

public class DrasTAnomalyDetector extends AbstractDrasAnomalyDetector<Long> {

    @Override
    protected int startIndexLeftSideMeasure(int currentPoint, Long globalOverview, List<Point> rectification) {
        Point point = rectification.get(currentPoint);
        int startIndex = 0;

        for (int i = currentPoint; i >= 0 && rectification.get(i).getTime() >= point.getTime() - globalOverview / 2; i --) {
            startIndex = i;
        }

        return startIndex;
    }

    @Override
    protected int endIndexRightSideMeasure(int currentPoint, Long globalOverview, List<Point> rectification) {
        Point point = rectification.get(currentPoint);
        int endIndex = 0;

        for (int i = currentPoint; i < rectification.size() && rectification.get(i).getTime() <= point.getTime() + globalOverview / 2; i ++) {
            endIndex = i;
        }

        return endIndex;
    }

    @Override
    protected double weightFunction(Long globalOverview, int currentPoint, int point, List<Point> rectification) {

        long t1 = rectification.get(currentPoint).getTime();
        long t2 = rectification.get(point).getTime();

        return globalOverview + 1 - Math.abs(t1 - t2)
                / (globalOverview + 1);
    }
}
