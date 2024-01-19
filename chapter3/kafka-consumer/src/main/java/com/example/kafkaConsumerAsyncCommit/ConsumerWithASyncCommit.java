package com.example.kafkaConsumerAsyncCommit;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

public class ConsumerWithASyncCommit {
    private final static Logger logger = LoggerFactory.getLogger(ConsumerWithASyncCommit.class);

    private final static String TOPIC_NAME = "test";

    private final static String BOOTSTRAP_SERVERS = "192.168.1.10:9092";

    private final static String GROUP_ID = "test-group";

    public static void main(String[] args){
        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(configs);
        consumer.subscribe(Arrays.asList(TOPIC_NAME));

        while (true){
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

            for(ConsumerRecord<String, String> record : records){
                logger.info("record:{}", record);
            }

//            비동기 오프셋 커밋
//            리턴된 가장 마지막 레코드를 기준으로 으포셋 커밋
//            커밋이 완료될 때까지 응답을 기다리지 않는다
//            동기 오프셋 커밋보다 동일 시간당 데이터 처리량이 많다
//            commitAsync은 비동기로 커밋 응답을 받기 때문에 callback 함수를 파라미터로 받아서 결과를 얻을 수 있다 - ex) OffsetCommitCallback
            consumer.commitAsync(new OffsetCommitCallback() {
//                비동기로 받은 커밋 응답은 onComplete로 받을 수 있다
//                정상 커밋은 exception 변수가 null 이다
//                커밋 완료된 정보는 Map<TopicPartition, OffsetAndMetadata> offsets에 포함되어 있다
                @Override
                public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
                    if (exception != null){
                        System.err.println("Commit failed");
                    }else{
                        System.err.println("Commit succeeded");
                    }

                    if(exception != null){
                        logger.error("Commit failed for offsets {}", offsets, exception);
                    }
                }
            });
        }
    }
}
