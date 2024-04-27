package com.example.leaderelectiondemo;

import io.kubernetes.client.extended.leaderelection.LeaderElectionConfig;
import io.kubernetes.client.extended.leaderelection.LeaderElector;
import io.kubernetes.client.extended.leaderelection.resourcelock.LeaseLock;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Configuration
public class K8SJavaClient {

    private LeaderElector leaderElector;
    private final AtomicBoolean isExit = new AtomicBoolean(false);

    @Value("${demo.name:${spring.application.name}}")
    private String appName;
    @Value("${demo.namespace:default}")
    private String appNamespace;

    private void leaderElection() throws IOException {
        log.debug("K8SJavaClient.leaderElection:start");
        final ApiClient client = ClientBuilder.defaultClient();
        log.debug("LeaderElectionDemoApplication.leaderElection");
        final String lockHolderIdentityName = UUID.randomUUID().toString(); // Anything unique
        System.out.println("LockHolderIdentityName = " + lockHolderIdentityName);
        final LeaseLock lock = new LeaseLock(appNamespace, appName, lockHolderIdentityName, client);


        final LeaderElectionConfig leaderElectionConfig =
                new LeaderElectionConfig(
                        lock, Duration.ofSeconds(60),
                        Duration.ofSeconds(30),
                        Duration.ofSeconds(10)
                );
        leaderElector = new LeaderElector(leaderElectionConfig);
        leaderElector.run(
                () -> {
                    log.info("Do something when getting leadership.");
                },
                () -> {
                    log.info("Do something when losing leadership.");
                }, s -> {
                    log.info("leader-hock = " + s);
                });
        //If the application is going to shut down, skip invoke retrying.
        //If the application is shut down, isExit value is updated in @PreDestroy method.
        if (!this.isExit.get()) {
            log.trace("Leader Election is retried due to that the process is finished for some reason without a shut down.");
            this.leaderElection();
        } else {
            log.trace("Invoking retrying is skipped due to that the application is going to shut down.");
        }

    }

    Thread thread;

    @PreDestroy
    public void preDestroy() throws InterruptedException {
        log.debug("LeaderElectionDemoApplication.preDestroy");
        this.isExit.set(true);
        this.leaderElector.close();
        this.thread.interrupt();

    }


    @PostConstruct
    public void init() throws IOException {
        log.debug("onApplicationEvent:init");
        thread = new Thread(() -> {
            try {
                this.leaderElection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.setName("LeaderElection-Invoker");
        thread.start();
    }
}
