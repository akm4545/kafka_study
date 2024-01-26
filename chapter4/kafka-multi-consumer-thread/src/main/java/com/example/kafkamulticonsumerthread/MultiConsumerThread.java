package com.example.kafkamulticonsumerthread;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//파티션 개수만큼 스레드 개수를 늘리는 멀티 스레드 전략
public class MultiConsumerThread {
    private final static String TOPIC_NAME = "test";

    private final static String BOOTSTRAP_SERVERS = "192.168.1.10:9092";

    private final static String GROUP_ID = "test-group";

//    스레드 개수 변수
    private final static int CONSUMER_COUNT = 3;

    public static void main(String[] args){
        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        ExecutorService executorService = Executors.newCachedThreadPool();

//        3개의 컨슈머 스레드를 생성한다
        for(int i=0; i<CONSUMER_COUNT; i++){
            ConsumerWorker worker = new ConsumerWorker(configs, TOPIC_NAME, i);

            executorService.execute(worker);
        }
    }
}
