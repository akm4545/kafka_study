package com.pipline;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Properties;

//실질적인 토픽의 데이터스트림 처리를 하는 클래스
//기능 정의 단계에서 그렸던 토폴로지를 토대로 코드를 작성하면 좋다
public class MetricStreams {

    private static KafkaStreams streams;

    public static void main(final String[] args){
        Runtime.getRuntime().addShutdownHook(new ShutdownThread());

//        스트림즈를 실행하기 위한 옵션들
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "metric-streams-application");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.10:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());

//        카프카 스트림즈의 토폴로지를 정의하기 위해 StreamsBuilder인스턴스 새로 생성
//        최초로 가져올 토픽인 metric.all을 기반으로 한 KStream 인스턴스를 생성
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> metrics = builder.stream("metric.all");
        
//        메시지 값을 기준으로 분기처리하기 위해 MetricJsonUtils를 통해 JSON 데이터에 적힌 메트릭 종류 값을 토대로 KStream을 두 갈래로 분기
//        branch 메서드를 사용하고 내부 파라미터로 Predicate 함수형 인터페이스를 사용하면 분기처리가 가능하다
//        KStream의 0번 배열에는 CPU 데이터, 1번 배열에는 메모리 데이터가 들어가도록 설정
        KStream<String, String>[] metricBranch = metrics.branch((key, value) -> MetricJsonUtils.getMetricName(value).equals("cpu"),
                (key, value) -> MetricJsonUtils.getMetricName(value).equals("memory")
        );

        metricBranch[0].to("metric.cpu");
        metricBranch[1].to("metric.memory");

//        분기처리된 데이터 중 전체 CPU 사용량이 50%가 넘어갈 경우를 필터링
        KStream<String, String> filteredCpuMetric = metricBranch[0].filter((key, value) -> MetricJsonUtils.getTotalCpuPercent(value) > 0.5);

//        전체 CPU 사용량의 50%가 넘는 데이터의 호스트와 timestamp 값을 조합하여 변환된 형태로 받는다
//        to 메서드에 정의된 metric.cpu.alert토픽에 전송된다
        filteredCpuMetric.mapValues(value -> MetricJsonUtils.getHostTimestamp(value)).to("metric.cpu.alert");

//        StreamBuilder 인스턴스로 정의된 토폴로지와 스트림즈 설정값을 토대로 KafkaStreams 인스턴스를 생성하고 실행한다 
        streams = new KafkaStreams(builder.build(), props);
        streams.start();
    }

//    카프카 스트림즈의 안전한 종료를 위해 셧다운 훅을 받을 경우 close()메서드를 호출하여 안전하게 종료
    static class ShutdownThread extends Thread {
        public void run() {
            streams.close();
        }
    }
}
