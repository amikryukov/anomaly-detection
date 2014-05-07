package ru.spbsu.amik.timeseries.model;

public class Point {

    private long time;
    private double value;

    public Point() {
    }

    public Point(long time, double value) {
        this.time = time;
        this.value = value;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}

