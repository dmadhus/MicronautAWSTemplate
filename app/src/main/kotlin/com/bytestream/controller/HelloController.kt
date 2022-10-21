package com.bytestream.controller

import com.bytestream.model.ddb.Person
import com.bytestream.service.PersonService
import io.micrometer.core.annotation.Timed
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import kotlin.random.Random

//needs to be open for the AOP to work, this is happening through all open in the gradle file
@Controller("/")
class HelloController(val personService: PersonService) {

    @Timed
    @Get
    fun hello(): String {
        return "Hello"
    }

    @Timed
    @Get("person/{name}/{lname}", produces = [MediaType.APPLICATION_JSON])
    fun getPerson(name: String, lname: String): Person? {
        return personService.getPerson(name,lname)
    }

    @Timed
    @Get("person2/{name}/{lname}", produces = [MediaType.APPLICATION_JSON])
    fun getPerson2(name: String, lname: String): Person? {
       return personService.getPerson2(name,lname)
    }


    /**
     * Yes this should be a post but this makes it easier to test from the url bar
     */
    @Timed
    @Get("people/add/{firstname}/{lastName}", produces = [MediaType.APPLICATION_JSON])
    fun addPerson(firstname: String, lastName: String): Person {
        return personService.addPerson(firstname,lastName)
    }


    @Timed
    @Get("people/list/firstname/{name}", produces = [MediaType.APPLICATION_JSON])
    fun listPeopleWithName(name: String): List<Person> {
     return personService.listPeopleWithName(name)
    }
}