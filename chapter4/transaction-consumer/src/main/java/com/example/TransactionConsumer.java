package com.example;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class TransactionConsumer {

    private final static Logger logger = LoggerFactory.getLogger(TransactionConsumer.class);

    private final static String TOPIC_NAME = "test";

    private final static String BOOTSTRAP_SERVERS = "192.168.1.10:9092";

    private final static String GROUP_ID = "test-group";

//    트랜잭션 프로듀서 설정
//    다수의 파티션에 데이터를 저장할 경우 모든 데이터에 대해 동일한 원자성을 만족시키기 위해 사용된다
//    동일 트랜잭션으로 묶어 전체 데이터를 처리하거나 처리하지 않도록 하는것을 의미
//    기본 컨슈머 = 데이터가 파티션에 쌓이는 대로 모두 가져가 처리
//    트랜잭션 컨슈머 = 트랜잭션으로 처리 완료된 데이터만 처리
//    트랜잭션의 시작과 끝을 표현하기 위해 트랜잭션 레코드를 하나 더 보낸다
//    트랜잭션 레코드는 파티션에 저장되어 오프셋을 한 개 차지한다
    public static void main(String[] args){
        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
//        트랜잭션 컨슈머 설정
        configs.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(configs);

        consumer.subscribe(Arrays.asList(TOPIC_NAME));

        while (true){
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

            for(ConsumerRecord<String, String> record : records){
                logger.info("record:{}", record);
            }
        }
    }
}
