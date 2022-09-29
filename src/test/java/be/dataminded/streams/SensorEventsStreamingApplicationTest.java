package be.dataminded.streams;

import be.dataminded.model.AggregateSensorEvent;
import be.dataminded.model.SensorEvent;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class SensorEventsStreamingApplicationTest {

    private TopologyTestDriver driver;
    private TestInputTopic<String, SensorEvent> inputTopic;
    private TestOutputTopic<String, AggregateSensorEvent> outputTopic;

    @BeforeEach
    void setUp() {
        SensorEventsStreamingApplication app = new SensorEventsStreamingApplication();

        StreamsBuilder builder = new StreamsBuilder();
        app.buildSensorAggregateStream(builder);

        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "sensor-event-app-unit-test");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:1234");
        driver = new TopologyTestDriver(builder.build(), config);

        inputTopic = driver.createInputTopic("sensor_events",
            app.getStringSerde().serializer(),
            app.getInputValueSerde().serializer());

        outputTopic = driver.createOutputTopic("sensor_events_aggregate",
            app.getStringSerde().deserializer(),
            app.getOutputValueSerde().deserializer());
    }

    @AfterEach
    void tearDown() {
        driver.close();
    }

    @Test
    void givenSensorEventsBelow100Value_expectEventsFilteredOut() {
        inputTopic.pipeInput("1", new SensorEvent(ZonedDateTime.now().toString(), 50, "co"));
        inputTopic.pipeInput("2", new SensorEvent(ZonedDateTime.now().toString(), 150, "co2"));
        inputTopic.pipeInput("3", new SensorEvent(ZonedDateTime.now().toString(), 75, "h2o"));

        List<KeyValue<String, AggregateSensorEvent>> outputs = outputTopic.readKeyValuesToList();
        assertThat(outputs.size()).isEqualTo(1);
        assertThat(outputs.get(0).value.getSum()).isEqualTo(150);
    }

    @Test
    void givenUnitsWithLowerCase_expectAllUnitsUppercase() {
        inputTopic.pipeInput("1", new SensorEvent(ZonedDateTime.now().toString(), 200, "co"));
        inputTopic.pipeInput("2", new SensorEvent(ZonedDateTime.now().toString(), 200, "co2"));
        inputTopic.pipeInput("3", new SensorEvent(ZonedDateTime.now().toString(), 200, "h2o"));

        List<KeyValue<String, AggregateSensorEvent>> outputs = outputTopic.readKeyValuesToList();
        assertThat(outputs.size()).isEqualTo(3);
        assertThat(outputs.get(0).value.getUnit()).isEqualTo("CO");
        assertThat(outputs.get(1).value.getUnit()).isEqualTo("CO2");
        assertThat(outputs.get(2).value.getUnit()).isEqualTo("H2O");
    }

    @Test
    void test_aggregation_of_sensor_events() {
        inputTopic.pipeInput("1", new SensorEvent(getMaximumZdt().toString(), 200, "co"));

        inputTopic.pipeInput("2", new SensorEvent(getMaximumZdt().toString(), 250, "co2"));
        inputTopic.pipeInput("3", new SensorEvent(getMinimumZdt().toString(), 250, "co2"));

        inputTopic.pipeInput("4", new SensorEvent(getMaximumZdt().toString(), 400, "h2o"));
        inputTopic.pipeInput("5", new SensorEvent(getMinimumZdt().toString(), 300, "h2o"));

        Map<String, AggregateSensorEvent> outputs = outputTopic.readKeyValuesToMap();
        assertThat(outputs.size()).isEqualTo(3);
        assertThat(outputs.get("CO")).isEqualTo(new AggregateSensorEvent("CO", 1L, 200d, 200d, getMaximumZdt()));
        assertThat(outputs.get("CO2")).isEqualTo(new AggregateSensorEvent("CO2", 2L, 500d, 250d, getMaximumZdt()));
        assertThat(outputs.get("H2O")).isEqualTo(new AggregateSensorEvent("H2O", 2L, 700d, 350d, getMaximumZdt()));
    }

    private ZonedDateTime getMinimumZdt() {
        return ZonedDateTime.of(LocalDate.MIN, LocalTime.MIN, ZoneId.of("UTC"));
    }

    private ZonedDateTime getMaximumZdt() {
        return ZonedDateTime.of(LocalDate.MAX, LocalTime.MAX, ZoneId.of("UTC"));
    }
}