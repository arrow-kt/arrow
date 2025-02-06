package arrow.integrations.jackson.module

import arrow.core.NonEmptyList
import arrow.core.Option
import arrow.core.bothIor
import arrow.core.nonEmptyListOf
import arrow.core.none
import arrow.core.right
import arrow.core.some
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.kotest.matchers.shouldBe
import java.net.URI
import kotlin.test.Ignore
import kotlin.test.Test

class ExampleTest {

  data class Organization(
    val name: String,
    val address: Option<String>,
    val websiteUrl: Option<URI>,
  )

  data class ArrowUser(
    val name: String,
    val emails: List<String>,
    val organization: Option<Organization>,
  )

  data class ArrowUserNel(
    val name: String,
    val emails: NonEmptyList<String>,
    val organization: Option<Organization>,
  )

  val mapper =
    ObjectMapper()
      .registerKotlinModule()
      .registerArrowModule()
      .setSerializationInclusion(JsonInclude.Include.NON_ABSENT) // will not serialize None as nulls

  val prettyPrinter = mapper.writerWithDefaultPrettyPrinter()

  @Test
  fun `example #1 - data structure serialization`() {
    val arrowKt = Organization("arrow-kt", none(), URI("https://arrow-kt.io").some())
    val arrowUser =
      ArrowUser("John Doe", listOf("john@email.com", "john.doe@email.com.au"), arrowKt.some())

    val jsonString = prettyPrinter.writeValueAsString(arrowUser)

    jsonString shouldBe
      """
      {
        "name" : "John Doe",
        "emails" : [ "john@email.com", "john.doe@email.com.au" ],
        "organization" : {
          "name" : "arrow-kt",
          "websiteUrl" : "https://arrow-kt.io"
        }
      }
      """
        .trimIndent()

    mapper.readValue(jsonString, ArrowUser::class.java) shouldBe arrowUser
  }

  @Ignore
  @Test
  fun `example #2 - data structure serialization - non-empty list`() {
    val arrowKt = Organization("arrow-kt", none(), URI("https://arrow-kt.io").some())
    val arrowUser =
      ArrowUserNel(
        "John Doe",
        nonEmptyListOf("john@email.com", "john.doe@email.com.au"),
        arrowKt.some(),
      )

    val jsonString = prettyPrinter.writeValueAsString(arrowUser)

    jsonString shouldBe
      """
      {
        "name" : "John Doe",
        "emails" : [ "john@email.com", "john.doe@email.com.au" ],
        "organization" : {
          "name" : "arrow-kt",
          "websiteUrl" : "https://arrow-kt.io"
        }
      }
      """
        .trimIndent()

    mapper.readValue(jsonString, ArrowUserNel::class.java) shouldBe arrowUser
  }

  @Test
  fun `example #3 - either`() {
    data class Fruit(@get:JsonValue val name: String)

    val apricot = Fruit("apricot")
    prettyPrinter.writeValueAsString(apricot.right()) shouldBe
      """
      {
        "right" : "apricot"
      }
      """
        .trimIndent()
  }

  @Test
  fun `example #4 - ior`() {
    data class Fruit(@get:JsonValue val name: String)
    data class Vegetable(val name: String, val kind: String)

    val starfruit = Fruit("starfruit")
    val spinach = Vegetable("spinach", "leafy greens")
    prettyPrinter.writeValueAsString(Pair(starfruit, spinach).bothIor()) shouldBe
      """
      {
        "left" : "starfruit",
        "right" : {
          "name" : "spinach",
          "kind" : "leafy greens"
        }
      }
      """
        .trimIndent()
  }

  @Test
  fun `example #5 - customizing field names`() {
    data class Fruit(@get:JsonValue val name: String)
    data class Vegetable(val name: String, val kind: String)

    val customMapper =
      ObjectMapper()
        .registerKotlinModule()
        .registerArrowModule(iorModuleConfig = IorModuleConfig("l", "r"))

    val starfruit = Fruit("starfruit")
    val spinach = Vegetable("spinach", "leafy greens")
    customMapper
      .writerWithDefaultPrettyPrinter()
      .writeValueAsString(Pair(starfruit, spinach).bothIor()) shouldBe
      """
      {
        "l" : "starfruit",
        "r" : {
          "name" : "spinach",
          "kind" : "leafy greens"
        }
      }
      """
        .trimIndent()
  }
}
