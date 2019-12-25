package arrow.integrations.jackson.module

import arrow.core.Option
import arrow.syntax.function.pipe
import arrow.test.UnitSpec
import arrow.test.generators.option
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.kotlintest.assertions.json.shouldMatchJson
import io.kotlintest.properties.Gen
import io.kotlintest.properties.assertAll
import io.kotlintest.shouldBe

class OptionModuleTest : UnitSpec() {
  private val mapper = ObjectMapper().registerModule(OptionModule).registerKotlinModule()

  init {
    "serializing Option should be the same as serializing a nullable value" {
      assertAll(Gen.option(Gen.oneOf(Gen.someObject(), Gen.int(), Gen.string(), Gen.bool()))) { option ->
        val actual = mapper.writeValueAsString(option)
        val expected = mapper.writeValueAsString(option.orNull())

        actual.shouldMatchJson(expected)
      }
    }

    "serializing Option and then deserialize it should be the same as before the deserialization" {
      assertAll(
        Gen.oneOf(
          Gen.option(Gen.someObject()).map { it to jacksonTypeRef<Option<SomeObject>>() },
          Gen.option(Gen.int()).map { it to jacksonTypeRef<Option<Int>>() },
          Gen.option(Gen.string()).map { it to jacksonTypeRef<Option<String>>() },
          Gen.option(Gen.bool()).map { it to jacksonTypeRef<Option<Boolean>>() }
        )
      ) { (option, typeReference) ->
        val roundTripped = mapper.writeValueAsString(option).pipe { mapper.readValue<Option<*>>(it, typeReference) }

        roundTripped shouldBe option
      }
    }
  }
}
