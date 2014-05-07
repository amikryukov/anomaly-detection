package ru.spbsu.amik.timeseries.api;

import ru.spbsu.amik.timeseries.model.Curve;

public interface GlobalRectifier {

    public Curve rectify(Curve curve);
}
