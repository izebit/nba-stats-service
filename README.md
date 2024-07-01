## NBA-Stats-Service

## Rest API Specification
there are 3 endpoints:
1. POST /api/v1/stats/seasons/{season_id}/teams/{team_id}/players/{player_id}  
   send stats data to the service
2. GET /api/v1/stats/seasons/{season_id}/teams/{team_id}/players/{player_id}  
   get stat data for specific player
3. GET /api/v1/stats/seasons/{season_id}/teams/{team_id}  
   get stat data for specific team

The file with open api spec is here

## How it works




## Build  
The service works on AWS. To create all required components, run the cloudformation template.  
To run it, there must be an existing s3 bucket.

```shell
mvn clean package
aws cloudformation package --template-file cloudformation.yaml --s3-bucket <s3-backet> --output-template-file target/out.yml
aws cloudformation deploy --template-file target/out.yml --stack-name nba-stats-stack --capabilities CAPABILITY_NAMED_IAM
```

The output parameter `ApiEndpoint` is a url of the running service.
