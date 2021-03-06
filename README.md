# wasupu boinet [![Build Status](https://travis-ci.org/wasupu/wasupu-boinet.svg?branch=master)](https://travis-ci.org/wasupu/wasupu-boinet)

![Image of the boinet](https://raw.githubusercontent.com/wasupu/wasupu-boinet/master/boinet-pic.png)

## Requirements:

* Maven
* Docker

## Environment variables required for deployment:

* AWS_DEFAULT_REGION
* AWS_DOCKER_REGISTRY
* STREAM_SERVICE_TRUSTSTORE_PASSPHRASE
* STREAM_SERVICE_KEYSTORE_PASSPHRASE
* ECS_TASK_EXECUTION_ROLE_ARN
* ECS_TASK_SUBNETS
* ECS_TASK_SECURITY_GROUPS

## How to

* To build the project:

```shell-script
mvn clean package
```

* To run the app:

```shell-script
docker run -t --rm boinet \
    --population=<POPULATION> \
    --companies=<COMPANIES> \
    --seed-capital=<SEED-CAPITAL> \
    [--number-of-ticks=<NUMBER_OF_TICKS>] \
    [--stream-service-api-key=<STREAM_SERVICE_API_KEY> \
    --stream-service-namespace=<STREAM_SERVICE_NAMESPACE>]
```

* To deploy the service:

```shell-script
./deploy.sh
    <POPULATION> \
    <COMPANIES> \
    [<STREAM_SERVICE_NAMESPACE>] \
    [<NUMBER_OF_TICKS>]
```
