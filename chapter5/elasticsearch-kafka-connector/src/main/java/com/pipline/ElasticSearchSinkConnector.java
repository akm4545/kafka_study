package com.pipline;

import com.pipline.config.ElasticSearchSinkConnectorConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.SinkConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//재사용성을 위해 커넥터로 개발
//ex) 동일 토픽을 서로 다른 엘라스틱서치 클러스터에 넣고 싶다면 엘라스틱서치 클러스터별 커넥터를 개발하는게 아니라 설정을 통해
//타깃 엘라스틱서치 클러스터를 변경

//커넥터를 생성했을 때 최초로 실행
//태스크를 실행하기 위한 이전 단계로써 설정값을 확인하고 태스크 클래스를 지정하는 역할 수행

//jar 파일로 빌드 -> 카프카 커넥트가 참조할 수 있는 디렉토리로 이동 -> 분산 모드 카프카 커넥트 설정파일 수정 (connect-distributed.properties)
// -> 분산 모드 카프카 커넥트에 REST API로 요청하여 커넥터 실행
// ex) curl -L -X POST 'localhost:8083/connectoers' \-H 'Content-Type: application/json' \ ...
public class ElasticSearchSinkConnector extends SinkConnector {

    private final Logger logger = LoggerFactory.getLogger(ElasticSearchSinkConnector.class);

    private Map<String, String> configProperties;

    @Override
    public String version() {
        return "1.0";
    }

//    설정값을 가져와 ElasticSearchSinkConnectorConfig 인스턴스 생성 
//    커넥터 생성시 입력
    @Override
    public void start(Map<String, String> props) {
        this.configProperties = props;

        try{
            new ElasticSearchSinkConnectorConfig(props);
        }catch (ConfigException e){
            throw new ConnectException(e.getMessage(), e);
        }
    }

//    커넥터 실행 시 태스크 역할을 할 클래스 선언
//    만약 다수의 태스크를 운영할 경우 태스크 클래스 분기 로직을 넣을 수 있다
    @Override
    public Class<? extends Task> taskClass() {
        return ElasticSearchSinkTask.class;
    }

//    태스크 별로 다른 설정값을 부여할 경우 여기에 로직 추가 
//    해당 구문은 모든 설정이 같도록 설정
    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        List<Map<String, String>> taskConfigs = new ArrayList<>();
        Map<String, String> taskProps = new HashMap<>();
        taskProps.putAll(configProperties);

        for(int i=0; i<maxTasks; i++){
            taskConfigs.add(taskProps);
        }

        return taskConfigs;
    }

//    설정값 리턴 
//    리턴한 설정값은 사용자가 커넥터를 생성할 때 설정값을 정상적으로 입력했는지 검증시 사용
    @Override
    public ConfigDef config() {
        return ElasticSearchSinkConnectorConfig.CONFIG;
    }

//    커넥터 종료 로그
    @Override
    public void stop() {
        logger.info("Stop elasticsearch connector");
    }
}
