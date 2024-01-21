package com.example.kafkaConsumerSyncOffsetCommitShutdownHook;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ConsumerWithSyncOffsetCommit {
    private final static Logger logger = LoggerFactory.getLogger(ConsumerWithSyncOffsetCommit.class);

    private final static String TOPIC_NAME = "test";

    private final static String BOOTSTRAP_SERVERS = "192.168.1.10:9092";

    private final static String GROUP_IP = "test-group";

    private static KafkaConsumer<String, String> consumer;

//    애플리케이션이 정상적으로 종료되지 않는다면 컨슈머는 세션 타임아웃이 발생할때까지 컨슈머 그룹에 남는다
//    실제로는 종료되었지만 더는 동작을 하지 않는 컨슈머가 존재하여 파티션의 데이터는 소모되지 않는다
//    컨슈머 랙이 늘어나게 된다 -> 데이터 처리 지연 발생
    public static void main(String[] args){
//        addShutdownHook 자바 애플리케이션이 사용자 또는 운영체제로부터 종료 요청을 받으면 실행하는 쓰레드
        Runtime.getRuntime().addShutdownHook(new ShutdownThread());

        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_IP);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        consumer = new KafkaConsumer<String, String>(configs);
        consumer.subscribe(Arrays.asList(TOPIC_NAME));

        try {
            while (true){
//                poll로 레코드를 지속적으로 처리하다가 종료 요청이 들어오면 위에 등록한
//                셧다운 훅이 작동
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

                for(ConsumerRecord<String, String> record : records){
                    logger.info("{}", record);
                }

                consumer.commitSync();
            }
        }catch (WakeupException e){
            logger.warn("Wakeup consumer");
        }finally {
            logger.warn("Consumer close");
//            사용 리소스 해제
            consumer.close();
        }
    }

    static class ShutdownThread extends Thread {
        public void run(){
            logger.info("Shutdown hook");
//            wakeup 메서드가 호출되면 다음 poll 메서드 호출 시 wakeupException을 발생
            consumer.wakeup();
        }
    }
}
