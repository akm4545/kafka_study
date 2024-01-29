package com.example.springKafkaListenerContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

//커스컴 리스너 컨테이너
//서로 다른 설정을 가진 2개 이상의 리스너를 구현하거나 리밸런스 리스너를 구현하기 위해서 사용
//스프링 카프카에서 카프카 리스너 컨테이너 팩토리(KafkaListenerContainerFactory) 인스턴스를 생성해야 한다
@SpringBootApplication
public class SpringConsumerApplication {
    public static Logger logger = LoggerFactory.getLogger(SpringConsumerApplication.class);

    public static void main(String[] args){
        SpringApplication application = new SpringApplication(SpringConsumerApplication.class);
        application.run(args);
    }

//    containerFactory = "customContainerFactory"
//    커스텀 컨테이너 팩토리로 설정
    @KafkaListener(topics = "test", groupId = "test-group", containerFactory = "customContainerFactory")
    public void customListener(String data){
        logger.info(data);
    }
}
