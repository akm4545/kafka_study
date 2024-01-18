package com.example.kafkaProducerCustomPartitioner;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.InvalidRecordException;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.utils.Utils;

import java.util.List;
import java.util.Map;

//특정 데이터를 특정 파티션으로 보내야 할때 사용하는 커스텀 파티셔너
public class CustomPartitioner implements Partitioner {
//    레코드를 기반으로 파티션을 정하는 로직
//    리턴값이 파티션 번호
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
//        키 유무를 파악하여 키가 없으면 비정상 데이터로 간주
        if(keyBytes == null){
            throw new InvalidRecordException("Need message key");
        }

//        키가 Pangyo일 경우 0번 파티션에 적재
        if(((String)key).equals("Pangyo")){
            return 0;
        }

        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        int numPartitions = partitions.size();

//        이외의 메세지가 들어올 경우 해시값을 지정하여 특정 파티션에 매칭되도록 설정
        return Utils.toPositive(Utils.murmur2(keyBytes)) % numPartitions;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
