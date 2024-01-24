package com.example.sourceConnectorExample;

import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;

import java.util.List;
import java.util.Map;

public class TestSourceTask extends SourceTask {
    
//    테스크 버전
    @Override
    public String version() {
        return null;
    }

//    테스크 실행 로직
//    데이터 처리에 필요한 모든 리소스를 여기서 초기화
    @Override
    public void start(Map<String, String> props) {

    }

//    소스로 부터 데이터를 읽어오는 로직 작성
//    데이터를 읽어오면 토픽으로 보낼 데이터를 SourceRecord로 정의
//    List<SourceRecord> 인스턴스에 데이터를 담아 리턴하면 데이터가 토픽으로 전송
    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        return null;
    }

//    종료 로직 
//    start에서 리소스를 사용한다면 여기서 해제
    @Override
    public void stop() {

    }
}
