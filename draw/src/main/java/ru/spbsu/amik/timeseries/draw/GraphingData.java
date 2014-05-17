package ru.spbsu.amik.timeseries.draw;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;
import ru.spbsu.amik.timeseries.implementations.*;
import ru.spbsu.amik.timeseries.model.Anomaly;
import ru.spbsu.amik.timeseries.model.Curve;
import ru.spbsu.amik.timeseries.model.Point;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class GraphingData {

    public static void main(String[] args) {

        rectificationsExamples();
    }

    private static void rectificationsExamples() {
        TimeSeriesGenerator seriesGenerator = new TimeSeriesGenerator(4);

        Curve curve = seriesGenerator.generateGoodExampleEqualStepSeries(
                "Incoming Data",
                1000, // count
                100, // step
                555,  // color
                4,// noise level
                30, // extremal noise level
                20 // number of points when fall
        );

        // energy examples
        EqualStepGlobalRectifier eqsGlobalRectifier = new EqualStepGlobalRectifier();
        eqsGlobalRectifier.setLocalRectifier(new FragmentEnergyRectifier());
        eqsGlobalRectifier.setLocalOverviewCount(5);
        Curve equalStepE5Rectifier = eqsGlobalRectifier.rectify(curve);


//        eqsGlobalRectifier.setLocalOverviewCount(15);
//        Curve equalStepE15Rectifier = eqsGlobalRectifier.rectify(curve);
//
//        eqsGlobalRectifier.setLocalOverviewCount(25);
//        Curve equalStepE25Rectifier = eqsGlobalRectifier.rectify(curve);

        // length examples
        eqsGlobalRectifier.setLocalRectifier(new FragmentLengthRectifier());

        eqsGlobalRectifier.setLocalOverviewCount(5);
        Curve equalStepL5Rectifier = eqsGlobalRectifier.rectify(curve);


//        eqsGlobalRectifier.setLocalOverviewCount(15);
//        Curve equalStepL15Rectifier = eqsGlobalRectifier.rectify(curve);
//
//        eqsGlobalRectifier.setLocalOverviewCount(25);
//        Curve equalStepL25Rectifier = eqsGlobalRectifier.rectify(curve);




        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));

        p.add(translateToJFree(curve, "Time, second", "Value"));
        p.add(translateToJFree(equalStepE5Rectifier, "Time, second", "Value of rectification"));
//        p.add(translateToJFree(equalStepE15Rectifier, "Time, second", "Value of rectification"));
//        p.add(translateToJFree(equalStepE25Rectifier, "Time, second", "Value of rectification"));
        p.add(translateToJFree(equalStepL5Rectifier, "Time, second", "Value of rectification"));
//        p.add(translateToJFree(equalStepL15Rectifier, "Time, second", "Value of rectification"));
//        p.add(translateToJFree(equalStepL25Rectifier, "Time, second", "Value of rectification"));
        JScrollPane ff = new JScrollPane(p);
        f.add(ff);

        f.setSize(1100, 650);
        f.setLocation(200,200);
        f.setVisible(true);
    }

    private static void allInOne() {
        TimeSeriesGenerator seriesGenerator = new TimeSeriesGenerator(0);

        Curve curve = seriesGenerator.generateFallEqualStepSeries(
                "Data",
                200, // count
                100, // step
                0,   // start time
                555  // color
        );

        EqualStepGlobalRectifier esr = new EqualStepGlobalRectifier();
        esr.setLocalRectifier(new FragmentEnergyRectifier());
        esr.setLocalOverviewCount(20);
        Curve rectification = esr.rectify(curve);
        rectification.setTitle("Rectification");

        EpsilonGlobalRectifier egr = new EpsilonGlobalRectifier();
        egr.setLocalRectifier(new FragmentEnergyRectifier());
        egr.setEpsilon(3000); // 3 second
        Curve epsilonRectification = egr.rectify(curve);
        epsilonRectification.setTitle("epsilon rectification");

        DrasAnomalyDetector dad = new DrasAnomalyDetector();
        dad.setGlobalOverview(50);
        dad.setHorizontalBackgroundLevel(0.9);
        System.out.println("--------DRAS----------");
        for (Anomaly anomaly : dad.detectAnomalies(epsilonRectification)) {
            System.out.println(anomaly.toString());
        }

        DrasTAnomalyDetector dtad = new DrasTAnomalyDetector();
        dtad.setGlobalOverview(10000l);
        dtad.setHorizontalBackgroundLevel(0.9);
        System.out.println("--------DRASt----------");
        for (Anomaly anomaly : dtad.detectAnomalies(rectification)) {
            System.out.println(anomaly.toString());
        }

        FlarsAnomalyDetector fad = new FlarsAnomalyDetector();
        fad.setGlobalOverviewCount(200);
        fad.setVerticalExtremalLevel(1);
        Curve measure05Curve = fad.createMeasuresCurve(rectification);
        measure05Curve.setTitle("Flars Measure");

        System.out.println("--------FLARS----------");
        for (Anomaly anomaly : fad.detectAnomalies(rectification)) {
            System.out.println(anomaly.toString());
        }

        translateToJFree(curve, "Time, second", "Value");

        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel p = new JPanel();
        p.add(translateToJFree(curve, "Time, second", "Value"));
        p.add(translateToJFree(rectification, "Time, second", "Value"));
        p.add(translateToJFree(epsilonRectification, "Time, second", "Value"));
        p.add(translateToJFree(measure05Curve, "Time, second", "Value"));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JScrollPane ff = new JScrollPane(p);
        f.add(ff);

        f.setSize(1100, 650);
        f.setLocation(200,200);
        f.setVisible(true);
    }

    private static JLabel translateToJFree(Curve curve, String xAxisLabel, String yAxisLabel) {
        return translateToJFree(curve, xAxisLabel, yAxisLabel, Collections.EMPTY_LIST);
    }

    private static JLabel translateToJFree(Curve curve, String xAxisLabel, String yAxisLabel, final List<Anomaly> anomalyList) {

        XYSeries series = new XYSeries(curve.getTitle());
        for (Point point : curve.getPoints()) {
            series.add(point.getTime(), point.getValue());
        }

        XYDataset xyDataset = new XYSeriesCollection(series);

        JFreeChart chart = ChartFactory.createXYLineChart(
                curve.getTitle(),
                xAxisLabel,
                yAxisLabel,
                xyDataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                true
        );


        final XYPlot plot = chart.getXYPlot();

        plot.setRenderer(0, new XYLineAndShapeRenderer(){

            @Override
            public Paint getItemPaint(int row, int column) {

                if (!anomalyList.isEmpty()) {
                    long x = plot.getDataset().getX(row, column).longValue();
                    for (Anomaly anomaly : anomalyList) {
                        if (x >= anomaly.getStart() && x <= anomaly.getEnd()) {
                            switch (anomaly.getAnomalyLevel()) {
                                case ANOMALY:
                                    return new Color(255, 0, 0);
                                case POTENTIAL:
                                    return new Color(255, 255, 0);
                                default:
                                    return new Color(200, 200, 0);

                            }
                        }
                    }
                }
                return new Color(0, 0, 255);
            }

            @Override
            public Shape getItemShape(int row, int column) {
                return ShapeUtilities.createDiamond(0);
            }
        });

//        XYSeries series2 = new XYSeries(curve.getTitle());
//        for (Point point : curve.getPoints()) {
//            series2.add(point.getTime(), point.getValue() + 100);
//        }
//
//        XYDataset xyDataset2 = new XYSeriesCollection(series2);
//        plot.setDataset(1, xyDataset2);
//
//        int widthOfLine = 32;
//        DefaultXYItemRenderer renderer1 = new DefaultXYItemRenderer();
//        DefaultXYItemRenderer renderer2 = new DefaultXYItemRenderer();
//        renderer1.setBaseShapesVisible(false);
//        renderer2.setBaseShapesVisible(false);
//        renderer1.setBaseStroke(new BasicStroke(widthOfLine));
//        renderer2.setBaseStroke(new BasicStroke(widthOfLine));
//        plot.setRenderer(0, renderer1);
//        plot.setRenderer(1, renderer2);
//
//        plot.getRenderer().setSeriesPaint(1, new ChartColor(20, 0, 0));
//
//        // in blue
//        plot.getRenderer().setSeriesPaint(0, new ChartColor(0, 0, 25));

        plot.getRangeAxis().setLowerBound(0);

        BufferedImage image = chart.createBufferedImage(1100,300);

       // saveToFile(image, "/home/amikryukov/Study/NAUCHNIC/materials/plots/", curve.getTitle().replaceAll("\\s", "_") + ".png");

        JLabel lblChart = new JLabel();
        lblChart.setIcon(new ImageIcon(image));

        return lblChart;
    }

    public static void saveToFile(BufferedImage img, String location, String fileName) {
        File outputfile = new File(location + fileName);
        try {
            ImageIO.write(img, "png", outputfile);
        } catch (IOException e) {
            //
            System.out.println("Could not save image");
        }
    }
}