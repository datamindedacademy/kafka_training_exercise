package be.dataminded.streams;

import be.dataminded.consumer.JsonDeserializer;
import be.dataminded.model.AggregateSensorEvent;
import be.dataminded.model.SensorEvent;
import be.dataminded.producer.JsonSerializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

public class SensorEventsStreamingApplication {

    private final Serde<String> inputKeySerde;
    private final Serde<SensorEvent> inputValueSerde;
    private final Serde<String> outputKeySerde;
    private final Serde<AggregateSensorEvent> outputValueSerde;

    public SensorEventsStreamingApplication() {
        this.inputKeySerde = getStringSerde();
        this.inputValueSerde = getInputValueSerde();
        this.outputKeySerde = getStringSerde();
        this.outputValueSerde = getOutputValueSerde();
    }

    void buildSensorAggregateStream(StreamsBuilder builder) {
        builder.stream("sensor_events", Consumed.with(inputKeySerde, inputValueSerde))
            .filter((k, v) -> v.getValue() > 100d)
            .mapValues(v -> new SensorEvent(v.getTimestamp(), v.getValue(), v.getUnit().toUpperCase()))
            .groupBy((k, v) -> v.getUnit(), Grouped.with(inputKeySerde, inputValueSerde))
            .aggregate(() ->
                    new AggregateSensorEvent("", 0L, 0d, 0d, ZonedDateTime.of(LocalDate.MIN, LocalTime.MIN, ZoneId.of("Europe/Brussels"))),
                this::calculateAggregate, Materialized.with(outputKeySerde, outputValueSerde))
            .toStream()
            .to("sensor_events_aggregate", Produced.with(outputKeySerde, outputValueSerde));
    }

    private AggregateSensorEvent calculateAggregate(String key, SensorEvent value, AggregateSensorEvent aggregate) {
        double newSum = aggregate.getSum() + value.getValue();
        long newCount = aggregate.getCount() + 1;
        double newAverage = newSum / newCount;

        ZonedDateTime eventTimestamp = ZonedDateTime.parse(value.getTimestamp());
        ZonedDateTime highestTimestamp = Collections.max(List.of(eventTimestamp, aggregate.getMaxTimestamp()));

        return new AggregateSensorEvent(
            value.getUnit(),
            newCount,
            newSum,
            newAverage,
            highestTimestamp);
    }

    public Serde<AggregateSensorEvent> getOutputValueSerde() {
        return Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(AggregateSensorEvent.class));
    }

    public Serde<SensorEvent> getInputValueSerde() {
        return Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(SensorEvent.class));
    }

    public Serde<String> getStringSerde() {
        return Serdes.String();
    }

}
