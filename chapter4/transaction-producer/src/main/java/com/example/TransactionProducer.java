package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class TransactionProducer {
    private final static Logger logger = LoggerFactory.getLogger(TransactionProducer.class);

    private final static String TOPIC_NAME = "test";

    private final static String BOOTSTRAP_SERVERS = "192.168.1.10:9092";

    public static void main(String[] args){
        Properties configs = new Properties();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configs.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "my-transaction-id");

        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(configs);
        producer.initTransactions();
        producer.beginTransaction();

        String messageValue = "testMessage";

        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, messageValue);
        producer.send(record);

        logger.info("{}", record);

        producer.flush();
        producer.commitTransaction();

        producer.close();
    }

}
