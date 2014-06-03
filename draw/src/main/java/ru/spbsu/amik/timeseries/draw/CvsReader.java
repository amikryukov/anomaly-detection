package ru.spbsu.amik.timeseries.draw;

import ru.spbsu.amik.timeseries.model.Curve;
import ru.spbsu.amik.timeseries.model.Point;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CvsReader {
    private Reader reader;

    private String separator = ",";

    public CvsReader(Reader reader, String separator) {
        this.reader = reader;
        this.separator = separator;
    }

    public static void main(String[] args) throws IOException {

        Reader reader = new FileReader("C:\\Users\\amikryukov\\Documents\\plot.csv");

        CvsReader simpleCsvPlotReader = new CvsReader(reader, ",");

        for (Curve curve : simpleCsvPlotReader.parsePlotInCsv()) {
            System.out.println(curve);
        }

    }

    public List<Curve> parsePlotInCsv() throws IOException {

        CustomReader csvReader = new CustomReader(this.reader, separator);

        String[] header = csvReader.readRaw();

        Map<String, Curve> curveMap = new HashMap<String, Curve>();

        // for all curve's column
        for (int i = 1; i < header.length; i ++) {
            curveMap.put(header[i], new Curve(header[i]));
        }

        String [] raw;
        int column = 1;
        while (null != (raw = csvReader.readRaw())) {

//            if (raw.length < header.length) {
//                break;
//            }
            System.out.println(raw.length);
            for (int i = 0; i < raw.length; i++) {
                System.out.print(raw[i] + ", ");
            }
            System.out.println();

            if (raw[column] == null || raw[column].isEmpty()) {
                column ++;
            }

            curveMap.get(header[column]).addPoint(
                    new Point(
                            new Double(raw[0]).longValue(),
                            Double.parseDouble(raw[column])
                    )
            );
        }


        // sort as they are in csv
        List<Curve> sortedArrayList = new ArrayList<Curve>(header.length - 1);
        for (int i = 1; i < header.length; i ++) {
            sortedArrayList.add(curveMap.get(header[i]));
        }

        return sortedArrayList;
    }

    /**
     * Reader to read splitted line by specified separator.
     */
    private class CustomReader extends BufferedReader {

        private String separator;

        public CustomReader(Reader in, String separator) {
            super(in);
            this.separator = separator;
        }

        public String [] readRaw() throws IOException {
            String line = readLine();


            if (null != line && !line.isEmpty()) {
                // or split in another way
                return line.split(separator);
            }

            return null;
        }
    }
}
