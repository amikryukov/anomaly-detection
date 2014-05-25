package ru.spbsu.amik.timeseries.implementations;

import ru.spbsu.amik.timeseries.api.ExtendedFuzzyComparison;
import ru.spbsu.amik.timeseries.api.RuntimeAnomalyDetector;
import ru.spbsu.amik.timeseries.model.Anomaly;
import ru.spbsu.amik.timeseries.model.Curve;
import ru.spbsu.amik.timeseries.model.Point;

import java.util.*;

public class FlarsRTAnomalyDetector implements RuntimeAnomalyDetector {

    private long globalOverviewInterval = -1;

    // value in [0,1]. usually used values : 0.5, 0.75, 1
    private double verticalExtremalLevel = 0.75;

    // by default sigma extension used
    private ExtendedFuzzyComparison extendedFuzzyComparison = new GrabityExtendedFuzzyComparison(new StandardFuzzyComparison(2));

    public void setVerticalExtremalLevel(double verticalExtremalLevel) {
        this.verticalExtremalLevel = verticalExtremalLevel;
    }


    public void setGlobalOverviewInterval(long globalOverviewInterval) {
        this.globalOverviewInterval = globalOverviewInterval;
    }

    public void setExtendedFuzzyComparison(ExtendedFuzzyComparison extendedFuzzyComparison) {
        this.extendedFuzzyComparison = extendedFuzzyComparison;
    }

    // Points that stick to overview interval
    private Queue<Point> globalOverview = new ArrayDeque<Point>();

    private long lastTimeStamp = -1;

    private List<Anomaly> resultList = new ArrayList<Anomaly>();

    private boolean anomalyStarted = false;

    private Curve measureCurve = new Curve("measureCurve");

    @Override
    public Anomaly.AnomalyLevel checkPoint(Point point) {

        if (globalOverview.isEmpty()) {
            lastTimeStamp = point.getTime();
        }

        long currentTimeStamp = point.getTime();
        while (currentTimeStamp - lastTimeStamp > globalOverviewInterval) {
            globalOverview.remove();

            Point last = globalOverview.peek();

            if (last == null) {
                // queue is empty
                lastTimeStamp = point.getTime();
                break;
            }

            lastTimeStamp = last.getTime();
        }

        globalOverview.add(point);

        Iterator<Point> iterator = globalOverview.iterator();

        Map<Point, Double> weightedSet = new HashMap<Point, Double>();
        while (iterator.hasNext()) {
            Point currentPoint = iterator.next();
            double calculatedWeight =  calculateWeight(globalOverviewInterval, currentPoint.getTime(), point.getTime());
            weightedSet.put(currentPoint, calculatedWeight);
        }
        double extremalMeasure = extendedFuzzyComparison.compare(weightedSet, point.getValue());

        measureCurve.addPoint(new Point(point.getTime(), extremalMeasure));

        if (extremalMeasure > verticalExtremalLevel) {
            if (anomalyStarted) {
                Anomaly lastOne = resultList.get(resultList.size() - 1);
                lastOne.setEnd(point.getTime());
            } else {
                resultList.add(new Anomaly(point.getTime(), point.getTime(), Anomaly.AnomalyLevel.ANOMALY));
                anomalyStarted = true;
            }
            return Anomaly.AnomalyLevel.ANOMALY;
        }

        anomalyStarted = false;
        // todo: look if it is potential Anomaly

        return Anomaly.AnomalyLevel.NONE;
    }

    public Curve getMeasureCurve() {
        return measureCurve;
    }

    public List<Anomaly> getAnomalyList() {
        return resultList;
    }

    private double calculateWeight(long globalOverview, long currentPointTime, long pointTime) {

        return (globalOverview + 1D - Math.abs(currentPointTime - pointTime)) / (globalOverview + 1D);
    }

    public void reset() {
        globalOverview.clear();
        resultList.clear();
    }
}
