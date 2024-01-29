package com.example.springKafkaRecordListener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;

//레코드 리스너 = 레코드 단위로 프로세싱 이후 커밋
@SpringBootApplication
public class SpringConsumerApplication {
    public static Logger logger = LoggerFactory.getLogger(SpringConsumerApplication.class);

    public static void main(String[] args){
        SpringApplication application = new SpringApplication(SpringConsumerApplication.class);
        application.run(args);
    }
//   컨슈머 그룹 아이디를 다 다르게 받음으로써 동일 데이터를 그룹별로 처리
//    1 -> 00
//    1 -> 01 ...

//    가장 기본적인 리스너 선언 
//    어노테이션으로 토픽과 그룹아이디 지정
//    poll이 호출되어 가져온 레코드들은 차례대로 개별 레코드의 메시지 값을 파라미터로 받게된다
//    파라미터로 컨슈머 레코드를 받기 떄문에 메시지 키, 메시지 값에 대한 처리를 이 메서드 안에서 수행하면 된다
    @KafkaListener(topics = "test", groupId = "test-group-00")
    public void recordListener(ConsumerRecord<String, String> record){
        logger.info(record.topic());
    }

//    메서지 값을 파라미터로 받는 리스너
//    역직렬화 클래스 기본값인 StringDeserializer를 사용했으므로 String 클래스로 메시지 값을 전달받음
    @KafkaListener(topics = "test", groupId = "test-group-01")
    public void singleTopicListener(String messageValue){
        logger.info(messageValue);
    }

//    개별 리스너에 카프카 컨슈머 옵션값을 부여하는 예제
    @KafkaListener(topics = "test",
            groupId = "test-group-02",
            properties = {
            "max.poll.interval.ms:60000",
            "auto.offset.reset:earliest"
    })
    public void singleTopicWithPropertiesListener(String messageValue){
        logger.info(messageValue);
    }

//    2개 이상의 카프카 컨슈머 스레드를 싱행하고 싶다면 concurrency 옵션을 사용한다
//    해당 옵션의 값만큼 컨슈머 스레드를 만들어 병렬처리 한다
    @KafkaListener(topics = "test", groupId = "test-group-03", concurrency = "3")
    public void concurrentTopicListener(String messageValue){
        logger.info(messageValue);
    }

//    특정 토픽의 특정 파티션만 구독하는 예제
    @KafkaListener(topicPartitions =
            {
//                    구독 토픽, 파티션
                @TopicPartition(topic = "test01", partitions = {"0", "1"}),
//                    구독 토픽, 파티션, 오프셋 지정
                @TopicPartition(topic = "test02", partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "3"))
            },
            groupId = "test-group-04"
    )
    public void listenSpecificPartition(ConsumerRecord<String, String> record){
        logger.info(record.toString());
    }
}
