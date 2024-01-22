package com.example;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.config.ConfigResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

//어드민 api를 사용하여 카브카 브로커에 접속해 내부 옵션을 확인하거나 설정한다
public class KafkaAdminClient {
    private final static Logger logger = LoggerFactory.getLogger(KafkaAdminClient.class);

    private final static String BOOTSTRAP_SERVERS = "192.168.1.10:9092";

    public static void main(String[] args) throws Exception {
        Properties configs = new Properties();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
//        어드민 api 생성
        AdminClient admin = AdminClient.create(configs);

//        브로커 정보 조회
        logger.info("== Get broker information");
        for (Node node : admin.describeCluster().nodes().get()){
            logger.info("node: {}", node);

            ConfigResource cr = new ConfigResource(ConfigResource.Type.BROKER, node.idString());
            DescribeConfigsResult describeConfigs = admin.describeConfigs(Collections.singleton(cr));

            describeConfigs.all().get().forEach((broker, config) -> {
                config.entries().forEach(configEntry ->
                        logger.info(configEntry.name() + "= " + configEntry.value())
                );
            });
        }

//        파티션 정보 조회
        logger.info("== Get default num.partitions");
        for(Node node : admin.describeCluster().nodes().get()){
            ConfigResource cr = new ConfigResource(ConfigResource.Type.BROKER, node.idString());
            DescribeConfigsResult describeConfigs = admin.describeConfigs(Collections.singleton(cr));
            Config config = describeConfigs.all().get().get(cr);
            Optional<ConfigEntry> optionalConfigEntry = config.entries().stream()
                    .filter(v -> v.name().equals("num.partitions"))
                    .findFirst();
            ConfigEntry numPartitionConfig = optionalConfigEntry.orElseThrow(Exception::new);

            logger.info("{}", numPartitionConfig.value());
        }

//        토픽 목록 정보 조회
        logger.info("== Topic list");
        for(TopicListing topicListing : admin.listTopics().listings().get()){
            logger.info("{}", topicListing.toString());
        }

//        특정 토픽의 정보 조회
        logger.info("== test topic information");
        Map<String, TopicDescription> topicInformation = admin.describeTopics(Collections.singletonList("test")).all().get();
        logger.info("{}", topicInformation);

//        컨슈머 그릅 정보 조회
        logger.info("== Consumer group list");
        ListConsumerGroupsResult listConsumerGroups = admin.listConsumerGroups();
        listConsumerGroups.all().get().forEach(v -> {
            logger.info("{}", v);
        });

//        리소스 낭비를 방지하기 위해 명시적으로 종료
        admin.close();
    }
}
