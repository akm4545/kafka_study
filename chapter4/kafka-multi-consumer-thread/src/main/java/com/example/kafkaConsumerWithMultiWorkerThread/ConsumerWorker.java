package com.example.kafkaConsumerWithMultiWorkerThread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//데이터를  처리하는 쓰레드
public class ConsumerWorker implements Runnable{

    private final static Logger logger = LoggerFactory.getLogger(ConsumerWorker.class);

    private String recordValue;

    ConsumerWorker(String recordValue){
        this.recordValue = recordValue;
    }

    @Override
    public void run() {
        logger.info("thread:{}\trecord:{}", Thread.currentThread().getName(), recordValue);
    }
}
