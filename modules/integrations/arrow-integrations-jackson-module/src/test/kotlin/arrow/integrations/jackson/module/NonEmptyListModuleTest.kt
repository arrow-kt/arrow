package arrow.integrations.jackson.module

import arrow.core.Nel
import arrow.syntax.function.pipe
import arrow.test.UnitSpec
import arrow.test.generators.nonEmptyList
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.kotlintest.assertions.json.shouldMatchJson
import io.kotlintest.properties.Gen
import io.kotlintest.properties.assertAll
import io.kotlintest.shouldBe

class NonEmptyListModuleTest : UnitSpec() {
  private val mapper = ObjectMapper().registerModule(NonEmptyListModule).registerKotlinModule()

  init {
    "serializing NonEmptyList should be the same as serializing the underlying list" {
      assertAll(Gen.nonEmptyList(Gen.oneOf(Gen.someObject(), Gen.int(), Gen.string(), Gen.bool()))) { list ->
        val actual = mapper.writeValueAsString(list)
        val expected = mapper.writeValueAsString(list.all)

        actual.shouldMatchJson(expected)
      }
    }

    "serializing NonEmptyList and then deserialize it should be the same as before the deserialization" {
      assertAll(
        Gen.oneOf(
          Gen.nonEmptyList(Gen.someObject()).map { it to jacksonTypeRef<Nel<SomeObject>>() },
          Gen.nonEmptyList(Gen.int()).map { it to jacksonTypeRef<Nel<Int>>() },
          Gen.nonEmptyList(Gen.string()).map { it to jacksonTypeRef<Nel<String>>() },
          Gen.nonEmptyList(Gen.bool()).map { it to jacksonTypeRef<Nel<Boolean>>() }
        )
      ) { (list, typeReference) ->
        val roundTripped = mapper.writeValueAsString(list).pipe { mapper.readValue<Nel<*>>(it, typeReference) }

        roundTripped shouldBe list
      }
    }
  }
}
