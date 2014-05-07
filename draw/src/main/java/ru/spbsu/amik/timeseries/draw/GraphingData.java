package ru.spbsu.amik.timeseries.draw;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import ru.spbsu.amik.timeseries.implementations.DrasAnomalyDetector;
import ru.spbsu.amik.timeseries.implementations.EqualStepRectifier;
import ru.spbsu.amik.timeseries.implementations.FragmentEnergyRectifier;
import ru.spbsu.amik.timeseries.implementations.FragmentLengthRectifier;
import ru.spbsu.amik.timeseries.model.Anomaly;
import ru.spbsu.amik.timeseries.model.Curve;
import ru.spbsu.amik.timeseries.model.Point;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class GraphingData {

    public static void main(String[] args) {


        Curve curve = new TimeSeriesGenerator(99).generateRandomEqualStepSeries(
                "Title",
                1000, // count
                100, // step
                0,   // start time
                555  // color
        );

        EqualStepRectifier esr = new EqualStepRectifier();
        esr.setLocalRectifier(new FragmentLengthRectifier());
        esr.setLocalOverviewCount(2);
        esr.setLocalOverviewCount(5);

        esr.setLocalRectifier(new FragmentEnergyRectifier(3));
        esr.setLocalOverviewCount(3);
        Curve curve3 = esr.rectify(curve);

        DrasAnomalyDetector dad = new DrasAnomalyDetector();
        dad.setGlobalOverviewCount(15);
        dad.setHorizontalBackgroundLevel(0.9);
        dad.setVerticalBackgroundLevel(25d);
        for (Anomaly anomaly : dad.detectAnomalies(curve3)) {
            System.out.println(anomaly.toString());
        }
        System.out.println("------------------");
        for (Anomaly anomaly : dad.detectAnomaliesCalculatingVL(curve3)) {
            System.out.println(anomaly.toString());
        }

        translateToJFree(curve);

        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel p = new JPanel();
        p.add(translateToJFree(curve));
        p.add(translateToJFree(curve3));
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