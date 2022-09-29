package be.dataminded.model;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.StringJoiner;

public class AggregateSensorEvent {

    private String unit;
    private long count;
    private double sum;
    private double average;
    private ZonedDateTime maxTimestamp;

    public AggregateSensorEvent() {
        // default constructor for Jackson
    }

    public AggregateSensorEvent(String unit, long count, double sum, double average, ZonedDateTime maxTimestamp) {
        this.unit = unit;
        this.count = count;
        this.sum = sum;
        this.average = average;
        this.maxTimestamp = maxTimestamp;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public ZonedDateTime getMaxTimestamp() {
        return maxTimestamp;
    }

    public void setMaxTimestamp(ZonedDateTime maxTimestamp) {
        this.maxTimestamp = maxTimestamp;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AggregateSensorEvent.class.getSimpleName() + "[", "]")
            .add("unit='" + unit + "'")
            .add("count=" + count)
            .add("sum=" + sum)
            .add("average=" + average)
            .add("maxTimestamp=" + maxTimestamp)
            .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AggregateSensorEvent that = (AggregateSensorEvent) o;
        return count == that.count && Double.compare(that.sum, sum) == 0 && Double.compare(that.average, average) == 0 && Objects.equals(unit, that.unit) && Objects.equals(maxTimestamp, that.maxTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unit, count, sum, average, maxTimestamp);
    }
}
