package com.example;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class IdempotenceProducer {

    private final static Logger logger = LoggerFactory.getLogger(IdempotenceProducer.class);

    private final static String TOPIC_NAME = "test";

    private final static String BOOTSTRAP_SERVERS = "192.168.1.10:9092";

//    멱등성 프로듀서
//    동일한 데이터를 여러 번 전송하더라도 단 한번만 저장됨을 의미
    public static void main(String[] args){
        Properties configs = new Properties();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        
//        멱등성 프로듀서로 동작하도록 설정
//        기본 프로듀서와 달리 PID(Producer unique ID)와 시퀀스 넘버를 같이 전달
//        해당 값을 비교하여 동일 메세지인지 판별
//        동일한 세션(PID 생명주기)에서만 정확히 한번 전달 보장
//        멱등성 프로듀서에 이슈가 생겨 재시작하면 PID가 달라지므로 중복 데이터가 적재될 수 있음
//        해당 옵션을 활성화 시키면 재전송 횟수 옵션은 최대값으로 설정되고 레코드의 적재 여부 옵션인 acks도 all로 설정됨
//        이렇게 설정되는 이유는 적어도 한번 이상 브로커에 데이터를 보냄으로써 한번만 데이터가 적재되는것을 보장하기 위함
//        시퀀스 넘버도 0에서 1씩 증가하므로 해당 예외 (OutOfOrderSequenceException)이 발생함을 대비해야 한다
        configs.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(configs);

        String messageValue = "testMessage";

        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, messageValue);
        producer.send(record);

        logger.info("{}", record);

        producer.flush();
        producer.close();
    }
}
