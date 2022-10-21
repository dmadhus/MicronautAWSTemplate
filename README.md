## TODOS

- [X] ~~Swagger~~  Can be found at http://localhost:8080/swagger-ui
- [ ] Local dev config for metrics? Can be expensive
- [ ] Dashboard/Alarm example
- [ ] S3 example
- [ ] Unit test
- [ ] Mutation testing
- [X] ~~CDK Setup~~  
- [X] ~~DDB Integration~~
- [X] ~~Cloudwatch~~
- [x] ~~Application based role~~
- [x] ~~Sample controller~~
- [x] ~~Simple example~~
- [ ] Documentation on logs
- [ ] Determine local vs AWS Runtime
- [ ] Code coverage
- [ ] Interceptor example
- [X] ~~Validate startup script~~


## Micronaut 3.7.1 Documentation

- [User Guide](https://docs.micronaut.io/3.7.1/guide/index.html)
- [API Reference](https://docs.micronaut.io/3.7.1/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/3.7.1/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)
---

## Requisites

- [AWS Account](https://aws.amazon.com/free/)
- [CDK CLI](https://docs.aws.amazon.com/cdk/v2/guide/cli.html)
- - depends on https://nodejs.org/en/download/
- [AWS CLI](https://aws.amazon.com/cli/)

## How to deploy

### Generate the deployable artifact

```
./gradlew build

deployable artifact can be found in 
app/build/distributions

The zip file will have a bin folder with a script

```

### Deploy Infra

The `infra/cdk.json` file tells the CDK Toolkit how to execute your app.

`cd infra`
`cdk synth` - emits the synthesized CloudFormation template
`cdk deploy` - deploy this stack to your default AWS account/region
`cd ..`

Other useful commands:

`cdk diff` - compare deployed stack with current state
`cdk docs`- open CDK documentation

### Cleanup

```
cd infra
cdk destroy
cd ..
```


- [Shadow Gradle Plugin](https://plugins.gradle.org/plugin/com.github.johnrengelman.shadow)
## Feature aws-codebuild-workflow-ci documentation

- [https://docs.aws.amazon.com/codebuild/latest/userguide](https://docs.aws.amazon.com/codebuild/latest/userguide)


## Feature http-client documentation

- [Micronaut HTTP Client documentation](https://docs.micronaut.io/latest/guide/index.html#httpClient)


## Feature micronaut-aot documentation

- [Micronaut AOT documentation](https://micronaut-projects.github.io/micronaut-aot/latest/guide/)


## Feature serialization-jackson documentation

- [Micronaut Serialization Jackson Core documentation](https://micronaut-projects.github.io/micronaut-serialization/latest/guide/)


## Feature management documentation

- [Micronaut Management documentation](https://docs.micronaut.io/latest/guide/index.html#management)


## Feature dynamodb documentation
  
- Not Used: [Micronaut Amazon DynamoDB documentation](https://micronaut-projects.github.io/micronaut-aws/latest/guide/#dynamodb)

- [https://aws.amazon.com/dynamodb/](https://aws.amazon.com/dynamodb/)


## Feature http-session documentation

- [Micronaut HTTP Sessions documentation](https://docs.micronaut.io/latest/guide/index.html#sessions)


## Feature aws-cdk documentation

- [https://docs.aws.amazon.com/cdk/v2/guide/home.html](https://docs.aws.amazon.com/cdk/v2/guide/home.html)


