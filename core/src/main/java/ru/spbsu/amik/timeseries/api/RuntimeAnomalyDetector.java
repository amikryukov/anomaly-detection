package ru.spbsu.amik.timeseries.api;

import ru.spbsu.amik.timeseries.model.Anomaly;
import ru.spbsu.amik.timeseries.model.Point;

public interface RuntimeAnomalyDetector {

    public Anomaly.AnomalyLevel checkPoint(Point point);
}
