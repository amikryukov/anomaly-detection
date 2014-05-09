package ru.spbsu.amik.timeseries.implementations;

import ru.spbsu.amik.timeseries.api.GlobalRectifier;
import ru.spbsu.amik.timeseries.api.Rectifier;
import ru.spbsu.amik.timeseries.model.Curve;
import ru.spbsu.amik.timeseries.model.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Rectifies curve with epsilon neighborhood ot current point
 */
public class EpsilonGlobalRectifier implements GlobalRectifier {

    private Rectifier localRectifier;

    private long epsilon;

    public void setLocalRectifier(Rectifier localRectifier) {
        this.localRectifier = localRectifier;
    }

    public void setEpsilon(long epsilon) {
        this.epsilon = epsilon;
    }

    @Override
    public Curve rectify(Curve curve) {

        Curve result = new Curve(curve.getTitle() + " [rectification]", curve.getColor());
        int count = curve.getPoints().size();

        List<Point> survey = new ArrayList<Point>();

        for (int i = 0; i < count; i++) {

            Point point = curve.getPoints().get(i);
            int startIndex = i;

            while (startIndex >= 0 && curve.getPoints().get(startIndex).getTime() >= point.getTime() - epsilon) {
                startIndex --;
            }
            startIndex ++;



            while(startIndex < count && curve.getPoints().get(startIndex).getTime() <= point.getTime() + epsilon) {
                survey.add(curve.getPoints().get(startIndex));
                startIndex ++;
            }

            long currentTime = curve.getPoints().get(i).getTime();
            result.addPoint(new Point(currentTime, localRectifier.rectify(survey)));
            survey.clear();
        }

        return result;
    }

    @Override
    public String toString() {
        return "EpsilonGlobalRectifier{" +
                "localRectifier=" + localRectifier +
                ", epsilon=" + epsilon +
                '}';
    }
}
