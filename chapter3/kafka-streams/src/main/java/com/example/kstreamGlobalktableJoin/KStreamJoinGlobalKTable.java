package com.example.kstreamGlobalktableJoin;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Properties;

public class KStreamJoinGlobalKTable {
    private static String APPLICATION_NAME = "global-table-join-application";

    private static String BOOTSTRAP_SERVERS = "192.168.1.10:9092";

    private static String ADDRESS_GLOBAL_TABLE = "address_v2";

    private static String ORDER_STREAM = "order";

    private static String ORDER_JOIN_STREAM = "order_join";

    public static void main(String[] args){
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_NAME);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();
//      GlobalKTable 생성
//        GlobalKTable = table 형태의 값을 모든 테스크들이 처리
//        ex 파티션 0 -> 테스크 0, 1 / 파티션 1 -> 테스트 0, 1
//        모든 데이터를 저장하고 사용하기 때문에 스트림즈 애플리케이션의 로컬 스토리지의 사용량 증가
//        네트워크, 브로커에 부하가 생김
//        작은 용량의 데이터일 경우에만 사용하는것이 좋음
//        다만 리파티셔닝을 거쳐 코파티셔닝이 되도록 할 필요가 없음
        GlobalKTable<String, String> addressGlobalTable = builder.globalTable(ADDRESS_GLOBAL_TABLE);
        KStream<String, String> orderStream = builder.stream(ORDER_STREAM);

//        조인
//        첫번째 파라미터 = 조인 대상
//        두번째 파라미터 = 조인 조건 GlobalKTable은 레코드 매칭시 KStream의 메세지 키와 메세지 값을 사용 가능하다
//        ex) KStream value == GlobalKTable key
//        세번째 파라미터 = 데이터 가공
        orderStream.join(addressGlobalTable,
                    (orderKey, orderValue) -> orderKey,
                    (order, address) -> order + " send to " + address)
                .to(ORDER_JOIN_STREAM);

        KafkaStreams streams;
        streams = new KafkaStreams(builder.build(), props);
        streams.start();
    }
}
