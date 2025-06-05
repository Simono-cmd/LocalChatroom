package com.example.Kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Map;

public class ChatProducer {
    private KafkaProducer<String, String> producer;

    public ChatProducer(String bootstrapServers) {
        this.producer = new KafkaProducer<>
                (
                Map.of(
                        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName(),
                        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()
                )
        );
    }

    public void sendMessage(String topic, String message) {
        producer.send(new ProducerRecord<>(topic, message));
        producer.flush();

    }
}
