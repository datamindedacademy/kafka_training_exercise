package be.dataminded.streams;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;

import java.util.Properties;

public class Main {

    public static void main(String[] args) {
        SensorEventsStreamingApplication sensorEventsStreamingApplication = new SensorEventsStreamingApplication();

        StreamsBuilder builder = new StreamsBuilder();
        sensorEventsStreamingApplication.buildSensorAggregateStream(builder);

        Properties props = new Properties();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "my-streams-application");

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
    }

}
