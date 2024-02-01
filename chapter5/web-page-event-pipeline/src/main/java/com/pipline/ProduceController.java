package com.pipline;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
//cors 해제
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProduceController {
    private final Logger logger = LoggerFactory.getLogger(ProduceController.class);

//    카프카 템플릿 생성
    private final KafkaTemplate<String, String> kafkaTemplate;

    public ProduceController(KafkaTemplate<String, String> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping("/api/select")
    public void selectColor(
            @RequestHeader("user-agnet") String userAgentName,
            @RequestParam(value = "color") String colorName,
            @RequestParam(value = "user") String userName){
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
        Date now = new Date();
        Gson gson = new Gson();
        UserEventVO userEventVO = new UserEventVO(sdfDate.format(now), userAgentName, colorName, userName);
        String jsonColorLog = gson.toJson(userEventVO);
        
//        템플릿으로 데이터 전송
//        정상전송 여부 확인을 위해 addCallback 추가
        kafkaTemplate.send("select-color", jsonColorLog).addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                logger.info(result.toString());
            }
            @Override
            public void onFailure(Throwable ex) {
                logger.error(ex.getMessage(), ex);
            }
        });
    }

}
