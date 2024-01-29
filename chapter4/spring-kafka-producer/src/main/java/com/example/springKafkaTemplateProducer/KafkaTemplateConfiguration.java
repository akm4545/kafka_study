package com.example.springKafkaTemplateProducer;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

//커스텀 카프카 템플릿
//프로듀서 팩토리를 통해 만든 카프카 템플릿 객체를 빈으로 등록하여 사용
//스프링 카프카 애플리케이션 내부에 다양한 종류의 카프카 프로듀서 인스턴스를 생성하는 방법
//ex) A클러스터 전송 카프카 프로듀서와 B클러스터 전송 카프카 프로듀서 동시 사용
@Configuration
public class KafkaTemplateConfiguration {
    
//    해당 메서드 명으로 빈 생성
    @Bean
    public KafkaTemplate<String, String> customKafkaTemplate(){
//      ProducerFactory를 사용하여 KafkaTemplate객체를 만들 때 프로듀서 옵션을 직접 사용
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.10:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        ProducerFactory<String, String> pf = new DefaultKafkaProducerFactory<>(props);

//        ProducerFactory에 커스텀으로 설정한 설정값이 적용된 KafkaTempalte 객체 리턴
//        이외에도
//        ReplyingKafkaTemplate = 컨슈머가 특정 데이터를 전달받았는지 여부 확인
//        RoutingKafkaTemplate = 전송하는 토픽별로 옵션을 다르게 설정
        return new KafkaTemplate<>(pf);
    }
}
