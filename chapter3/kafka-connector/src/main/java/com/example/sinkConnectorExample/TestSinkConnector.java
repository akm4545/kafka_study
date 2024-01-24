package com.example.sinkConnectorExample;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;

import java.util.List;
import java.util.Map;

//사용자 정의 클래스 선언 = 커넥트 호출명
public class TestSinkConnector extends SinkConnector {

    //    설정값 초기화
//    올바른 값이 아니라면 ConnectException을 발생시켜 종료해야 한다
    @Override
    public void start(Map<String, String> props) {

    }

//    커넥터의 사용 테스크 클래스 지정
    @Override
    public Class<? extends Task> taskClass() {
        return null;
    }

//    테스크 개수가 2개 이상일 경우 각기 다른 옵션을 설정할 때 사용
    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        return null;
    }

//    커넥터 종료 로직
    @Override
    public void stop() {

    }

//    커넥터가 사용할 설정값 정보를 받는다
    @Override
    public ConfigDef config() {
        return null;
    }

    //    버전
    @Override
    public String version() {
        return null;
    }
}
