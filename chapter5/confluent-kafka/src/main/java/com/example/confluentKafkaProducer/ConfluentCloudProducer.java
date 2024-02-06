package com.example.confluentKafkaProducer;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ConfluentCloudProducer {
    private final static Logger logger = LoggerFactory.getLogger(ConfluentCloudProducer.class);

//    토픽명
    private final static String TOPIC_NAME = "test.log";

//    컨플루언트클라우드로 만든 카프카 클러스터의 호스트와 포트번호
    private final static String BOOTSTRAP_SERVERS = "pkc-test.ap-northeast-1.aws.confluent.cloud:9092";

//    SASL 보안 접속을 하기 위한 보안 프로토콜들을 선언
//    컨플루언트 클라우드에서 제공하는 설정값에 따라서 클라이언트 값으로 설정해야 정상적으로 데이터를 전송할 수 있다
//    컨플루언트 클라우드 메시지 키와 메시지 값을 String으로 전송하는 코드이므로 Confluent Cloud Schema Registry 설정값은 적용하지 않는다
//    이전에 발급받은 api 키와 시크릿 문자열을 sasl.jaas.config 설정으로 정확히 입력해야 접속 할 수 있다
    private final static String SECURITY_PROTOCOL = "SASL_SSL";

    private final static String JAAS_CONFIG = "org.apache.kafka.common.security.plain.PlainLoginModule   required username=\"test\"   password=\"test\";";

    private final static String SSL_ENDPOINT = "https";

    private final static String SASL_MECHANISM = "PLAIN";

    public static void main(String[] args){
        Properties configs = new Properties();

        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

//        선언한 보안 프로토콜을 Properties에 설정
        configs.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SECURITY_PROTOCOL);
        configs.put(SaslConfigs.SASL_JAAS_CONFIG, JAAS_CONFIG);
        configs.put(SaslConfigs.SASL_MECHANISM, SASL_MECHANISM);
        configs.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, SSL_ENDPOINT);

        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(configs);

//        메세지 키, 값
        String messageKey = "helloKafka";
        String messageValue = "helloConfluentCloud";

        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, messageKey, messageValue);

        try {
//            프로듀서가 전송한 데이터가 정상적으로 브로커에 적재되었는지 여부 확인을 위해 RecordMetadata로 리턴받음
            RecordMetadata metadata = producer.send(record).get();
//           데이터가 정상 적재되었다면 어느 파티션에 어떤 오프셋으로 저장되었는지 결과를 보기 위해 로그 출력
            logger.info(metadata.toString());
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        } finally {
//            프로듀서에 존재하는 배치를 모두 전송한 이후에 안전하게 종료
            producer.flush();
            producer.close();
        }

//        for(int i=0; i<1000; i++){
//            String messageKeyInFor = "helloKafka";
//            String messageValueInFor = "helloConfluentCloud" + i;
//
//            ProducerRecord<String, String> recordInFor = new ProducerRecord<>(TOPIC_NAME, messageKeyInFor, messageValueInFor);
//
//            try{
//                RecordMetadata metadata = producer.send(record).get();
//                logger.info(metadata.toString());
//            }catch (Exception e){
//                logger.error(e.getMessage(), e);
//            }finally {
//                producer.flush();
//            }
//        }
//
//        producer.close();
    }
}
