package com.bytestream.service

import com.bytestream.model.ddb.PersonEntity
import io.micronaut.context.annotation.Requires
import jakarta.inject.Inject
import jakarta.inject.Singleton
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional


@Singleton
class PersonService(@Inject val personEntityTable: DynamoDbTable<PersonEntity>) {

    fun getPerson(name: String, lastName: String): PersonEntity? {
        //since ddb is only KVP, all queries should be very simple
        return personEntityTable.getItem(Key.builder().partitionValue(name).sortValue(lastName).build())
    }

    fun getPerson2(name: String, lastName: String): PersonEntity? {
        //this is similar to query by example from hibernate
        val searchCriteria: PersonEntity = PersonEntity().apply {
            this.firstName = name
            this.lastName = lastName
        }
        return personEntityTable.getItem(searchCriteria)
    }

    fun addPerson(personEntity: PersonEntity): PersonEntity {
        personEntityTable.putItem(personEntity)
        return personEntity
    }

    fun listPeopleWithName(name: String): List<PersonEntity> {
        //ALWAYS DO GETITEM or QUERY, SCANS WILL GET EXPENSIVE
        val ddbResults = personEntityTable.query(QueryConditional.keyEqualTo(Key.builder().partitionValue(name).build()))
        return ddbResults.items().toList()
    }
}