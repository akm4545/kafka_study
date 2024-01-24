package com.example.simpleSinkConnector;

import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class SingleFileSinkTask extends SinkTask {

    private SingleFileSinkConnectorConfig config;

    private File file;

    private FileWriter fileWriter;

    @Override
    public String version() {
        return "1.0";
    }

//    커넥터 실행시 설정한 옵션을 토대로 리소스 초기화
    @Override
    public void start(Map<String, String> props) {
        try{
            config = new SingleFileSinkConnectorConfig(props);
            file = new File(config.getString(config.DIR_FILE_NAME));
            fileWriter = new FileWriter(file, true);
        }catch (Exception e){
            throw new ConnectException(e.getMessage(), e);
        }
    }

//  일정 주기로 토픽의 데이터를 가져오는 메서드
//    데이터 저장 코드를 작성
//    SinkRecord는 토픽의 레코드이며 토픽, 파티션, 타임스탬프 정보를 포함한다
    @Override
    public void put(Collection<SinkRecord> records) {
        try{
            for(SinkRecord record : records){
//                여기서 저장하는 데이터는 레코드의 메시지 값이므로 value 메서드로 리턴받은 객체를 String 포멧으로 저장한다
                fileWriter.write(record.value().toString() + "\n");
            }
        }catch (IOException e){
            throw new ConnectException(e.getMessage(), e);
        }
    }

//    put 메서드는 버퍼에 데이터를 저장하고
//    flush 메서드가 실질적으로 파일 시스템에 데이터를 저장한다
    @Override
    public void flush(Map<TopicPartition, OffsetAndMetadata> offsets){
        try{
            fileWriter.flush();
        }catch (IOException e){
            throw new ConnectException(e.getMessage(), e);
        }
    }

//    사용자가 테스크를 종료할 경우 열고 있던 파일을 안전하게 닫는다
    @Override
    public void stop() {
        try{
            fileWriter.close();
        }catch (IOException e){
            throw new ConnectException(e.getMessage(), e);
        }
    }
}
