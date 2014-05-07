package ru.spbsu.amik.timeseries.model;

import java.util.ArrayList;
import java.util.List;

public class Curve {

    private String title;
    // may be here should be something else - not int;
    private int color;
    private List<Point> points;

    public Curve() {
    }

    public Curve(String title, int color, List<Point> points) {
        this.title = title;
        this.color = color;
        this.points = points;
    }

    public Curve(String title, int color) {
        this.title = title;
        this.color = color;
    }

    public Curve(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public void addPoint(Point point) {
        if (points == null) {
            points = new ArrayList<Point>();
        }
        points.add(point);
    }
}
