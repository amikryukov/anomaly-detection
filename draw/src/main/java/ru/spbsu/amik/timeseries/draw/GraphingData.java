package ru.spbsu.amik.timeseries.draw;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import ru.spbsu.amik.timeseries.implementations.*;
import ru.spbsu.amik.timeseries.model.Anomaly;
import ru.spbsu.amik.timeseries.model.Curve;
import ru.spbsu.amik.timeseries.model.Point;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class GraphingData {

    public static void main(String[] args) {


        Curve curve = new TimeSeriesGenerator(99).generateRandomEqualStepSeries(
                "Data",
                200, // count
                100, // step
                0,   // start time
                555  // color
        );

        EqualStepGlobalRectifier esr = new EqualStepGlobalRectifier();
        esr.setLocalRectifier(new FragmentLengthRectifier());
        esr.setLocalOverviewCount(2);
        esr.setLocalOverviewCount(5);

        esr.setLocalRectifier(new FragmentEnergyRectifier());
        esr.setLocalOverviewCount(3);
        Curve rectification = esr.rectify(curve);
        rectification.setTitle("Rectification");

        EpsilonGlobalRectifier egr = new EpsilonGlobalRectifier();
        egr.setLocalRectifier(new FragmentEnergyRectifier());
        egr.setEpsilon(300); // 0.5 second
        Curve epsilonRectification = egr.rectify(curve);
        epsilonRectification.setTitle("epsilon rectification");

        DrasAnomalyDetector dad = new DrasAnomalyDetector();
        dad.setGlobalOverviewCount(15);
        dad.setHorizontalBackgroundLevel(0.9);
        System.out.println("--------DRAS----------");
        for (Anomaly anomaly : dad.detectAnomalies(rectification)) {
            System.out.println(anomaly.toString());
        }

        FlarsAnomalyDetector fad = new FlarsAnomalyDetector();
        fad.setGlobalOverviewCount(70);
        fad.setVerticalExtremalLevel(0.5);
        Curve measure05Curve = fad.createMeasuresCurve(rectification);
        measure05Curve.setTitle("Flars Measure");

        System.out.println("--------FLARS----------");
        for (Anomaly anomaly : fad.detectAnomalies(rectification)) {
            System.out.println(anomaly.toString());
        }

        translateToJFree(curve);

        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel p = new JPanel();
        p.add(translateToJFree(curve));
        p.add(translateToJFree(rectification));
        p.add(translateToJFree(epsilonRectification));
        p.add(translateToJFree(measure05Curve));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JScrollPane ff = new JScrollPane(p);
        f.add(ff);

        f.setSize(1100, 650);
        f.setLocation(200,200);
        f.setVisible(true);
    }

    private static JLabel translateToJFree(Curve curve) {

        XYSeries series = new XYSeries(curve.getTitle());
        for (Point point : curve.getPoints()) {
            series.add(point.getTime(), point.getValue());
        }

        XYDataset xyDataset = new XYSeriesCollection(series);

        JFreeChart chart = ChartFactory.createXYLineChart(
                curve.getTitle(),
                "X-label",
                "Y-label",
                xyDataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                true
        );


        XYPlot plot = chart.getXYPlot();
        plot.getRangeAxis().setLowerBound(series.getMinY());

        BufferedImage image = chart.createBufferedImage(1100,200);

        JLabel lblChart = new JLabel();
        lblChart.setIcon(new ImageIcon(image));

        return lblChart;
    }
}