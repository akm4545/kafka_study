package com.example.kafkaConsumerSyncCommit;

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

public class ConsumerWithSyncCommit {
    private final static Logger logger = LoggerFactory.getLogger(ConsumerWithSyncCommit.class);

    private final static String TOPIC_NAME = "test";

    private final static String BOOTSTRAP_SERVER = "192.168.1.10:9092";

    private final static String GROUP_ID = "test-group";

    public static void main(String[] args) {
        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        
//        커밋을 직접 수행할 경우
//        기본 옵션은 비명시적으로 수행하는 true
//        오프셋이 유실되면 데이터 처리의중복이 발생할 수 있다
//        때문에 컨슈머 애플리케이션이 오프셋 커밋을 정상적으로 처리했는지 검증해야한다      
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(configs);
        consumer.subscribe(Arrays.asList(TOPIC_NAME));

        while (true){
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

            for (ConsumerRecord<String, String> record : records){
                logger.info("record:{}", record);
            }

//            commitSync는 poll 메서드로 받은 가장 마지막 레코드의 오프셋을 기준으로 커밋한다
//            동기 오프셋 커밋을 사용할 경우 poll 메서드로 받은 모든 레코드의 처리가 끝난 뒤에 호출해야 한다
//            커밋이 완료되기까지 기다리기 때문에 자동 커밋이나 비동기 오프셋 커밋보다 시간당 처리량이 적다
            consumer.commitSync();
        }

    }

}
