package com.example.Kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

public class ChatConsumer {
    private KafkaConsumer<String, String> consumer;
    private String topic;

    public ChatConsumer(String bootstrapServers, String topic, String groupID) {
        this.topic = topic;
        this.consumer = new KafkaConsumer<>
                (
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                        ConsumerConfig.GROUP_ID_CONFIG, groupID,
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName(),
                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName(),
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"
                        )
                );

    }

    public void listen(Consumer<String> messageHandler) {
        try {
            consumer.subscribe(Collections.singleton(topic));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    messageHandler.accept(record.value());
                }
            }
        } finally {
            consumer.close();

        }
    }

}
