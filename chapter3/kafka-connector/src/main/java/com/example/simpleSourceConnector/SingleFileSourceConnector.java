package com.example;

import com.example.simpleSourceConnector.SingleFileSourceConnectorConfig;
import com.example.simpleSourceConnector.SingleFileSourceTask;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//소스 커넥터 = 소스 파일이나 애플리케이션으로부터 데이터를 읽어 토픽으로 전송하는 커넥터
public class SingleFileSourceConnector extends SourceConnector {

    private final Logger logger = LoggerFactory.getLogger(SingleFileSourceConnector.class);

    private Map<String, String> configProperties;

    @Override
    public String version() {
        return "1.0";
    }

    @Override
    public void start(Map<String, String> props) {
        this.configProperties = props;
        try {
//            커넥트에서 SingleFileSourceConnector 커넥터를 생성할 때 받은 설정값 초기화
            new SingleFileSourceConnectorConfig(props);
        } catch (ConfigException e) {
//            필수값이 빠져있다면 예외를 발생시켜 커넥터 종료
            throw new ConnectException(e.getMessage(), e);
        }
    }

//    테스크 클래스 이름 지정
    @Override
    public Class<? extends Task> taskClass() {
        return SingleFileSourceTask.class;
    }

//    테스크가 2개 이상이더라도 동일한 설정값을 받도록 ArrayList에 동일한 설정
    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        List<Map<String, String>> taskConfigs = new ArrayList<>();
        Map<String, String> taskProps = new HashMap<>();
        taskProps.putAll(configProperties);
        for (int i = 0; i < maxTasks; i++) {
            taskConfigs.add(taskProps);
        }
        return taskConfigs;
    }
    
//    커넥터에서 사용할 설정값 지정
    @Override
    public ConfigDef config() {
        return SingleFileSourceConnectorConfig.CONFIG;
    }

    @Override
    public void stop() {
    }
}
