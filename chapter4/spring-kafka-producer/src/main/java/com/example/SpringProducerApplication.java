package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
public class SpringProducerApplication implements CommandLineRunner {

    private static String TOPIC_NAME = "test";

//    KafkaTemplate 주입 
//    application.yaml의 설정값이 들어간다
    @Autowired
    private KafkaTemplate<Integer, String> template;

    public static void main(String[] args){
        SpringApplication application = new SpringApplication(SpringProducerApplication.class);
        application.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        for (int i=0; i<10; i++){
//            sned 메서드로 토픽 이름과 메시지 값을 넣어 전송
            template.send(TOPIC_NAME, "test" + i);
        }

        System.exit(0);
    }
}
