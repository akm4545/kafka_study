package com.pipeline.consumer;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.module.Configuration;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//컨슈머가 싱핼될 스레드를 정의하기 위해 Runnable 구현
public class ConsumerWorker implements Runnable{
    
    private final Logger logger = LoggerFactory.getLogger(ConsumerWorker.class);

//    컨슈머의 poll 메서드를 통해 전달받은 데이터를 임시 저장하는 버퍼 선언
//    static으로 선언하여 다수의 스레드가 만들어지더라도 동일 변수에 접근
//    다수 스레드가 동시 접근할 수 있으므로 멀티 스레드 환경에서 안전하게 사용할 수 있는 ConcurrentHashMap로 구현
//    파티션 번호와 메시지값들이 들어간다
    private static Map<Integer, List<String>> bufferString = new ConcurrentHashMap<>();

//    오프셋 값을 저장하고 파일 이름을 저장할 때 오프셋 번호를 붙이는데 사용
    private static Map<Integer, Long> currentFileOffset = new ConcurrentHashMap<>();

    private final static int FLUSH_RECORD_COUNT = 10;

    private Properties prop;

    private String topic;

    private String threadName;

    private KafkaConsumer<String, String> consumer;

    public ConsumerWorker(Properties prop, String topic, int number){
        logger.info("Generate ConsumerWorker");

        this.prop = prop;
        this.topic = topic;
        
//        로깅 시에 식별하기 편하도록 스레드에 번호를 붙임
        this.threadName = "consumer-thread-" + number;
    }

    @Override
    public void run() {
//        토픽 구독부분
        Thread.currentThread().setName(threadName);
        consumer = new KafkaConsumer<String, String>(prop);
        consumer.subscribe(Arrays.asList(topic));

        try {
            while (true){
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

                for(ConsumerRecord<String, String> record : records){
//                    데이터를 버퍼에 쌓음
                    addHdfsFileBuffer(record);
                }

//                버퍼에 쌓인 데이터가 일정 개수 이상 쌓였을 경우 HDFS에 저장하는 로직 수행
//                파티션 식별 assignment
                saveBufferToHdfsFile(consumer.assignment());
            }
        }catch (WakeupException e){
            logger.warn("Wakeup consumer");
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }finally {
            consumer.close();
        }
    }

//    레코드를 받아서 메시지 값을 버퍼에 넣음
    private void addHdfsFileBuffer(ConsumerRecord<String, String> record) {
//        getOrDefault = 찾는 키가 있으면 반환하고 없으면 2번째 인자로 들어간 default값을 반황
        List<String> buffer = bufferString.getOrDefault(record.partition(), new ArrayList<>());
//        가져온 버퍼에 레코드 밸류를 적재한다
        buffer.add(record.value());
//        해당 파티션을 키값으로 저장
        bufferString.put(record.partition(), buffer);

//        버퍼 사이즈가 1이면 버퍼의 처음 오프셋임
//        오프셋 번호를 관리하면 추후 파일 저장 시 파티션 이름과 오프셋 번호를 붙여서 저장 가능
//        이슈 발생 시 파티션과 오프셋에 대한 정보를 알 수 있다는 장점이 있다
        if(buffer.size() == 1){
//            해당 파티션의 오프셋 번호를 저장
            currentFileOffset.put(record.partition(), record.offset());
        }
    }

//    버퍼의 데이터가 flush 될 만큼 개수가 충족되었는지 확인하는 메서드 호출
//    컨슈머로부터 Set<TopicPartition> 정보를 받아 컨슈머 스레드에 할당된 파티션에만 접근
    private void saveBufferToHdfsFile(Set<TopicPartition> partitions){
        partitions.forEach(p -> checkFlushCount(p.partition()));
    }

//    파티션 번호의 버퍼를 확인하여 flush수행
    private void checkFlushCount(int partitionNo){
        if(bufferString.get(partitionNo) != null){
//            해당 파티션에 버퍼가 다 차면 save 호출
            if(bufferString.get(partitionNo).size() > FLUSH_RECORD_COUNT - 1){
                
                save(partitionNo);
            }
        }
    }

//    HDFS적재 수행
//    컨슈머 멀티 스레드 환경은 동일 데이터의 동시 접근에 유의해야 한다
//    여러 개의 컨슈머가 동일한 HDFS 파일에 접근을 시도하면 교착상태에 빠질 위험이 있다
//    따라서 파티션 번호에 따라 HDFS 파일을 따로 저장하는 로직을 사용
    private void save(int partitionNo){
        if(bufferString.get(partitionNo).size() > 0){
            try {
//                파일이름 = color-{파티션번호}-{오프셋번호}.log
//                이렇게 저장해 두면 파티션 번호, 오프셋 번호를 파일 이름만 보고도 알 수 있기 때문에 장애 시 복구 시점이 명확함
                String fileName = "/data/color-" + partitionNo + "-" + currentFileOffset.get(partitionNo) + ".log";
//                HDFS적재를 위한 설정 수행
                org.apache.hadoop.conf.Configuration configuration = new org.apache.hadoop.conf.Configuration();
                configuration.set("fs.defaultFS", "hdfs://localhost:9000");

                FileSystem hdfsFileSystem = FileSystem.get(configuration);
                FSDataOutputStream fileOutputStream = hdfsFileSystem.create(new Path(fileName));
//                버퍼에 쌓인 데이터를 fileOutputStream에 저장 
                fileOutputStream.writeBytes(StringUtils.join(bufferString.get(partitionNo), "\n"));
                fileOutputStream.close();

//                적재 완료시 버퍼 초기화
                bufferString.put(partitionNo, new ArrayList<>());
            }catch (Exception e){
                logger.error(e.getMessage(), e);
            }
        }
    }

    private void saveRemainBufferToHdsfFile(){
//        버퍼의 데이터 저장
        bufferString.forEach((partitionNo, v) -> this.save(partitionNo));
    }

//    셧다운 훅이 발생 시 실행
    public void stopAndWakeup(){
        logger.info("stopAndWakeup");
//        consumer 종료
        consumer.wakeup();
//        남은 버퍼의 데이터를 모두 저장
        saveRemainBufferToHdsfFile();
    }
}
