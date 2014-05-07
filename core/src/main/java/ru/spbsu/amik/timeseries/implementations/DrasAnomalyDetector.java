package ru.spbsu.amik.timeseries.implementations;

import ru.spbsu.amik.timeseries.api.AnomalyDetector;
import ru.spbsu.amik.timeseries.model.Anomaly;
import ru.spbsu.amik.timeseries.model.Curve;
import ru.spbsu.amik.timeseries.model.Point;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Detector that implements DRAS algorithm of anomaly detection
 */
public class DrasAnomalyDetector implements AnomalyDetector {

    private double verticalBackgroundLevel;

    /** should be much more then localOverviewCount */
    private int globalOverviewCount;

    /** in interval (0, 1] */
    private double horizontalBackgroundLevel;

    public void setVerticalBackgroundLevel(double verticalBackgroundLevel) {
        this.verticalBackgroundLevel = verticalBackgroundLevel;
    }

    public void setGlobalOverviewCount(int globalOverviewCount) {
        this.globalOverviewCount = globalOverviewCount;
    }

    public void setHorizontalBackgroundLevel(double horizontalBackgroundLevel) {
        this.horizontalBackgroundLevel = horizontalBackgroundLevel;
    }

    @Override
    public List<Anomaly> detectAnomalies(Curve rectification) {

        return detectAnomalies(rectification, verticalBackgroundLevel);
    }

    public List<Anomaly> detectAnomaliesCalculatingVL(Curve rectification) {

        double verticalLevel = 0;
        // используя гравитационное расширение нечетких сравнений найдем центр тяжести всей совокупности.
        double sumOfRectifications = 0;
        for (Point point : rectification.getPoints()) {
            sumOfRectifications += point.getValue();
        }
        double mediana = sumOfRectifications / rectification.getPoints().size();

        System.out.println("mediana : " + mediana);
        // сравнивая медиану с искомым вертикальным уровнем, должны получить, что уровень сильно больше ,
        // тоесть n ( mediana. verticalLevel) = 0.5
        // возьмем простое сравнение n(a, b) = (b - a) / (a^2 + b^2)^0.5
        // есть решение на бумажке
        verticalLevel = mediana * (8 + Math.sqrt(28)) / 6;
        System.out.println("vertical level : " + verticalLevel);

        return detectAnomalies(rectification, verticalLevel);
    }


    private List<Anomaly> detectAnomalies(Curve rectification, double verticalBackgroundLevel) {

        List<Anomaly> anomalies = new ArrayList<Anomaly>();
        List<Point> points = rectification.getPoints();

        Point startAnomaly = null;
        Point endAnomaly = null;
        // value - is difference of left and right measures
        Curve potentialAnomalies = new Curve();
        for (int i = 0 ; i < points.size(); i ++) {
            double leftMeasure = leftSideMeasure(verticalBackgroundLevel, globalOverviewCount, i, points);
            double rightMeasure = rightSideMeasure(verticalBackgroundLevel, globalOverviewCount, i, points);
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
    private double leftSideMeasure(double alpha, int lyambda, int currentPoint, List<Point> rectification) {

        if (currentPoint < lyambda)
            lyambda = currentPoint;

        return intervalMeasure(alpha, lyambda, currentPoint, rectification, currentPoint - lyambda, currentPoint);
    }

    /** Measures ratio of rectifications witch level more then alpha on right of current point */
    private double rightSideMeasure(double alpha, int lyambda, int currentPoint, List<Point> rectification) {

        // ?? some errors can occur here
        if (rectification.size() - lyambda <= currentPoint)
            lyambda = rectification.size() - currentPoint - 1;

        return intervalMeasure(alpha, lyambda, currentPoint, rectification, currentPoint, currentPoint + lyambda);
    }

    /** Measures ratio of rectifications witch level more then alpha on interval */
    private double intervalMeasure(double alpha, int lyambda, int currentPoint, List<Point> rectification, int start, int end) {

        double numerator = 0;
        double denominator = 0;
        for (int i = start; i <= end; i++) {

            double weight = weightFunction(lyambda, currentPoint, i);
            if (rectification.get(i).getValue() <= alpha)
                numerator += weight;
            denominator += weight;
        }

        return denominator == 0 ? 0 : numerator / denominator;
    }

    /** used in one side measures */
    private double weightFunction (int globalOverviewCount, int currentPoint, int point) {

        return globalOverviewCount + 1 - Math.abs(point - currentPoint)
                      / (globalOverviewCount + 1);
    }


    /** additional class to find anomalies on potential anomalies set */
    private class DifferenceAnomalyDetector {

    }
}
