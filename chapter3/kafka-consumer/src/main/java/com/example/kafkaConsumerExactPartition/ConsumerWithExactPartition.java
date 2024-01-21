package com.example.kafkaConsumerExactPartition;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ConsumerWithExactPartition {
    private final static Logger logger = LoggerFactory.getLogger(ConsumerWithExactPartition.class);

    private final static String TOPIC_NAME = "test";

    private final static int PARTITION_NUMBER = 0;

    private final static String BOOTSTRAP_SERVERS = "192.168.1.10:9092";

    public static void main(String[] args){
        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(configs);
        
//        파티션을 컨슈머에게 명시적으로 할당할때 사용
//        해당 구문은 test토픽의 0번 파티션 레코드를 가져오는 구문
//        컨슈머가 특정 토픽, 특정 파티션에 할당되므로 리벨런싱 과정이 없다
        consumer.assign(Collections.singleton(new TopicPartition(TOPIC_NAME, PARTITION_NUMBER)));

        while (true){
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

            for(ConsumerRecord<String, String> record : records){
                logger.info("record:{}", record);
            }
        }

    }
}
