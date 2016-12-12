package com.fjbecerra.config;

import com.fjbecerra.mailrecord.MailRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.ExecutionException;


public enum KafkaConfig {

    INSTANCE;

    private KafkaProducer<String, MailRecord> kafkaProducer;
    Properties kafkaProp = new Properties();

    KafkaConfig(){

        kafkaProp.put("key.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        kafkaProp.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        kafkaProp.put("acks", "all");
        kafkaProp.put("retries", "5");
    }

    public void init(String broker, String shemaRegistry, Integer batchSize, Integer requestSize, Integer messageMaxSize)
    {
        kafkaProp.put("bootstrap.servers", broker);
        kafkaProp.put("schema.registry.url",shemaRegistry);
        kafkaProp.put("batch.size", batchSize);
        kafkaProp.put("send.buffer.bytes",messageMaxSize );
        kafkaProp.put("max.request.size", requestSize);
        kafkaProducer = new KafkaProducer<>(kafkaProp);
    }

    public RecordMetadata send(MailRecord message, String topic) throws ExecutionException, InterruptedException {
        ProducerRecord<String, MailRecord> record = new ProducerRecord<>(topic, message.getUuid().toString(), message);
        return kafkaProducer.send(record).get();
    }
}
