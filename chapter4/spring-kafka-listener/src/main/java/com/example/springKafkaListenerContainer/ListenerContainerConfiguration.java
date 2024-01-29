package com.example.springKafkaListenerContainer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ListenerContainerConfiguration {

//    해당 메서드 명은 커스텀 리스너 컨에티너 팩토리로 선언할 때 사용
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> customContainerFactory(){
//        설정값 
//        group.id 는 리스너 컨테이너에도 선언하므로 지금 바로 선언하지 않아도 된다
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.10:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

//        DefaultKafkaConsumerFactory 인스턴스 생성 
//        리스너 컨테이너 팩토리를 생성할 때 컨슈머 기본 옵션을 설정하는 용도
        DefaultKafkaConsumerFactory cf = new DefaultKafkaConsumerFactory(props);
        
//      ConcurrentKafkaListenerContainerFactory = 리스너 컨테이너를 만들기 위해 사용
//        2개 이상의 컨슈머 리스너를 만들 때 사용한다 
//        concurrency를 1로 설정하면 1개 컨슈머 스레드로 실행
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();

//        리밸런스 리스너를 선언하기 위해 setConsumerRebalanceListener 사용
        factory.getContainerProperties().setConsumerRebalanceListener(new ConsumerAwareRebalanceListener() {
            
//            커밋이 되기 전 리밸런스 발생 시
            @Override
            public void onPartitionsRevokedBeforeCommit(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {

            }

//            커밋 후 리밸런스 발생 시
            @Override
            public void onPartitionsRevokedAfterCommit(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {

            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {

            }

            @Override
            public void onPartitionsLost(Collection<TopicPartition> partitions) {

            }
        });

//        레코드 리스너로 사용하기 위해 batch = false
        factory.setBatchListener(false);
//        ackMode 설정 레코드 단위 커밋으로 설정하고 있음
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
//        컨슈머 설정값을 가지고 있는 DefaultKafkaConsumerFactory 인스턴스를 ConcurrentKafkaListenerContainerFactory의 컨슈머 팩토리에 설정
        factory.setConsumerFactory(cf);

        return factory;
    }
}
