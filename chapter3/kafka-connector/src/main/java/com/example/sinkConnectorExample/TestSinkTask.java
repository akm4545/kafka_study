package com.example.sinkConnectorExample;

import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;

import java.util.Collection;
import java.util.Map;


public class TestSinkTask extends SinkTask {

//    테스크 버전
    @Override
    public String version() {
        return null;
    }

//    테스크 시작 로직
    @Override
    public void start(Map<String, String> props) {

    }

//    데이터를 일정 주기로 싱크 애플리케이션 또는 싱크 파일에 저장할 때 사용하는 로직
//    레코드를 저장하는 로직을 넣을 수 있으며 이 경우에는 flush메서드 로직을 구현하지 않아도 된다
    @Override
    public void put(Collection<SinkRecord> records) {

    }

//   put 후처리 메서드 
//    ex) 몽고디비인 경우 put에서 insert 후 flush에서 commit을 수행하여 트랜잭션 종료
    @Override
    public void flush(Map<TopicPartition, OffsetAndMetadata> currentOffsets) {
        
    }

//    테스크 종료 로직
    @Override
    public void stop() {

    }
}
