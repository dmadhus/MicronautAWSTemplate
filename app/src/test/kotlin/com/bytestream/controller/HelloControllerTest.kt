package com.bytestream.controller
import com.bytestream.model.ddb.PersonEntity
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.kotest.MicronautKotestExtension.getMock
import io.micronaut.test.extensions.kotest.annotation.MicronautTest
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import jakarta.inject.Singleton
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key


@MicronautTest
class HelloControllerTest(
        private val application: EmbeddedApplication<*>,
        @Client("/") private val client: HttpClient,
        private val mockDynamoTable: DynamoDbTable<PersonEntity> //this is needed to get the reference through getMock
        ): StringSpec({

    "test the server is running" {
        assert(application.isRunning)

    }

    "Get on root returns hello"{
        client.toBlocking().retrieve("/") shouldBe  "Hello"
    }


    "Get person that exists"{
        //just combine both tests for person and person2

        //configure the mock
        val ddb = getMock(mockDynamoTable)
        val patrickEntity = PersonEntity().apply {
            this.age=2
            this.favoriteColor="Pink"
            this.firstName="Patrick"
            this.lastName = "Star"
        }

        every {ddb.getItem(any<Key>())} returns patrickEntity
        every {ddb.getItem(any<PersonEntity>())} returns patrickEntity

        val person1 = client.toBlocking().retrieve("/person/Patrick/Star")
        val person2 = client.toBlocking().retrieve("/person2/Patrick/Star")
        //make sure both returned the same value
        person1 shouldBe person2

        //verify the fields, shouldEqualJson handles all the formatting and ordering issues
        person1  shouldEqualJson """{"firstName":"Patrick","lastName":"Star","age":2,"favoriteColor":"Pink"}"""
        person1  shouldEqualJson """{"firstName":"Patrick","age":2,    "lastName":"Star",              "favoriteColor":"Pink"}"""
    }

    "Get Person that does not exist"{
        //just combine both tests for person and person2

        //configure the mock
        val ddb = getMock(mockDynamoTable)

        every {ddb.getItem(any<Key>())} returns null
        every {ddb.getItem(any<PersonEntity>())} returns null

        val p1Exception = shouldThrow<HttpClientResponseException> {
            client.toBlocking().retrieve("/person/Patrick/Star")
        }
        val p2Exception = shouldThrow<HttpClientResponseException> {
            client.toBlocking().retrieve("/person2/Patrick/Star")
        }
        //make sure both returned the same value
        p1Exception shouldBe p2Exception

        //make sure we got a 404
        p1Exception.status shouldBe HttpStatus.NOT_FOUND

    }


    "Add valid person"{
        //just combine both tests for person and person2

        //configure the mock
        val ddb = getMock(mockDynamoTable)

        //have it be success from the db side
        every {ddb.putItem(any<PersonEntity>())} just runs

        val squidward = Person("Squidward", "Tentacles", 22,"Blue")
      //send the request
        val added:HttpResponse<Person?> = client.toBlocking().exchange( HttpRequest.POST("people/add", squidward))
        added.status shouldBe  HttpStatus.OK
        added.body() shouldNotBe  null
        added.body()!!.apply {
            age shouldBe squidward.age
            favoriteColor shouldBe squidward.favoriteColor
            firstName shouldBe squidward.firstName
            lastName shouldBe squidward.lastName
        }

    }

    "Add an invalid person"{
        //just combine both tests for person and person2

        //configure the mock
        val ddb = getMock(mockDynamoTable)

        //have it be success from the db side
        every {ddb.putItem(any<PersonEntity>())} just runs

        val spongebob = Person("Spongebob", "Squarepants", 12,"Yellow")

        //send the request
        val exception = shouldThrow<HttpClientResponseException> {
            //The micronaut http client is weird with errors, you need the 3 argument variant of exchange in order to get the body of errors instead of just the header
            client.toBlocking().exchange(HttpRequest.POST("people/add", spongebob),Argument.of(Person::class.java),Argument.of(String::class.java))
        }
        exception.status shouldBe  HttpStatus.BAD_REQUEST
        //TODO: validate the body
       // exception.status.reason shouldBe ""

    }

}){
    /**
     * Since we are running with env=test we need to mock service to avoid the AWS dependencies
     * For controller tests I like to test the controller responsibility and not the service
     */
    @Singleton
    @MockBean(DynamoDbTable::class)
    fun personDynamoTableMock(): DynamoDbTable<PersonEntity> {
        return mockk()
    }

}
