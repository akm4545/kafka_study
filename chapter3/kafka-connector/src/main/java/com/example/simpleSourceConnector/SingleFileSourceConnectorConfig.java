package com.example.simpleSourceConnector;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public class SingleFileSourceConnectorConfig extends AbstractConfig {

//    파일 소스 커넥터는 어떤 파일을 읽을지 지정해야 하므로 파일에 대한 정보를 기입
    public static final String DIR_FILE_NAME = "file";

    private static final String DIR_FILE_NAME_DEFAULT_VALUE = "/tmp/kafka.txt";

    private static final String DIR_FILE_NAME_DOC = "읽을 파일 경로와 이름";

//    읽은 파일을 어느 토픽으로 보낼 것인지 지정
    public static final String TOPIC_NAME = "topic";

    public static final String TOPIC_DEFAULT_VALUE = "test";

    private static final String TOPIC_DOC = "보낼 토픽명";

//    커넥터에서 사용할 옵션값들에 대한 정의를 표현
//    플루언트 스타일로 옵션값 지정
//    소스 파일, 토픽 순이다
//    importance 값은 사용자에게 해당 옵션이 중요하다는 것을 명시적으로 표현하기 위한 값으로만 사용된다
    public static ConfigDef CONFIG = new ConfigDef().define(DIR_FILE_NAME,
            ConfigDef.Type.STRING,
            DIR_FILE_NAME_DEFAULT_VALUE,
            ConfigDef.Importance.HIGH,
            DIR_FILE_NAME_DOC)
            .define(TOPIC_NAME,
                    ConfigDef.Type.STRING,
                    TOPIC_DEFAULT_VALUE,
                    ConfigDef.Importance.HIGH,
                    TOPIC_DOC);

    public SingleFileSourceConnectorConfig(Map<String, String> props){
        super(CONFIG, props);
    }
}
