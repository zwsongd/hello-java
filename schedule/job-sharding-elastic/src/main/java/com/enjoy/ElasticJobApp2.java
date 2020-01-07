package com.enjoy;

import com.cxytiandi.elasticjob.annotation.EnableElasticJob;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@EnableElasticJob
public class ElasticJobApp2 {
    public static void main(String[] args) {
        SpringApplication.run(ElasticJobApp2.class,args);
    }
}