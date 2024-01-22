package com.example.kstreamKtableJoin;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;

import java.util.Properties;

public class KStreamJoinKTable {
    private static String APPLICATION_NAME = "order-join-application";

    private static String BOOTSTRAP_SERVERS = "192.168.1.10:9092";

    private static String ADDRESS_TABLE = "address";

    private static String ORDER_STREAM = "order";

    private static String ORDER_JOIN_STREAM = "order_join";

//    KTable과 KStream을 조인할때는 코파티셔닝이 되어 있어야 한다
//    코파티셔닝 = 파티션 객수가 같고 파티셔닝 전략이 같아야 한다
    public static void main(String[] args){
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_NAME);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();

//        address 토픽을 KTable 형식으로 가져온다
        KTable<String, String> addressTable = builder.table(ADDRESS_TABLE);
//        order 토픽을 KStream 형식으로 가져온다
        KStream<String, String> orderStream = builder.stream(ORDER_STREAM);

//        조인을 위해 KStream 인스턴스에 정의되어 있는 join 메서드 사용
//        첫번째 파라미터 = 조인을 수행할 대상
//        두번째 파라미터 = 동일한 메세지 키를 가진 데이터를 찾을경우 작업 정의
//        작업 후 조인으로 만들어진 데이터를 to 메서드(싱크 프로세서)를 사용하여 order_join 토픽에 적재
//        조인할때 사용했던 메시지 키는 조인된 데이터의 메세지 키로 들어간다
        orderStream.join(addressTable,
                        (order, address) -> order + " send to " + address)
                .to(ORDER_JOIN_STREAM);

        KafkaStreams streams;
        streams = new KafkaStreams(builder.build(), props);
        streams.start();
    }
}
