package arrow.integrations.jackson.module

import arrow.core.Option
import arrow.syntax.function.pipe
import arrow.test.UnitSpec
import arrow.test.generators.option
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.kotlintest.assertions.json.shouldMatchJson
import io.kotlintest.properties.Gen
import io.kotlintest.properties.assertAll
import io.kotlintest.shouldBe

class OptionModuleTest : UnitSpec() {
  private val mapper = ObjectMapper().registerModule(OptionModule).registerKotlinModule()

  init {
    "serializing Option should be the same as serializing a nullable value" {
      assertAll(Gen.option(Gen.someObject())) { option ->
        val actual = mapper.writeValueAsString(option)
        val expected = mapper.writeValueAsString(option.orNull())

        actual.shouldMatchJson(expected)
      }
    }

    "serializing Option and then deserialize it should be the same as before the deserialization" {
      assertAll(Gen.option(Gen.someObject())) { option ->
        val roundTripped = mapper.writeValueAsString(option).pipe { mapper.readValue<Option<SomeObject>>(it) }

        roundTripped shouldBe option
      }
    }
  }
}