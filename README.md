# Kafka Training Exercises
[![Open in
Gitpod](https://gitpod.io/button/open-in-gitpod.svg)](https://gitpod.io/#https://github.com/jelledv/kafka_training_exercise.git)

## References

- [Kafka quickstart](https://kafka.apache.org/quickstart)
- [Kafka Java client](https://docs.confluent.io/kafka-clients/java/current/overview.html)

#### Setting up a local Kafka broker and Zookeeper
```bash
docker-compose up
```

## Exercises

### first steps
1) Use the Kafka CLI (available in the path (eg: kafka-topics.sh --list --bootstrap-server localhost:9092):
2) Create new topic called 'my-events' with 3 partitions
3) Produce some messages using the 'kafka-console-producer'
4) Consume the messages using the 'kafka-console-producer'

### Console Producer & Consumer

1) Produce records with full key-value pairs. Kafka works with key-value pairs, but so far you’ve only sent records with values only. Well to be fair you’ve sent key-value pairs, but the keys are null. Sometimes you’ll need to send a valid key in addition to the value from the command line. To enable sending full key-value pairs from the command line you add two properties to your console producer, ```parse.key``` and ```key.separator```
2) Start a consumer to show full key-value pairs. Now that we’ve produced full key-value pairs from the command line, you’ll want to consume full key-value pairs from the command line as well. Add the properties ```print.key``` and ```key.separator```
3) Start a new consumer. This time provide a ```group.id``` property. Start multiple consumers with the same group id. This should trigger a rebalance. There are now 2 consumers in the same group. One consumer will get 1 partition assigned, the other one will get 2 partitions. Try to see this effect in action by producing and consuming. 
4) Start a new consumer. change the ```group.id``` property again. What happens now? Why? 

### Java Producer

1) Take a look at [this](https://developer.confluent.io/learn-kafka/apache-kafka/producers/) page for a Java Kafka Producer example.
2) Write a Java producer that uses a string serializer for the key and a json serializer for the value. Create a POJO ```SensorEvent``` with 3 properties: timestamp (string), value (double) and unit (string). Write a for loop to send 100 SensorEvents to a newly created topic called ```sensor_events``` with 3 partitions. Try to simulate real sensor measurements and randomize the values and units.
3) Update your Kafka producer properties and use the safest way to produce messages on Kafka, using the properties that we saw in the slides. Those properties increase the durability of our data, guarantee that we don't get any duplicates and provide end to end ordering guarantees
4) Create another topic called ```sensor_events_2``` with 3 partitions. Create a new transactional producer that writes messages to both topics in a transactional way. Try to abort a transaction after you have written to a single topic. What happens now when you try to consume this topic with a console consumer?
5) Bonus: Convert your Java project to a Spring Boot project and use spring-kafka. Figure out how to automatically create topics at application bootstrap time

### Java Consumer
1) Take a look at [this](https://developer.confluent.io/learn-kafka/apache-kafka/consumers/) page for a Java Kafka Consumer example.
2) Write a Java consumer  that uses a string deserializer for the key and a json deserializer for the value. Read data from the topic that you created in the producer exercise. 
3) Start a second instance of your Java consumer with the same ```group.id``` This should trigger a rebalance. There are now 2 consumers in the same group. One consumer will get 1 partition assigned, the other one will get 2 partitions. Produce some more records to this topic and see what happens. 
4) Disable auto committing by setting the ```enable.auto.commit``` to ```false```. What happens when you don't commit your offsets manually, and you start your consumer group? Try committing your offsets manually both synchronously as asynchronously. 