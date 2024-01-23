package com.example.simpleSourceConnector;

import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SingleFileSourceTask extends SourceTask {

    private Logger logger = LoggerFactory.getLogger(SingleFileSourceTask.class);

    public final String FILENAME_FIELD = "filename";

    public final String POSITION_FIELD = "position";

    private Map<String, String> fileNamePartition;

    private Map<String, Object> offset;

    private String topic;

    private String file;

    private long position = -1;


    @Override
    public String version() {
        return "1.0";
    }

    @Override
    public void start(Map<String, String> props) {
        try{
            SingleFileSourceConnectorConfig config = new SingleFileSourceConnectorConfig(props);
            topic = config.getString(SingleFileSourceConnectorConfig.TOPIC_NAME);
            file = config.getString(SingleFileSourceConnectorConfig)
        }catch (Exception e){

        }
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        return null;
    }

    @Override
    public void stop() {

    }
}
