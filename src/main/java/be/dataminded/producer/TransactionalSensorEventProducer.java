package be.dataminded.producer;

import be.dataminded.model.SensorEvent;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.ProducerFencedException;

import java.util.Properties;

public class TransactionalSensorEventProducer {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "sensor-events-transaction-");

        try (Producer<String, SensorEvent> producer = new KafkaProducer<>(props)) {

            String key = "9999";
            SensorEvent value = SensorEvent.generateRandomEvent();

            producer.initTransactions();
            try {
                producer.beginTransaction();
                producer.send(new ProducerRecord<>("sensor_events", key, value));

                // Simulate an error and abort the transaction after the record has been send to one of the two topics
                // producer.abortTransaction();

                producer.send(new ProducerRecord<>("sensor_events_2", key, value));
                producer.commitTransaction();
            } catch (ProducerFencedException e) {
                producer.close();
            } catch (KafkaException e) {
                producer.abortTransaction();
            }

            producer.flush();
        }
    }

}
