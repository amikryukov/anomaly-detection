package ru.spbsu.amik.timeseries.draw;

import ru.spbsu.amik.timeseries.model.Curve;
import ru.spbsu.amik.timeseries.model.Point;

import java.util.Random;

public class TimeSeriesGenerator {

    private Random random;

    public TimeSeriesGenerator(long randomSeed) {
        random = new Random(randomSeed);
    }

//    public TimeSeriesGenerator() {
//        random = new Random();
//    }


    public Curve generateRandomEqualStepSeries (String title, int count, long step, long startTime, int color) {
        Curve curve = new Curve(title, color);

        for (int i = 0; i < count; ) {
            double value = random.nextDouble();
            if (value < 0.02) {
                if (value < 0.01) {

                    for (int k = 0; k < 5; k++) {
                        double vv = value;
                        vv += 10 * random.nextDouble();
                        Point point = new Point(startTime + (i++) * step, vv);
                        curve.addPoint(point);
                    }
                }
                value += 10 * random.nextDouble();
            }
            Point point = new Point(startTime + (i++) * step, value);
            curve.addPoint(point);
        }
        return curve;
    }

    public Curve generateFallEqualStepSeries(String title, int count, int step, int startTime, int color) {
        Curve curve = new Curve(title, color);

        int stab = 200;
        for (int i = 0; i < count; ) {
            double value = stab + random.nextDouble();
            if (value < stab + 0.01) {
                stab = stab / 2;
            }
            Point point = new Point(startTime + (i++) * step, value);
            curve.addPoint(point);
        }
        return curve;
    }

    public Curve generateNoAnomalyEqualStepSeries(String title, int count, int step, int startTime, int color) {
        Curve curve = new Curve(title, color);

        int stab = 200;
        for (int i = 0; i < count; ) {
            double value = stab + random.nextDouble();
            Point point = new Point(startTime + (i++) * step, value);
            curve.addPoint(point);
        }
        return curve;
    }
}
