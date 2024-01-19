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

public class SimpleConsumer {
    private final static Logger logger = LoggerFactory.getLogger(SimpleConsumer.class);

//    토픽 명
    private final static String TOPIC_NAME = "test";

//    카프카 클러스터 주소
    private final static String BOOTSTRAP_SERVERS = "192.168.1.10:9092";

//    컨슈머 그룹 명
//    컨슈머 그룹을 통해 컨슈머의 목적을 구분할 수 있다
//    컨슈머 그룹을 기준으로 컨슈머 오프셋(데이터를 어디까지 처리했는지 인덱스) 을 관리하기 때문에 subscribe 메서드를 사용하여 구독하는 경우에는 컨슈머 그룹을 선언해야한다
//    컨슈머가 중단/재시작 되어도 컨슈머 그룹의 컨슈머 오프셋을 기준으로 이후 데이터를 처리
//    선언하지 않으면 어떤 그룹에도 속하지 않는 컨슈머로 동작
    private final static String GROUP_ID = "test-group";

    public static void main(String[] args){
        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
//        브로커가 전송한 메세지 역직렬화 역직렬화 타입이 맞지 않는경우 변환이 되지 않을 수도 있다.
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

//        컨슈머 객체 생성
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(configs);
//        토픽을 할당하기 위해 subscribe 메서드 사용
//        인자로 구독할 토픽명을 받는다
        consumer.subscribe(Arrays.asList(TOPIC_NAME));

        while (true){
//            컨슈머는 poll메서드를 호출하여 데이터를 가져와서 처리한다
//            지속적 데이터 처리를 위해서 반복 호출을 해야한다
//            인자값 Duration은 브로커로부터 데이터를 가져올 때 컨슈머 버퍼에 데이터를 기다리기 위한 타임아웃 간격이다
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

//            데이터 처리부
            for(ConsumerRecord<String, String> record : records){
                logger.info("{}", record);
            }
        }

    }
}
