package com.pipline.config;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public class ElasticSearchSinkConnectorConfig extends AbstractConfig {
//    토픽의 데이터를 저장할 엘라스틱서치 호스트 이름 설정
    public static final String ES_CLUSTER_HOST = "es.host";
    private static final String ES_CLUSTER_HOST_DEFAULT_VALUE = "localhost";
    private static final String ES_CLUSTER_HOST_DOC = "엘라스틱서치 호스트를 입력";

//    토픽의 데이터를 저장할 엘라스틱서치의 포트 이름 설정

    public static final String ES_CLUSTER_PORT = "es.port";
    private static final String ES_CLUSTER_PORT_DEFAULT_VALUE = "9200";
    private static final String ES_CLUSTER_PORT_DOC = "엘라스틱서치 포트를 입력";

//    토픽의 데이터를 저장할 때 설정할 데이터의 인덱스 이름
    public static final String ES_INDEX = "es.index";
    private static final String ES_INDEX_DEFAULT_VALUE = "kafka-connector-index";
    private static final String ES_INDEX_DOC = "엘라스틱서치 인덱스를 입력";

//    앞의 설정값으로 ConfigDef 인스턴스 생성
//    ConfigDef = 커넥터에서 설정값이 정상적으로 들어왔는지 검증하기 위해 사용
//    해당 설정값들은 커넥터를 사용하는 개발자가 커넥터를 신규 생성할 때 사용
    public static ConfigDef CONFIG = new ConfigDef()
            .define(ES_CLUSTER_HOST, ConfigDef.Type.STRING, ES_CLUSTER_HOST_DEFAULT_VALUE, ConfigDef.Importance.HIGH, ES_CLUSTER_HOST_DOC)
            .define(ES_CLUSTER_PORT, ConfigDef.Type.STRING, ES_CLUSTER_PORT_DEFAULT_VALUE, ConfigDef.Importance.HIGH, ES_CLUSTER_PORT_DOC)
            .define(ES_INDEX, ConfigDef.Type.STRING, ES_INDEX_DEFAULT_VALUE, ConfigDef.Importance.HIGH, ES_INDEX_DOC);

    public ElasticSearchSinkConnectorConfig(Map<String, String> props) {
        super(CONFIG, props);
    }
}
