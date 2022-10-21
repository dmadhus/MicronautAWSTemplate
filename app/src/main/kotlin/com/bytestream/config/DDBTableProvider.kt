package com.bytestream.config

import com.bytestream.model.ddb.PersonEntity
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Property
import jakarta.inject.Singleton
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.TableSchema

@Factory
class DDBTableProvider {

    @Singleton
    fun personTableProvider(ddbEnhanced: DynamoDbEnhancedClient, @Property(name = "ddbtablenames.person") personTableName:String): DynamoDbTable<PersonEntity> {
        return ddbEnhanced.table(personTableName, TableSchema.fromBean(PersonEntity::class.java))
    }


}