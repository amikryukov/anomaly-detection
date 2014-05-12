package ru.spbsu.amik.timeseries.implementations;

import ru.spbsu.amik.timeseries.api.AnomalyDetector;
import ru.spbsu.amik.timeseries.model.Anomaly;
import ru.spbsu.amik.timeseries.model.Curve;
import ru.spbsu.amik.timeseries.model.Point;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractDrasAnomalyDetector<T extends Number> implements AnomalyDetector {

    /** should be much more then localOverview of rectification */
    protected T globalOverview;

    /** in interval (0, 1] */
    protected double horizontalBackgroundLevel;

    public void setHorizontalBackgroundLevel(double horizontalBackgroundLevel) {
        this.horizontalBackgroundLevel = horizontalBackgroundLevel;
    }

    public void setGlobalOverview(T globalOverview) {
        this.globalOverview = globalOverview;
    }

    /**
     * Detect anomalies on rectification
     * @param rectification rectification of curve
     * @return List of anomalies
     */
    @Override
    public List<Anomaly> detectAnomalies(Curve rectification) {

        double verticalLevel = calculateVerticalLevel(rectification);

        return detectAnomalies(rectification, verticalLevel);
    }

    /**
     * Calculate vertical extremal level for rectification
     * todo : currently this Class tied to one fuzzy comparison. Should be refactored in next version.
     * @param rectification rectification of curve
     * @return extremal vertical level
     */
    protected double calculateVerticalLevel(Curve rectification) {

        // используя гравитационное расширение нечетких сравнений найдем центр тяжести всей совокупности.
        double sumOfRectifications = 0;
        for (Point point : rectification.getPoints()) {
            sumOfRectifications += point.getValue();
        }
        double mediana = sumOfRectifications / rectification.getPoints().size();

        // сравнивая медиану с искомым вертикальным уровнем, должны получить, что уровень сильно больше ,
        // тоесть n ( mediana. verticalLevel) = 0.5
        // возьмем простое сравнение n(a, b) = (b - a) / (a^2 + b^2)^0.5
        // есть решение на бумажке
        return mediana * (8 + Math.sqrt(28)) / 6;
    }


    private List<Anomaly> detectAnomalies(Curve rectification, double verticalBackgroundLevel) {

        List<Anomaly> anomalies = new ArrayList<Anomaly>();
        List<Point> points = rectification.getPoints();

        Point startAnomaly = null;
        Point endAnomaly = null;
        // value - is difference of left and right measures
        Curve potentialAnomalies = new Curve();
        for (int i = 0 ; i < points.size(); i ++) {
            double leftMeasure = leftSideMeasure(verticalBackgroundLevel, globalOverview, i, points);
            double rightMeasure = rightSideMeasure(verticalBackgroundLevel, globalOverview, i, points);
            double curDifference = leftMeasure - rightMeasure;

            // potentially anomaly
            if (Math.min(leftMeasure, rightMeasure) < horizontalBackgroundLevel) {
                if (startAnomaly == null) {
                    startAnomaly = points.get(i);
                }
                endAnomaly = points.get(i);
                potentialAnomalies.addPoint(new Point(endAnomaly.getTime(), curDifference));
            } else {
                if (startAnomaly != null) {
                    anomalies.add(new Anomaly(startAnomaly.getTime(), endAnomaly.getTime(), Anomaly.AnomalyLevel.POTENTIAL));
                    anomalies.addAll(findAnomaliesInPotentialSet(potentialAnomalies));
                    startAnomaly = null;
                    potentialAnomalies.getPoints().clear();
                }
            }
        }

        return anomalies;
    }

    private Collection<? extends Anomaly> findAnomaliesInPotentialSet(Curve potentialAnomalies) {
        List<Anomaly> resultList = new ArrayList<Anomaly>();
        double difference = Double.MAX_VALUE;
        long startTime = 0;
        long endTime = 0;
        List<Point> points = potentialAnomalies.getPoints();
        int pointsCount = points.size();
        for (int i = 0; i < pointsCount; i++) {
            double currentDiff = points.get(i).getValue();

            while (currentDiff < 0 && i < pointsCount - 1) {
                if (currentDiff < difference) {
                    difference = currentDiff;
                    startTime = points.get(i).getTime();
                }
                currentDiff = points.get(++i).getValue();
            }

            while (currentDiff >= 0 && i < pointsCount - 1) {
                if (currentDiff > difference) {
                    difference = currentDiff;
                    endTime = points.get(i).getTime();
                }
                currentDiff = points.get(++i).getValue();
            }

            if (startTime != 0 && endTime != 0) {
                resultList.add(new Anomaly(startTime, endTime, Anomaly.AnomalyLevel.ANOMALY));
                startTime = 0; endTime = 0;
            }
        }
        return resultList;
    }


    /** Measures ratio of rectifications witch level more then alpha on left of current point */
    private double leftSideMeasure(double alpha, T globalOverview, int currentPoint, List<Point> rectification) {

        int startIndex = startIndexLeftSideMeasure(currentPoint, globalOverview, rectification);

        return intervalMeasure(alpha, globalOverview, currentPoint, rectification, startIndex, currentPoint);
    }

    /** Measures ratio of rectifications witch level more then alpha on right of current point */
    private double rightSideMeasure(double alpha, T globalOverview, int currentPoint, List<Point> rectification) {

        int endIndex = endIndexRightSideMeasure(currentPoint, globalOverview, rectification);

        return intervalMeasure(alpha, globalOverview, currentPoint, rectification, currentPoint, endIndex);
    }

    /** Measures ratio of rectifications witch level more then alpha on interval */
    private double intervalMeasure(double alpha, T globalOverview, int currentPoint, List<Point> rectification, int start, int end) {

        double numerator = 0;
        double denominator = 0;
        for (int i = start; i <= end; i++) {

            double weight = weightFunction(globalOverview, currentPoint, i, rectification);
            if (rectification.get(i).getValue() <= alpha)
                numerator += weight;
            denominator += weight;
        }

        return denominator == 0 ? 0 : numerator / denominator;
    }

    /** used in one side measures */
    protected abstract double weightFunction (T globalOverview, int currentPoint, int point, List<Point> rectification);

    protected abstract int startIndexLeftSideMeasure(int currentPoint, T globalOverview, List<Point> rectification);

    protected abstract int endIndexRightSideMeasure(int currentPoint, T globalOverview, List<Point> rectification);
}
