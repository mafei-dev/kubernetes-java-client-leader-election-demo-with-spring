package com.example.leaderelectiondemo;

import io.kubernetes.client.extended.leaderelection.LeaderElectionConfig;
import io.kubernetes.client.extended.leaderelection.LeaderElector;
import io.kubernetes.client.extended.leaderelection.resourcelock.LeaseLock;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.ClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

public class KubernetesJavaClientLeaderElection {
    private static final Logger log = LoggerFactory.getLogger(KubernetesJavaClientLeaderElection.class);

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                KubernetesJavaClientLeaderElection.leaderElection("default", "test-app-5");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        Thread.sleep(200_000);
        leaderElector.close();
        thread.interrupt();
    }

    private static LeaderElector leaderElector;

    private static void leaderElection(String appNamespace, String appName) throws IOException {
        ApiClient client = ClientBuilder.defaultClient();

        String lockHolderIdentityName = UUID.randomUUID().toString(); // Anything unique
        System.out.println("lockHolderIdentityName = " + lockHolderIdentityName);
        LeaseLock lock = new LeaseLock(appNamespace, appName, lockHolderIdentityName, client);

        LeaderElectionConfig leaderElectionConfig =
                new LeaderElectionConfig(
                        lock, Duration.ofSeconds(300),
                        Duration.ofSeconds(15),
                        Duration.ofSeconds(5)
                );
        leaderElector = new LeaderElector(leaderElectionConfig);
        leaderElector.run(
                () -> {
                    System.out.println("Do something when getting leadership.");
                },
                () -> {
                    System.out.println("Do something when losing leadership.");
                }, s -> {
                    System.out.println("leader-hock = " + s);
                });
        System.out.println("done");
    }
}
