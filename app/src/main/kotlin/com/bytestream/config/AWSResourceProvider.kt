package com.bytestream.config

import io.micrometer.cloudwatch2.CloudWatchConfig
import io.micrometer.cloudwatch2.CloudWatchMeterRegistry
import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.MeterRegistry
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Property
import jakarta.inject.Singleton
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.sts.StsClient
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest
import java.time.Duration
import java.util.*

@Factory
class AWSResourceProvider {

    @Singleton
    fun awsRegion(@Property(name = "aws.region") region: String): Region = Region.of(region)

    /**
     * This credential provider does an assume role into the application role based on the current credentials in the provider chain
     * This means that we can have a dev account ACC1 and if it has assumeRole access in its account, and the application role
     * has listed account1 as allowed to assume it, we can now have a many to one mapping of accounts to the application role in a controlled fashion
     */
    @Singleton
    fun credentialProvider(
        @Property(name = "aws.appRoleArn") appRoleArn: String, region: Region
    ): AwsCredentialsProvider {
        val assumeRoleRequest =
            AssumeRoleRequest.builder().roleSessionName(UUID.randomUUID().toString()).roleArn(appRoleArn).build();
        val stsClient = StsClient.builder().region(region).build();
        return StsAssumeRoleCredentialsProvider.builder().stsClient(stsClient).refreshRequest(assumeRoleRequest)
            .build();
    }


    /**
     * Use the DDB enhanced client over the micronaut one, it is way easier to use and we can configure the application role credentials
     */
    @Singleton
    fun ddbClient(credentials: AwsCredentialsProvider, region: Region): DynamoDbEnhancedClient {
        val client = DynamoDbClient.builder().credentialsProvider(credentials).region(region).build()
        return DynamoDbEnhancedClient.builder().dynamoDbClient(client).build()
    }


    /**
     *   Manually register our own CW registry instead of using the Micronaut one, this will allow us to pass in the client
     *   which is configured with our application role, which again scopes down access
     */

    @Singleton
    fun cloudwatchMetersRegistry(
        credentials: AwsCredentialsProvider,
        region: Region,
        @Property(name = "micronaut.application.name") appName: String
    ): MeterRegistry {
        //good example on how to use this: https://reflectoring.io/spring-aws-cloudwatch/, the main getters just call into the get function with hardcoded keys
        //See the implementation of CloudWatchConfig
        val config = object : CloudWatchConfig {
            val configMap: Map<String, String> = mapOf(
                "cloudwatch.namespace" to appName, "cloudwatch.step" to Duration.ofMinutes(1).toString()
            )

            override fun get(key: String): String? = configMap[key]

        }
        val cwClient = CloudWatchAsyncClient.builder().credentialsProvider(credentials).region(region).build()
        return CloudWatchMeterRegistry(config, Clock.SYSTEM, cwClient)

    }

}