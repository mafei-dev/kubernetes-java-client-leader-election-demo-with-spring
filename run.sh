source ./docker-push.sh

kubectl apply -f RBAC.yaml
kubectl apply -f deployment.yaml

