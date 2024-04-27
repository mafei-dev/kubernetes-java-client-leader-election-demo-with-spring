./mvnw clean package -DskipTests=true
docker build -t mafeidev/k8s-leader-election-spring-demo:1.0.0 .
docker push mafeidev/k8s-leader-election-spring-demo:1.0.0