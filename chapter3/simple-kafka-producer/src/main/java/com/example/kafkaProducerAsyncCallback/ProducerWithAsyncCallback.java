package com.example.kafkaProducerAsyncCallback;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class ProducerWithAsyncCallback {
    private final static String TOPIC_NAME = "test";

    private final static String BOOTSTRAP_SERVERS = "192.168.1.10:9092";

    public static void main(String[] args){
        Properties configs = new Properties();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(configs);

        ProducerRecord<String, String> record = new ProducerRecord<String, String>(TOPIC_NAME, "Pangyo", "Pangyo");
//        레코드 전송 결과를 비동기로 받기 위해서 호출시 사용자 정의 callback 클래스를 넣는다
//        동기로 받는것보다 더 빠르지만 전송 데이터의 순서가 중요한 겨우 사용하면 안된다
        producer.send(record, new ProducerCallback());

        producer.flush();
        producer.close();
    }

}
