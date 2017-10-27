# employeechallengejava
GET Service with get all employees  using AWS Lambda + API Gateway + DynamoDB + IAM  and JAVA.

The project assumes that you have the AWS tool kit installed in your eclipse IDE OR you are conversant with AWS CLI.
The project assumes that you are conversant with NoSql DB concepts.

This project is the implmentation of an employee management.
This microservice ingests the external data in Dynamo DB ( using JSON parser and put items API )
This micro service will  then get all the employees in the database ( called as scan )

The Handler is the lamda implementationin JAVA.
The Employee class is the Dynamo DB wrapper to map the DB to class.

The API gateway is configured with this lambda function in my AWS instance.
The template used for API gateway is passthrough i.e. all the infor from request will be sent to Lambda.
API gateway is created with /getlist resource

The live service/ REST is on this URL : https://vsdc77v5ph.execute-api.us-west-2.amazonaws.com/prod/getlist
