package com.example.springKafkaTemplateProducer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaSendCallback;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

public class SpringProducerApplication implements CommandLineRunner {

    private static String TOPIC_NAME = "test";

//    커스텀 카프카 템플릿 주입받아서 사용
//    메서드명과 변수명이 동일해야 한다
    @Autowired
    private KafkaTemplate<String, String> customKafkaTemplate;

    public static void main(String[] args){
        SpringApplication application = new SpringApplication(SpringProducerApplication.class);
        application.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
//        정상적재 여부를 확인하고 싶다면 ListenableFuture 사용
        ListenableFuture<SendResult<String, String>> future = customKafkaTemplate.send(TOPIC_NAME, "test");
        
//        addCallback을 붙여 프로듀서가 보낸 데이터의 브로커 적재 여부를 비동기로 확인 할 수 있다
        future.addCallback(new KafkaSendCallback<String, String>(){

//            정상 적재시 호출 메서드
            @Override
            public void onSuccess(SendResult<String, String> result) {

            }

//            실패시 호출 메서드
            @Override
            public void onFailure(KafkaProducerException ex) {

            }
        });

        System.exit(0);
    }
}
