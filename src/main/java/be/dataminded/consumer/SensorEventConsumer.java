package be.dataminded.consumer;

import be.dataminded.model.SensorEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class SensorEventConsumer {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "my-consumer-group-id2");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        try (Consumer<String, SensorEvent> consumer = new KafkaConsumer<>(props, new StringDeserializer(), new JsonDeserializer<>(SensorEvent.class))) {
            consumer.subscribe(List.of("sensor_events"));

            while (true) {
                ConsumerRecords<String, SensorEvent> records = consumer.poll(Duration.ofMinutes(5));

                for (ConsumerRecord<String, SensorEvent> record : records) {
                    String key = record.key();
                    String value = record.value().toString();
                    //consumer.commitAsync();
                    consumer.commitSync(Duration.ofSeconds(1));
                    System.out.printf("Consumed record with key %s and value %s on partition %s\n", key, value, record.partition());
                }

            }

        }
    }

}
