#!/usr/bin/env bash

mvn clean package
aws cloudformation delete-stack --stack-name nba-stats-stack
aws cloudformation package --template-file cloudformation.yaml --s3-bucket izebit-lambda-functions --output-template-file target/out.yml
aws cloudformation deploy --template-file target/out.yml --stack-name nba-stats-stack --capabilities CAPABILITY_NAMED_IAM