package ru.spbsu.amik.timeseries.implementations;

import ru.spbsu.amik.timeseries.api.AnomalyDetector;
import ru.spbsu.amik.timeseries.model.Anomaly;
import ru.spbsu.amik.timeseries.model.Curve;
import ru.spbsu.amik.timeseries.model.Point;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DrasTAnomalyDetector implements AnomalyDetector {


    private long globalOverviewInterval;

    /** in interval (0, 1] */
    private double horizontalBackgroundLevel;

    public void setGlobalOverviewInterval(long globalOverviewInterval) {
        this.globalOverviewInterval = globalOverviewInterval;
    }

    public void setHorizontalBackgroundLevel(double horizontalBackgroundLevel) {
        this.horizontalBackgroundLevel = horizontalBackgroundLevel;
    }

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
    public double calculateVerticalLevel(Curve rectification) {

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
            double leftMeasure = leftSideMeasure(verticalBackgroundLevel, globalOverviewInterval / 2, i, points);
            double rightMeasure = rightSideMeasure(verticalBackgroundLevel, globalOverviewInterval / 2, i, points);
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
        return new ArrayList<Anomaly>(0);
    }


    private double rightSideMeasure(double verticalBackgroundLevel, long globalOverviewInterval, int currentIndex, List<Point> points) {

        Point currentPoint = points.get(currentIndex);
        int endIndex = 0;

        for (int i = currentIndex; i < points.size() && points.get(i).getTime() <= currentPoint.getTime() + globalOverviewInterval / 2; i ++) {
            endIndex = i;
        }

        return intervalMeasure(verticalBackgroundLevel, globalOverviewInterval, points, currentIndex, currentIndex, endIndex);
    }

    private double weightFunction(long globalOverviewInt, long centerTime, long currentTime) {
        return globalOverviewInt + 1 - Math.abs(centerTime - currentTime)
                / (globalOverviewInt + 1);
    }

    private double leftSideMeasure(double verticalBackgroundLevel, long globalOverviewInterval, int currentIndex, List<Point> points) {

        Point currentPoint = points.get(currentIndex);
        int startIndex = 0;

        for (int i = currentIndex; i >= 0 && points.get(i).getTime() >= currentPoint.getTime() - globalOverviewInterval / 2; i --) {
            startIndex = i;
        }

        return intervalMeasure(verticalBackgroundLevel, globalOverviewInterval, points, currentIndex, startIndex, currentIndex);
    }

    private double intervalMeasure(
                double verticalBackgroundLevel,
                long globalOverviewInterval,
                List<Point> points,
                int currentIndex,
                int startIndex,
                int endIndex) {

        Point currentPoint = points.get(currentIndex);
        double numerator = 0;
        double denominator = 0;

        for (int i = startIndex; i <= endIndex; i ++) {

            double weight = weightFunction(globalOverviewInterval, currentPoint.getTime(), points.get(i).getTime());
            if (points.get(i).getValue() <= verticalBackgroundLevel)
                numerator += weight;
            denominator += weight;
        }

        return denominator == 0 ? 0 : numerator / denominator;
    }
}
