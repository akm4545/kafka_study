package com.example.kafkaConsumerWithMultiWorkerThread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.awt.datatransfer.StringSelection;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//스레드를 사용하면 데이터를 병렬 처리하므로 속도의 이점을 얻을 수 있다
//다만 데이터 처리가 끝나지 않았음에도 불구하고 커밋을 하기 때문에 리밸런싱, 컨슈머 장애 시에 데이터가 유실될 수 있다
//레코드 처리의 역전현상이 발생할 수 있다
//레코드 처리의 역전현상 = 스레드별로 데이터 처리 속도가 다를 수 있으므로 처음 처리한 데이터보다 나중에 처리한 데이터가 더 빨리 완료될 수 있다
public class ConsumerWithMultiWorkerThread {

    private final static Logger logger = LoggerFactory.getLogger(ConsumerWithMultiWorkerThread.class);

    private final static String TOPIC_NAME = "test";

    private final static String BOOTSTRAP_SERVERS = "192.168.1.10:9092";

    private final static String GROUP_ID = "test-group";

    public static void main(String[] args){
        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringSelection.class.getName());
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        configs.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 10000);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(configs);
        consumer.subscribe(Arrays.asList(TOPIC_NAME));

//        쓰레드를 실행하기 위해 ExecutorService사용
//        레코드 출력이 완료되면 쓰레드를 종료하도록 newCachedThreadPool 사용
//        newCachedThreadPool = 필요한 만큼 스레드 풀을 늘려서 스레드를 실행하는 방식
//        짧은 시간의 생명주기를 가진 쓰레드에서 유용
        ExecutorService executorService = Executors.newCachedThreadPool();

        while (true){
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(10));

            for(ConsumerRecord<String, String> record : records){
//                쓰레드 작업할 객체 생성
                ConsumerWorker worker = new ConsumerWorker(record.value());
//                작업 실행
                executorService.execute(worker);
            }
        }

    }
}
