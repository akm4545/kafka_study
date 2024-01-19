package com.example.kafkaConsumerRebalanceListener;

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

public class ConsumerWithRebalanceListener {
    private final static Logger logger = LoggerFactory.getLogger(ConsumerWithRebalanceListener.class);

    private final static String TOPIC_NAME = "test";

    private final static String BOOTSTRAP_SERVERS = "192.168.1.10:9092";

    private final static String GROUP_ID = "test-group";

    private static KafkaConsumer<String, String> consumer;

    public static void main(String[] args){
        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        consumer = new KafkaConsumer<String, String>(configs);
//        리벨런스 감지를 위해 ConsumerRebalanceListener 구현한 클래스를 인자로 넣는다
        consumer.subscribe(Arrays.asList(TOPIC_NAME), new RebalanceListener());

        Map<TopicPartition, OffsetAndMetadata> currentOffset = new HashMap<TopicPartition, OffsetAndMetadata>();

        while(true){
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

            for(ConsumerRecord<String, String> record : records){
                logger.info("{}", record);

//                레코드 데이터 처리가 끝나면 hashmap에 담는다.
//                해당 값은 오프셋 지정 커밋 시에 사용한다
                currentOffset.put(
                        new TopicPartition(record.topic(), record.partition()),
                        new OffsetAndMetadata(record.offset() + 1, null)
                );

                consumer.commitSync(currentOffset);
            }
        }
    }
}
