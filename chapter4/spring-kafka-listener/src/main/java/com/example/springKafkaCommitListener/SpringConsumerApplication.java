package com.example.springKafkaCommitListener;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

//배치 컨슈머 리스너
//배치 타입으로 선언된 리스너
//컨슈머를 직접 사용하기 위해 컨슈머 인스턴스를 파라미터로 받는다
//동기, 비동기 커밋을 사용할 수 있다
//동기, 비동기 커밋이나 컨슈머 인스턴스에서 제공하는 메서드를 사용하고 싶을 때 사용

//배치 커밋 리스너 
//배치 타입으로 선언된 리스너
//컨테이너에서 관리하는 AckMode를 사용하기 위해 Acknowledgement인스턴스르 파라미터로 받는다
//커밋을 수행하기 위해 한정적인 메서드만 제공한다
//컨슈머 컨테이너에서 관리하는 AckMode를 사용하여 커밋하고 싶을때 사용

//두 방식의 방법을 모두 사용하고 싶다면 배치 커밋 컨슈머 리스너(BatchAcknowledgingConsumerAwareMessageListener)를 사용한다
@SpringBootApplication
public class SpringConsumerApplication {
    public static Logger logger = LoggerFactory.getLogger(SpringConsumerApplication.class);

    public static void main(String[] args){
        SpringApplication application = new SpringApplication(SpringConsumerApplication.class);
        application.run(args);
    }

//    AckMode를 MANUAL 또는 MANUAL_IMMEDIATE로 사용할 경우에는 수동 커밋을 하기 위해 
//    파라미터로 Acknowledgment 인스턴스를 받아야 한다
//    acknowledge메서드를 호출함으로써 커밋을 수행한다
    @KafkaListener(topics = "test", groupId = "test-group-01")
    public void commitListener(ConsumerRecords<String, String> records, Acknowledgment ack){
        records.forEach(record -> logger.info(record.toString()));
        ack.acknowledge();
    }

//    동기 커밋, 비동기 커밋을 사용하고 싶다면 컨슈머 인스턴스를 파라미터로 받아 사용한다
//    commitSync나 commitAsync메서드를 호출하면 사용자가 원하는 타이밍에 커밋할 수 있도록 로직을 추가할 수 있다
//    다만 리스너가 커밋을 하지 않도록 AckMode는 MENUAL / MENUAL_IMMEDIATE로 설정해야 한다
    @KafkaListener(topics = "test", groupId = "test-group-02")
    public void consumerCommitListener(ConsumerRecords<String, String> records, Consumer<String, String> consumer){
        records.forEach(record -> logger.info(record.toString()));
        consumer.commitSync();
    }
}
