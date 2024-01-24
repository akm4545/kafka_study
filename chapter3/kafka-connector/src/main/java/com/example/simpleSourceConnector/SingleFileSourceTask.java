package com.example.simpleSourceConnector;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.POST;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

//커넥터, 테스크, 커넥터 설정 클래스로 구성하였으며 
//해당 클래스 자체로는 실행 할 수 없고 jar파일로 생성 후 커넥트 플러그인 디렉토리에 넣어야 한다
//jar파일을 커넥트 지정 플러그인 디렉토리에 넣은 이후 커넥트는 재시작 해야 한다
public class SingleFileSourceTask extends SourceTask {

    private Logger logger = LoggerFactory.getLogger(SingleFileSourceTask.class);

//    파일 이름과 읽은 지점을 오프셋 스토리지에 저장하기 위해 값 정의 
//    해당 2개으 ㅣ키를 기준으로 오프셋 스토리지에 읽은 위치 저장
    public final String FILENAME_FIELD = "filename";

    public final String POSITION_FIELD = "position";

//    오프셋 스토리지에 데이터를 저장하고 읽을 때는 Map 자료구조를 사용
    private Map<String, String> fileNamePartition;

    private Map<String, Object> offset;

    private String topic;

    private String file;

//    읽은 파일의 위치를 커넥터 멤버 변수로 지정
//    커넥터가 최초 실행시 오프셋 스토리지에서 마지막으로 읽은 파일 위치를 position 변수에 선언하여 중복적재를 방지 할 수 있다
//    처음 읽는 파일이라면 오프셋 스토리지에 기록이 없으므로 0으로 설정하여 처음부터 읽도록 한다
    private long position = -1;


    @Override
    public String version() {
        return "1.0";
    }

    @Override
    public void start(Map<String, String> props) {
        try{
//            커넥터 실행 시 받은 설정값을 SingleFileSourceConnectorConfig로 선언하여 사용
//            해당 클래스는 토픽 이름과 읽을 파일 이름 설정값을 사용함
//            토픽 이름과 파일명은 멤버 변수로 선언되어 start메서드에서 초기화 후에 다른 메서드에서도 사용 가능
            SingleFileSourceConnectorConfig config = new SingleFileSourceConnectorConfig(props);
            topic = config.getString(SingleFileSourceConnectorConfig.TOPIC_NAME);
            file = config.getString(SingleFileSourceConnectorConfig.DIR_FILE_NAME);

//            오프셋 스토리지에서 현재 읽고자 하는 파일 정보를 가져온다
//            오프셋 스토리지는 실제로 데이터가 저장되는 곳
//            단일 모드 커넥트는 로컬 파일로 저장
//            분산 모드 커넥트는 내부 토픽에 저장
//            offset이 null이면 읽고자 하는 데이터가 없다는 뜻이다
//            null이 아니라면 한 번이라도 커넥터를 통해 해당 파일을 처리했다는 뜻이다
            fileNamePartition = Collections.singletonMap(FILENAME_FIELD, file);
            offset = context.offsetStorageReader().offset(fileNamePartition);

            if(offset != null){
//                get메서드를 통해 파일의 마지막 읽은 위치를 가져온다
                Object lastReadFileOffset = offset.get(POSITION_FIELD);

//                오프셋 스토리지에서 가져온 마지막 처리 시점을 position에 할당
//                해당 작업을 통해 커넥터가 재시작 되더라도 데이터의 중복, 유실을 막을 수 있다
                if(lastReadFileOffset != null){
                    position = (Long) lastReadFileOffset;
                }
            }else{
//                null이면 처리한 적이 없는 데이터이므로 처음부터 처리하기 위해 0을 할당한다
                position = 0;
            }
        }catch (Exception e){
            throw new ConnectException(e.getMessage(), e);
        }
    }

//    테스크가 시작한 이후 지속적으로 데이터를 가져오기 위해 반복 호출되는 메서드
    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        List<SourceRecord> results = new ArrayList<>();

        try {
            Thread.sleep(1000);

            List<String> lines = getLines(position);

            if(lines.size() > 0){
                lines.forEach(line -> {
//                    오프셋 저장
                    Map<String, Long> sourceOffset = Collections.singletonMap(POSITION_FIELD, ++position);
//                    처리한 데이터를 레코드 객체로 만듦
                    SourceRecord sourceRecord = new SourceRecord(fileNamePartition, sourceOffset, topic, Schema.STRING_SCHEMA, line);

//                    처리한 데이터를 담는다
                    results.add(sourceRecord);
                });
            }

//            토픽으로 레코드들을 내보내기
            return results;
        }catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ConnectException(e.getMessage(), e);
        }
    }

//    파일에서 한줄씩 읽어오는 메서드
    private List<String> getLines(long readLine) throws Exception{
        BufferedReader reader = Files.newBufferedReader(Paths.get(file));

        return reader.lines().skip(readLine).collect(Collectors.toList());
    }

    @Override
    public void stop() {
    }
}
