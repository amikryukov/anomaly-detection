package ru.spbsu.amik.timeseries.implementations;

import ru.spbsu.amik.timeseries.api.Rectifier;
import ru.spbsu.amik.timeseries.model.Point;

import java.util.List;

public class FragmentLengthRectifier implements Rectifier {

    @Override
    public double rectify(List<Point> survey) {

        if (survey.size() == 1) {
            return survey.iterator().next().getValue();
        }

        double result = 0;

        for (int i = 1; i < survey.size(); i++) {
            double previous = survey.get(i - 1).getValue();
            double current = survey.get(i).getValue();
            result += Math.abs(current - previous);
        }

        return result;
    }
}
