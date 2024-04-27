./mvnw clean install -DskipTests=true
docker build -t mafeidev/k8s-leader-election-demo-spring-2:1.0.1 .
docker push mafeidev/k8s-leader-election-demo-spring-2:1.0.1