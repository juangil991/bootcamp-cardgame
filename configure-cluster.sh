#eksctl create cluster --name raul-cardgame \
#  --region region-code \
#  --version 1.24 \
#  --vpc-private-subnets subnet-0064cbb5bf2086a41,subnet-0ce1127dbee4ada19,subnet-07f280948e955088d \
#  --without-nodegroup

eksctl create iamserviceaccount \
  --cluster=raul-cardgame \
  --namespace=kube-system \
  --name=aws-load-balancer-controller \
  --attach-policy-arn=arn:aws:iam::316078593388:policy/AWSLoadBalancerControllerIAMPolicy \
  --override-existing-serviceaccounts \
  --region us-east-1 \
  --approve

#eksctl create addon --name aws-ebs-csi-driver \
#  --cluster dairon-cardgame \
#  --service-account-role-arn arn:aws:iam::316078593388:role/AmazonEKS_EBS_CSI_DriverRole \
#  --force

kubectl apply -k "github.com/aws/eks-charts/stable/aws-load-balancer-controller//crds?ref=master"

helm install aws-load-balancer-controller eks/aws-load-balancer-controller  -n kube-system \
  --set clusterName=raul-cardgame \
  --set serviceAccount.create=false \
  --set serviceAccount.name=aws-load-balancer-controller

kubectl get deployment -n kube-system aws-load-balancer-controller
#kubectl logs -n kube-system deployment.apps/aws-load-balancer-controller

