#!/bin/bash
set -e # Stops the script if any command fails

# Set dummy credentials for LocalStack interactions
export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
export AWS_DEFAULT_REGION=ap-south-1 # Use a dummy region

# --- Your original commands ---

# If the stack deployment failed, this next command will print the actual error
aws --endpoint-url=http://localhost:4566 cloudformation deploy \
    --stack-name patient-management \
    --template-file "./cdk.out/localstack.template.json"

aws --endpoint-url=http://localhost:4566 elbv2 describe-load-balancers \
    --query "LoadBalancers[0].DNSName" --output text