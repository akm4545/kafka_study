package com.pipline;

import com.google.gson.Gson;
import com.pipline.config.ElasticSearchSinkConnectorConfig;
import org.apache.http.HttpHost;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

//실제 적재 로직이 들어가는 곳
public class ElasticSearchSinkTask extends SinkTask {

    private final Logger logger = LoggerFactory.getLogger(ElasticSearchSinkTask.class);

    private ElasticSearchSinkConnectorConfig config;

    private RestHighLevelClient esClient;

    @Override
    public String version() {
        return "1.0";
    }

    @Override
    public void start(Map<String, String> props) {
        try{
            config = new ElasticSearchSinkConnectorConfig(props);
        }catch (ConfigException e){
            throw new ConnectException(e.getMessage(), e);
        }

//        엘라스틱서치에 적재하기 위해 RestHighLevelClient 인스턴스를 생성
//        사용자가 입력한 호스트와 포트를 기반으로 생성 (map에서 사용자가 입력한 설정값을 key 로 꺼냄)
        esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost(config.getString(config.ES_CLUSTER_HOST),
                        config.getInt(config.ES_CLUSTER_PORT))));
    }

    @Override
    public void put(Collection<SinkRecord> records) {
//        레코드들이 존재할때
        if(records.size() > 0){
//            bulkRequest = 1개 이상의 데이터들을 묶음으로 엘라스틱서치로 전송할 때 사용
            BulkRequest bulkRequest = new BulkRequest();

            for(SinkRecord record : records){
                Gson gson = new Gson();
                Map map = gson.fromJson(record.value().toString(), Map.class);

//                레코드들을 bulkRequest에 추가 파라미터로 인덱스 이름, Map타입의 데이터가 필요  
                bulkRequest.add(new IndexRequest(config.getString(config.ES_INDEX))
                        .source(map, XContentType.JSON));

                logger.info("record : {}", record.value());
            }
            
//            bulkRequest에 담긴 데이터들을 bulkAsync로 전송
//            해당 메서드를 사용해서 전송하면 결과를 비동기로 받아볼 수 있다
            esClient.bulkAsync(bulkRequest, RequestOptions.DEFAULT, new ActionListener<BulkResponse>() {
                @Override
                public void onResponse(BulkResponse bulkItemResponses) {
//                    정상 응답, 실패 응답에 따라 로그를 남긴다
                    if(bulkItemResponses.hasFailures()){
                        logger.error(bulkItemResponses.buildFailureMessage());
                    }else{
                        logger.info("bulk save success");
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    logger.error(e.getMessage(), e);
                }
            });
        }
    }

//    일정 주기마다 호출 여기서는 put메서드에서 레코드들을 받아서 엘라스틱서치로 보내므로 flush에서 추가 작성 로직은 없다
    @Override
    public void flush(Map<TopicPartition, OffsetAndMetadata> offsets) {
        logger.info("flush");
    }

//    커넥터 종료 시 esClient 종료 
    @Override
    public void stop() {
        try{
            esClient.close();
        }catch (IOException e){
            logger.info(e.getMessage(), e);
        }
    }
}
