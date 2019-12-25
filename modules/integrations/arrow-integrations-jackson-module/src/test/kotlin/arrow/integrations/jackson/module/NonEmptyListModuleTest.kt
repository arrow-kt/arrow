package arrow.integrations.jackson.module

import arrow.core.Nel
import arrow.syntax.function.pipe
import arrow.test.UnitSpec
import arrow.test.generators.nonEmptyList
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.kotlintest.assertions.json.shouldMatchJson
import io.kotlintest.properties.Gen
import io.kotlintest.properties.assertAll
import io.kotlintest.shouldBe

class NonEmptyListModuleTest : UnitSpec() {
  private val mapper = ObjectMapper().registerModule(NonEmptyListModule).registerKotlinModule()

  init {
    "serializing NonEmptyList should be the same as serializing the underlying list" {
      assertAll(Gen.nonEmptyList(Gen.someObject())) { list ->
        val actual = mapper.writeValueAsString(list)
        val expected = mapper.writeValueAsString(list.all)

        actual.shouldMatchJson(expected)
      }
    }

    "serializing NonEmptyList and then deserialize it should be the same as before the deserialization" {
      assertAll(Gen.nonEmptyList(Gen.someObject())) { list ->
        val roundTripped = mapper.writeValueAsString(list).pipe { mapper.readValue<Nel<SomeObject>>(it) }

        roundTripped shouldBe list
      }
    }
  }
}
