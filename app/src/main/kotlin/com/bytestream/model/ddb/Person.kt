package com.bytestream.model.ddb

import io.micronaut.serde.annotation.Serdeable
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey

//https://micronaut-projects.github.io/micronaut-serialization/1.0.x/guide/
@Serdeable //Allow Micronaut to serialize this in the controller
@DynamoDbBean
class Person() {
    @get:DynamoDbPartitionKey var firstName:String? = null

    @get:DynamoDbSortKey var lastName:String? = null

    var age:Int? = null
    var favoriteColor:String? = null
}