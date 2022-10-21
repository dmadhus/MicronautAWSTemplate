package com.bytestream.service

import com.bytestream.model.ddb.Person
import jakarta.inject.Inject
import jakarta.inject.Singleton
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import kotlin.random.Random

@Singleton
class PersonService(@Inject val personTable: DynamoDbTable<Person>) {

    fun getPerson(name: String, lastName: String): Person? {
        //since ddb is only KVP, all queries should be very simple
        return personTable.getItem(Key.builder().partitionValue(name).sortValue(lastName).build())
    }

    fun getPerson2(name: String, lastName: String): Person? {
        //this is similar to query by example from hibernate
        val searchCriteria: Person = Person().apply {
            this.firstName = name
            this.lastName = lastName
        }
        return personTable.getItem(searchCriteria)
    }

    fun addPerson(firstname: String, lastName: String): Person {
        val colors = listOf("Red", "Green", "Blue", "Yellow", "Black", "Purple", "Orange")
        val p: Person = Person().apply {
            this.age = Random.nextInt(10, 100)
            this.favoriteColor = colors.random()
            this.firstName = firstname
            this.lastName = lastName
        }
        personTable.putItem(p)
        return p
    }

    fun listPeopleWithName(name: String): List<Person> {
        //ALWAYS DO GETITEM or QUERY, SCANS WILL GET EXPENSIVE
        val ddbResults = personTable.query(QueryConditional.keyEqualTo(Key.builder().partitionValue(name).build()))
        return ddbResults.items().toList()
    }
}