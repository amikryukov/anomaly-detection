package ru.spbsu.amik.timeseries.implementations;

import ru.spbsu.amik.timeseries.api.AnomalyDetector;
import ru.spbsu.amik.timeseries.model.Anomaly;
import ru.spbsu.amik.timeseries.model.Curve;
import ru.spbsu.amik.timeseries.model.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Detector that implements FLARS algorithm of anomaly detection
 */
public class FlarsAnomalyDetector implements AnomalyDetector {

    private int globalOverviewCount;

    // value in [0,1]. usually used values : 0.5, 0.75, 1
    private double verticalExtremalLevel = 0.5;

    public void setVerticalExtremalLevel(double verticalExtremalLevel) {
        this.verticalExtremalLevel = verticalExtremalLevel;
    }

    public void setGlobalOverviewCount(int globalOverviewCount) {
        this.globalOverviewCount = globalOverviewCount;
    }

    /**
     * Detect anomalies on rectification
     * @param rectification rectification of curve
     * @return List of anomalies
     */
    @Override
    public List<Anomaly> detectAnomalies(Curve rectification) {

        List<Anomaly> result = new ArrayList<Anomaly>();
        long startTime = -1;
        long endTime = -1;
        for (Point point : createMeasuresCurve(rectification).getPoints()) {
            if (point.getValue() > verticalExtremalLevel) {
                // anomaly

                if (startTime == -1) {
                    startTime = point.getTime();
                }
                endTime = point.getTime();
            } else {
                // not anomaly

                if (startTime != -1) {
                    result.add(new Anomaly(startTime, endTime, Anomaly.AnomalyLevel.ANOMALY));
                    startTime = -1;
                }
            }
        }

        return result;
    }

    /**
     * Create`s curve of measures mu(k) = n(ImF(k), F(k))
     * ImF(k) = {F(k), qk(k`)} , qk(k`) = 1 - |k - k`| / (max(|a-k|, |b-k|) + 1)
     * @param rectification rectification of curve
     * @return curve of measures mu(k)
     */
    public Curve createMeasuresCurve(Curve rectification) {

        Curve result = new Curve("Flars Measure Curve");
        List<Point> points = rectification.getPoints();
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            result.addPoint(new Point(point.getTime(), calculateMeasure(i, points, globalOverviewCount)));
        }

        return result;
    }


    /**
     * Calculates FLARS measure of particular point
     * @param centerPosition current point position
     * @param points rectification of curve
     * @param globalOverviewCount number of points to left and to right to use in measure
     * @return measure of certain point
     */
    private double calculateMeasure(int centerPosition, List<Point> points, int globalOverviewCount) {

        int totalCount = points.size();
        int a = (centerPosition <= globalOverviewCount) ?
                0 : centerPosition - globalOverviewCount;
        int b = (centerPosition >= totalCount - globalOverviewCount) ?
                totalCount - 1 : centerPosition + globalOverviewCount;

        Point centerPoint = points.get(centerPosition);
        double leftMoment = 0;
        double rightMoment = 0;

        // calculate left, right moments
        for (int i = a; i < b; i ++) {
            Point currentPoint = points.get(i);
            if (currentPoint.getValue() < centerPoint.getValue()) {
                leftMoment += (centerPoint.getValue() - currentPoint.getValue()) * calculateWeight(a, b, i, centerPosition);
            } else if (currentPoint.getValue() > centerPoint.getValue()) {
                rightMoment += (currentPoint.getValue() - centerPoint.getValue()) * calculateWeight(a, b, i, centerPosition);
            }
        }

        // fuzzy comparison of moments
        // in this version let's uze n2(rM,lM) fuzzy comparison
        return fuzzyComparison(rightMoment, leftMoment);
    }


    /**
     *currently only n2(a,b) uses in this algorithm
     * todo : make it configurable
     * @return double value in [-1, 1]
     */
    private double fuzzyComparison(double a, double b) {
        return (b - a) / Math.sqrt(a * a + b * b);
    }

    /**
     * Calculates weight to ImF(k)
     * @param startIndex first index of global overview
     * @param endIndex last index of global overview
     * @param currentIndex current index, to calculate weight
     * @param centerIndex index of element to calculate muF(k)
     * @return weight
     */
    private double calculateWeight(
            int startIndex,
            int endIndex,
            int currentIndex,
            int centerIndex) {
        return 1D - Math.abs(currentIndex - centerIndex) /
         (Math.max(Math.abs(centerIndex - startIndex), Math.abs(centerIndex - endIndex)) + 1D);
    }
}
