package com.example;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Properties;

public class SimpleStreamApplication {
    private static String APPLICATION_NAME = "streams-application";

    private static String BOOTSTRAP_SERVERS = "192.168.1.10:9092";

    private static String STREAM_LOG = "stream_log";

    private static String STREAM_LOG_COPY = "stream_log_copy";
    
//    카프카 스트림즈 
//    토픽에 적재된 데이터를 상태기반 또는 비상태기반으로 실시간 변환하여 다른 토픽에 적재하는 라이브러리
    public static void main(String[] args){
        Properties props = new Properties();
//        스트림즈 애플리케이션은 애플리케이션 아이디를 지정해야한다
//        아이디 값을 기준으로 병렬처리를 한다
//        다른 스트림즈 애플리케이션을 운영하고 싶다면 중복을 피해야 한다
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_NAME);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
//        키와 벨류의 직렬화, 역직렬화 방법
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

//        스트림 토폴로지를 정의하기 위한 용도로 사용
        StreamsBuilder builder = new StreamsBuilder();
//        stream_log 토픽으로부터 Kstream 객체를 만든다
//        이외에 table[KTable], globalTable[GlobalKTable] 메서드도 지원한다
//        최초의 토픽 데이터를 가져오는 소스 프로세서 -> 하나 이상의 토픽에서 데이터를 가져오는 역할
//        KStream = list, KTable = set, GlobalKTable = 해당 토픽을 스트림즈 애플리케이션의 모든 테스크(데이터 처리 단위)에서 처리
        KStream<String, String> stream = builder.stream(STREAM_LOG);

//      to = 다른 토픽으로 전송하기 위한 메서드
//        싱크 프로세스이다 = 데이터를 다른 토픽으로 보내는 프로세스 (마지막 프로세스)
        stream.to(STREAM_LOG_COPY);

//        위에서 설정한 토폴리지에 대한 정보와 스트림즈 실행을 위한 기본 옵션을 파라미터로 KafkaStreams 생성
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
//        인스턴스 실행 (stream_log 토픽의 데이터를 stream_log_copy 토픽으로 전달)
        streams.state();
    }
}
