package io.spring.springbatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * spring boot 5.xx 이상부터는 @EnableBatchProcessing 생략 가능
 * 다만 DefaultBatchConfigruation 존재하지 않는 경우에만 BatchAutoConfiguration 생략 가능
 */
@SpringBootApplication
public class SpringBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchApplication.class, args);
    }

}
