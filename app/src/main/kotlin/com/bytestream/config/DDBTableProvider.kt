package com.bytestream.config

import com.bytestream.model.ddb.Person
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Property
import jakarta.inject.Singleton
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.TableSchema

@Factory
class DDBTableProvider {

    @Singleton
    fun personTableProvider(ddbEnhanced: DynamoDbEnhancedClient, @Property(name = "ddbtablenames.person") personTableName:String): DynamoDbTable<Person> {
        return ddbEnhanced.table(personTableName, TableSchema.fromBean(Person::class.java))
    }


}