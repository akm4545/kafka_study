package com.example.simpleSinkConnector;

import com.example.simpleSourceConnector.SingleFileSourceTask;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.SinkConnector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SingleFileSinkConnector extends SinkConnector {

    private Map<String, String> configProperties;

    @Override
    public String version() {
        return "1.0";
    }

//    커넥트에서 SingleFileSinkConnector 커넥터를 생성할 때 받은 설정값들을 초기화한다
//    설정이 잘못되었다면 ConnectException예외를 발생시켜 종료시킨다 
    @Override
    public void start(Map<String, String> props) {
        this.configProperties = props;

        try{
            new SingleFileSinkConnectorConfig(props);
        }catch (ConfigException e){
            throw new ConnectException(e.getMessage(), e);
        }
    }

//    커넥터가 사용할 테스크 클래스
    @Override
    public Class<? extends Task> taskClass() {
        return SingleFileSourceTask.class;
    }

//    테스크 설정값
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

//    커넥터에서 사용할 설정값
    @Override
    public ConfigDef config() {
        return SingleFileSinkConnectorConfig.CONFIG;
    }

    @Override
    public void stop() {

    }
}
