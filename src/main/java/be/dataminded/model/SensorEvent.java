package be.dataminded.model;

import com.github.javafaker.Faker;

import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;

public class SensorEvent {

    private static Faker faker = new Faker();

    private String timestamp;
    private double value;
    private String unit;

    public SensorEvent() {
    }

    public SensorEvent(String timestamp, double value, String unit) {
        this.timestamp = timestamp;
        this.value = value;
        this.unit = unit;
    }

    public static SensorEvent generateRandomEvent() {
        return new SensorEvent(
            ZonedDateTime.now().toString(),
            faker.number().randomDouble(2, 0, 1000),
            faker.stock().nsdqSymbol().toLowerCase(Locale.ROOT));
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensorEvent that = (SensorEvent) o;
        return Double.compare(that.value, value) == 0 && Objects.equals(timestamp, that.timestamp) && Objects.equals(unit, that.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, value, unit);
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SensorEvent.class.getSimpleName() + "[", "]")
            .add("timestamp='" + timestamp + "'")
            .add("value=" + value)
            .add("unit='" + unit + "'")
            .toString();
    }
}
