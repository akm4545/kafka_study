package com.example;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class SimpleProducer {
    private final static Logger logger = LoggerFactory.getLogger(SimpleProducer.class);

//    전송 토픽 명
    private final static String TOPIC_NAME = "test";

//    카프카 클러스터 서버
    private final static String BOOTSTRAP_SERVERS = "192.168.1.10:9092";

    public static void main(String[] args){
//        카프카 프로듀서(레코드 생성) 객체의 설정 정보
        Properties configs = new Properties();
//        프로듀서가 바라보는 카프카 클러스터 서버 주소
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
//        메세지 키와 값을 직렬화(바이트화) 시키기 위한 직렬화 클래스 설정
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

//        설정정보를 넣어 카프카 프로듀서 생성
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(configs);

//        보낼 메세지
        String messageValue = "testMessage";
//        토픽 이름과 메세지를 넣어 레코드 객체 생성
//        제네릭 타입은 키와 벨류가 들어간다 [현재 메세지 키는 null]
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, messageValue);

//        메세지 키값이 존재하는 레코드로 전송
//        토픽이름, 키, 값
//        키값이 같으면 같은 파티션에 적재
        ProducerRecord<String, String> keyRecord = new ProducerRecord<>("test", "Pangyo", "23");
        
//        적재 파티션 지정 
//        토픽이름, 파티션 번호, 키, 값 
//        파티션 번호는 토픽에 존재하는 파티션 번호로 설정해야함
        int partitionNo = 0;
        ProducerRecord<String, String> partitionRecord = new ProducerRecord<>(TOPIC_NAME, partitionNo, "key", messageValue);

//        send 메서드를 호출한다고 바로 전송되는 방식이 아니라 producer 객체에 담음 
//        배치 전송을 위함
        producer.send(record);
        logger.info("{}", record);

//        메세지 전송
        producer.flush();
//        리소스 종료
        producer.close();
    }
}
