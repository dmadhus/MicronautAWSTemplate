package com.bytestream.model.ddb

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey

/**
 * This class is a Java Bean, it might be clear to have these in Java
 */
@DynamoDbBean
class PersonEntity() {
    @get:DynamoDbPartitionKey
    var firstName: String? = null

    @get:DynamoDbSortKey
    var lastName: String? = null

    var age: Int? = null
    var favoriteColor: String? = null
}