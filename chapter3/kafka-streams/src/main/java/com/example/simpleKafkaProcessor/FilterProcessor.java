package com.example.simpleKafkaProcessor;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;

//스트림 프로세서 클래스를 생성하기 위해 Processor 인터페이스 구현
public class FilterProcessor implements Processor<String, String> {

//    ProcessorContext는 프로세서에 대한 정보를 담고있다 
//    ProcessorContext로 생성된 인스턴스로 현재 처리 중인 토폴로지의 토픽정보, 애플리케이션 아이디를 조회 가능하다
//    또한 schedule, forward, commit 등의 프로세싱 처리에 필요한 메서드를 사용할 수도 있다
    private ProcessorContext context;

//    스트림 프로세서의 생성자
//    프로세싱 처리에 필요한 리소스를 선언하는 구문이 들어갈 수 있다
    @Override
    public void init(ProcessorContext context) {
        this.context = context;
    }

//    프로세싱 로직이 들어가는 부분
//    1개의 레코드를 받는 것을 가정하여 코드를 짜면 된다
//    필터링된 데이터의 경우 forward 메서드를 사용하여 다음 토폴리지(다음 프로세서) 로 넘어가도록 한다
//    처리가 완료된 이후에는 commit 을 호출하여 명시적으로 데이터가 처리되었음을 선언한다
    @Override
    public void process(String key, String value) {
        if(value.length() > 5){
            context.forward(key, value);
        }

        context.commit();
    }

//    프로세서가 종료되기 전에 호출되는 메서드 
//    리소스를 해제하는 구문을 넣는다
    @Override
    public void close() {

    }
}
