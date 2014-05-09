package ru.spbsu.amik.timeseries.implementations;

import ru.spbsu.amik.timeseries.api.AnomalyDetector;
import ru.spbsu.amik.timeseries.model.Anomaly;
import ru.spbsu.amik.timeseries.model.Curve;
import java.util.List;

public class DrasTAnomalyDetector implements AnomalyDetector {


    @Override
    public List<Anomaly> detectAnomalies(Curve rectification) {
        return null;
    }
}
