source ./docker-push.sh

kubectl apply -f ./k8s/RBAC.yaml
kubectl apply -f ./k8s/deployment.yaml

