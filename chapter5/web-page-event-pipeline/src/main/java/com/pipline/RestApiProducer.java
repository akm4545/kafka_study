package com.pipline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
public class RestApiProducer {
    public static void main(String[] args){
        SpringApplication.run(RestApiProducer.class, args);
    }
}
