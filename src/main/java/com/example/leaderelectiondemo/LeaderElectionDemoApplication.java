package com.example.leaderelectiondemo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@Slf4j
@EnableAsync
@RestController
@RequiredArgsConstructor
public class LeaderElectionDemoApplication {

    private final ApplicationContext applicationContext;

    @GetMapping("down")
    public void index() {
        System.out.println("LeaderElectionDemoApplication.index");
        SpringApplication.exit(applicationContext);
    }

    public static void main(String[] args) {
        SpringApplication.run(LeaderElectionDemoApplication.class, args);
    }


}
