package com.bytestream.controller

import com.bytestream.model.ddb.PersonEntity
import com.bytestream.service.PersonService
import io.micrometer.core.annotation.Timed
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.serde.annotation.Serdeable
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size


//https://micronaut-projects.github.io/micronaut-serialization/1.0.x/guide/
@Serdeable //Allow Micronaut to serialize this in the controller
data class Person(
    //add some validations
    @field:NotEmpty @field:Size(min = 2, max = 10) val firstName: String,
    @field:NotEmpty @field:Size(min = 2, max = 10)  val lastName: String,
    @field:Min(18) @field:Max(110) val age: Int,
    @field:Size(min = 3, max = 8)val favoriteColor: String
    ) {
    fun toPersonEntity(): PersonEntity {
        val current = this
        return PersonEntity().apply {
            this.age = current.age
            this.favoriteColor = current.favoriteColor
            this.firstName = current.firstName
            this.lastName = current.lastName
        }
    }
}

fun PersonEntity.toPerson(): Person {
    //all fields are required by the controller for insert so this should be safe
    return Person(this.firstName!!, this.lastName!!, this.age!!, this.favoriteColor!!)

}

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
        return personService.getPerson(name, lname)?.toPerson()
    }

    @Timed
    @Get("person2/{name}/{lname}", produces = [MediaType.APPLICATION_JSON])
    fun getPerson2(name: String, lname: String): Person? {
        return personService.getPerson2(name, lname)?.toPerson()
    }


    @Timed
    @Post("people/add", produces = [MediaType.APPLICATION_JSON])
    //Adds valid person or throws a validation error
    fun addPerson(@Valid @Body person: Person): Person {
        personService.addPerson(person.toPersonEntity())
        return person
    }


    @Timed
    @Get("people/list/firstname/{name}", produces = [MediaType.APPLICATION_JSON])
    fun listPeopleWithName(name: String): List<Person> {
        return personService.listPeopleWithName(name).map { it.toPerson() }
    }
}