package com.example.kafkaConsumerSyncOffsetCommit;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConsumerWithSyncOffsetCommit {
    private final static Logger logger = LoggerFactory.getLogger(ConsumerWithSyncOffsetCommit.class);

    private final static String TOPIC_NAME = "test";

    private final static String BOOTSTRAP_SERVERS = "192.168.1.10:9092";

    private final static String GROUP_ID = "test-group";

    public static void main(String[] args){
        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
//        명시적 오프셋 처리
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(configs);
        consumer.subscribe(Arrays.asList(TOPIC_NAME));

        while (true){
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

//            현재 처리한 오프셋을 매번 커밋하기위해 commitSync 메서드가 파라미터로 받을 Hashmap 타입 서넝ㄴ
//            키는 토픽과 파티션 정보가 담긴 TopicPartition
//            값은 오프셋 정보가 담긴 OffsetAndMetadata
            Map<TopicPartition, OffsetAndMetadata> currentOffset = new HashMap<TopicPartition, OffsetAndMetadata>();

            for(ConsumerRecord<String, String> record : records){
                logger.info("record:{}", record);

//                처리가 완료된 레코드의 정보를 토대로 키와 벨류를 넣는다
//                offset 저장시에 poll을 수행할때 마지막으로 커밋한 오프셋부터 레코드를 리턴하기 때문에 지금 오프셋 번호에 + 1을 해줘야 한다
                currentOffset.put(
                        new TopicPartition(record.topic(), record.partition()),
                        new OffsetAndMetadata(record.offset() + 1, null)
                );

//                commitSync에 파라미터를 넣어 호출하면 해당 측정 토픽, 파티션의 오프셋이 매번 커밋된다
                consumer.commitSync(currentOffset);
            }
        }
    }
}
