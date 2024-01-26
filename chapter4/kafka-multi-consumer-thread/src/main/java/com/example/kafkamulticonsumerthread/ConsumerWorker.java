package com.example.kafkamulticonsumerthread;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.CharBuffer;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ConsumerWorker implements Runnable{

    private final static Logger logger = LoggerFactory.getLogger(ConsumerWorker.class);

    private Properties prop;

    private String topic;

    private String threadName;

    private KafkaConsumer<String, String> consumer;

//    KafkaConsuemr 인스턴스를 생성하기 위해 필요한 변수를 컨슈머 스레드 생성자 변수로 받는다
    ConsumerWorker(Properties prop, String topic, int number){
        this.prop = prop;
        this.topic = topic;
        this.threadName = "consumer-thread-" + number;
    }

    @Override
    public void run() {
//        KafkaConsumer 클래스는 스레드 세이프하지 않다.
//        이 때문에 스레드별로 KafkaConsumer 인스턴스를 별개로 만들어서 운영해야만 한다
//        만약 KafkaConsumer 인스턴스를 여러 스레드에서 실행하면 ConcurrentModificationExceoption 예외가 발생한다
        consumer = new KafkaConsumer<String, String>(prop);
        consumer.subscribe(Arrays.asList(topic));

        while (true){
//            poll메서드를 통해 리턴받은 레코드들을 처리한다
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

            for(ConsumerRecord<String, String> record : records){
                logger.info("{}", record);
            }

            consumer.commitSync();
        }
    }
}
