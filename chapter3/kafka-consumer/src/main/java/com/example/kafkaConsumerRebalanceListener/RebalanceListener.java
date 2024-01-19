package com.example.kafkaConsumerRebalanceListener;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

//리벨런스 리스너
//컨슈머 그룹에서 컨슈머가 추가 또는 제거되면 파티션을 컨슈머에 재할당하는 과정
//poll메서드를 총해 반환받은 데이터를 모두 처리하기 전에 리벨런스가 발생하면 데이터를 중복 처리할 수 있다.
//받은 데이터 중 일부를 처리했으나 커밋하지 않았기 떄문
//리벨런스 시 중복을 방지하기 위해 처리한 데이터를 기준으로 커밋을 시도해야 한다
//ConsumerRebalanceListener 리벨런스 발생을 감지하기 위한 인터페이스
public class RebalanceListener implements ConsumerRebalanceListener {
    private final static Logger logger = LoggerFactory.getLogger(RebalanceListener.class);

//    리벨런스가 끝난 뒤에 파티션 할당이 완료되면 호출
    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
        logger.warn("Partitions are assigned: " + partitions.toString());
    }

//    리벨런스가 시작되기 직전에 호출 
//    마지막 처리 레코드를 기준으로 커밋하기 위해서는 해당 메서드에 작업을 해야한다
    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
        logger.warn("Partitions are revoked: " + partitions.toString());
//        책의 예제 코드에서는 하나의 클래스로 작성되었으니 깃 코드는 분할되어 작성됨
//        책의 예제 코드는 ConsumerWithRebalanceListener에 작성
//        리벨런스가 발생하면 가장 마지막으로 처리 완료한 레코드를 기준으로 커밋 실시
//        데이터 처리의 중복을 방지
        //consumer.commitSync(currentOffset);
    }
}
