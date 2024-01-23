package com.example.simpleKafkaProcessor;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;

import java.util.Properties;

public class SimpleKafkaProcessor {
    private static String APPLICATION_NAME = "processor-application";

    private static String BOOTSTRAP_SERVERS = "192.168.1.10:9092";

    private static String STREAM_LOG = "stream_log";

    private static String STREAM_LOG_FILTER = "stream_log_filter";

    public static void main(String[] args){
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_NAME);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

//        프로세서 API를 사용한 토폴로지를 구성하기 위해 선언
        Topology topology = new Topology();
//        소스 프로세서를 가져오기 위한 구문 (데이터 읽어오기)
//        첫번째 파라미터 = 소스 프로세서의 이름
//        두번째 파라미터 = 대상 토픽 이름
        topology.addSource("Source", STREAM_LOG)
//                스트림 프로세서를 사용하기 위한 구문 (데이터 처리)
//                첫번째 파라미터 = 스트림 프로세서의 이름
//                두번째 파라미터 = 사용자가 정의한 프로세서 인스턴스
//                세번째 파라미터 = 부모 노드
                .addProcessor("Process", () -> new FilterProcessor(), "Source")
//                싱크 프로세서를 사용하기 위한 구문 (데이터 가공)
                .addSink("Sink", STREAM_LOG_FILTER, "Process");

        KafkaStreams streaming = new KafkaStreams(topology, props);
        streaming.start();
    }
}
