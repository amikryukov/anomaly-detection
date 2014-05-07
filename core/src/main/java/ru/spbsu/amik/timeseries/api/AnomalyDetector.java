package ru.spbsu.amik.timeseries.api;

import ru.spbsu.amik.timeseries.model.Anomaly;
import ru.spbsu.amik.timeseries.model.Curve;

import java.util.List;

public interface AnomalyDetector {

    List<Anomaly> detectAnomalies(Curve rectification);
}
