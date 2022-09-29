package be.dataminded.producer;

import be.dataminded.model.SensorEvent;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class SensorEventProducer {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());

        try (Producer<String, SensorEvent> producer = new KafkaProducer<>(props)) {

            for (int i = 0; i < 100; i++) {
                String key = String.valueOf(i + 1);
                SensorEvent value = SensorEvent.generateRandomEvent();

                producer.send(new ProducerRecord<>("sensor_events", key, value), (metadata, exception) -> {
                    if (exception != null) {
                        exception.printStackTrace();
                    } else {
                        System.out.printf("Produced record to topic %s partition [%d] @ offset %d%n",
                            metadata.topic(),
                            metadata.partition(),
                            metadata.offset());
                    }
                });
            }

            producer.flush();
        }
    }

}
