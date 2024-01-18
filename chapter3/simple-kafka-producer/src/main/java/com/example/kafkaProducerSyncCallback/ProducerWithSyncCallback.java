package com.example.kafkaProducerSyncCallback;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

//브로커 정상 전송 여부 확인
public class ProducerWithSyncCallback {
    private final static Logger logger = LoggerFactory.getLogger(ProducerWithSyncCallback.class);

    private final static String TOPIC_NAME = "test";

    private final static String BOOTSTRAP_SERVERS = "192.168.1.10:9092";
    
    public static void main(String[] args){
        Properties configs = new Properties();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(configs);
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, "Pangyo", "Pangyo");

        try{
            //send 메서드는 Future 객체 반환
            //Future객체는 RecordMetadata의 비동기 결과를 표현한 객체 
            //정상 적재에 대한 결과 데이터가 포함되어 있다
            //프로듀서로 보낸 데이터의 결과를 동기적으로 가져올 수 있다
            //send의 결괏값은 카프카 브로커로부터 응답을 기다렸다가 응답이 오면 RecordMetadata인스턴스를 반환한다
            RecordMetadata metadata = producer.send(record).get();
//            [main] INFO com.example.kafkaProducerSyncCallback.ProducerWithSyncCallback -- test-1@1
            //test 토픽에 1번 파티션에 적재되었으며 레코드의 오프셋은 1번
            logger.info(metadata.toString());
        }catch (Exception e){
            logger.error(e.getMessage());
        }finally {
            producer.flush();
            producer.close();
        }

    }
}
