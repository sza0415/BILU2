package org.sza.bilu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("org.sza.bilu.mapper")
public class BiluApplication {

    public static void main(String[] args) {
        SpringApplication.run(BiluApplication.class, args);
    }

}
