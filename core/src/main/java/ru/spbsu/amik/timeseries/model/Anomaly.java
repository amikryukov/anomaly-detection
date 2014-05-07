package ru.spbsu.amik.timeseries.model;

/**
 *  Class that represents anomaly as time period with anomaly level
 */
public class Anomaly {

    /** Start time of anomaly */
    private long start;
    /** End time of anomaly */
    private long end;

    private AnomalyLevel anomalyLevel;

    public Anomaly(long start, long end, AnomalyLevel anomalyLevel) {
        this.start = start;
        this.end = end;
        this.anomalyLevel = anomalyLevel;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public AnomalyLevel getAnomalyLevel() {
        return anomalyLevel;
    }

    /** Anomaly level */
    public enum AnomalyLevel {

        NONE,
        /** Not really anomaly, but it not far from anomaly */
        POTENTIAL,
        ANOMALY
    }

    @Override
    public String toString() {
        return "Anomaly{" +
                "start=" + start +
                ", end=" + end +
                ", anomalyLevel=" + anomalyLevel +
                '}';
    }
}
