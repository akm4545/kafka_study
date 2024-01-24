package com.example.sourceConnectorExample;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;

import java.util.List;
import java.util.Map;

//소스 커넥터를 만들 때 
//SourceConnector = 태스크 실행 전 설정파일 초기화 / 사용 태스크 클래스 설정
//SourceTask = 소스 애플리케이션, 소스 파일로부터 데이터를 가져와서 토픽으로 보내는 역할 (토픽에서 사용하는 오프셋이 아닌 자체적인 오프셋 사용)

//해당 클래스 이름은 최종적으로 커넥트 호출에 사용되므로 명확하게 하는게 좋다 ex) mongoDB에서 데이터를 가져와 토픽으로 저장 -> MongoDBSourceConnector
public class TestSourceConnector extends SourceConnector {

//    커넥터 버전 리턴
    @Override
    public String version() {
        return null;
    }
    
//    JSON 또는 config 파일 형태로 입력한 설정값을 초기화 하는 메서드 
//    값이 올바르지 않다면 여기서 ConnectException()을 발생서켜 커넥터를 종료할 수 있다
    @Override
    public void start(Map<String, String> props) {

    }

//    커넥터가 사용할 테스크 클래스 지정
    @Override
    public Class<? extends Task> taskClass() {
        return null;
    }

//    테스크 개수가 2개 이상인 경우 테스크마다 각기 다른 옵션을 설정할 때 사용
    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        return null;
    }

//  커넥터가 사용할 설정값에 대한 정보를 받는다 커넥터 설정값은 ConfigDef 클래스를 통해 각 설정의 이름, 기본값, 중요도, 설명을 정의할 수 있다
    @Override
    public ConfigDef config() {
        return null;
    }

//    커넥터 종료 로직
    @Override
    public void stop() {

    }
}
