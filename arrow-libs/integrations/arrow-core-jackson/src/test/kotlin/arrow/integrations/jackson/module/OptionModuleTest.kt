package arrow.integrations.jackson.module

import arrow.core.Option
import arrow.core.some
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class OptionModuleTest {
  private val mapper = ObjectMapper().registerModule(OptionModule).registerKotlinModule()

  @Test
  fun `serializing Option should be the same as serializing a nullable value`() = runTest {
    checkAll(Arb.option(Arb.choice(Arb.someObject(), Arb.int(), Arb.string(), Arb.boolean()))) {
      option ->
      val actual = mapper.writeValueAsString(option)
      val expected = mapper.writeValueAsString(option.getOrNull())

      actual shouldBe expected
    }
  }

  @Test
  fun `serializing Option with NON_ABSENT should honor such configuration and omit serialization when option is empty`() =
    runTest {
      val mapperWithSettings =
        ObjectMapper()
          .registerModule(OptionModule)
          .registerKotlinModule()
          .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)

      data class Wrapper(val option: Option<Any>)

      checkAll(Arb.option(Arb.choice(Arb.someObject(), Arb.int(), Arb.string(), Arb.boolean()))) {
        option ->
        val actual = mapperWithSettings.writeValueAsString(Wrapper(option))
        val expected =
          option.fold({ "{}" }, { mapperWithSettings.writeValueAsString(Wrapper(it.some())) })
        actual shouldBe expected
      }
    }

  @Test
  fun `serializing Option and then deserialize it should be the same as before the deserialization`() =
    runTest {
      checkAll(
        Arb.choice(
          arbitrary { Arb.option(Arb.someObject()).bind() to jacksonTypeRef<Option<SomeObject>>() },
          arbitrary { Arb.option(Arb.int()).bind() to jacksonTypeRef<Option<Int>>() },
          arbitrary { Arb.option(Arb.string()).bind() to jacksonTypeRef<Option<String>>() },
          arbitrary { Arb.option(Arb.boolean()).bind() to jacksonTypeRef<Option<Boolean>>() },
        )
      ) { (option, typeReference) ->
        val encoded = mapper.writeValueAsString(option)
        val decoded = mapper.readValue(encoded, typeReference)

        decoded shouldBe option
      }
    }

  @Test
  fun `should round-trip on wildcard types`() = runTest {
    val mapper = ObjectMapper().registerArrowModule()
    checkAll(Arb.option(Arb.int(1..10))) { original: Option<*> ->
      val serialized = mapper.writeValueAsString(original)
      val deserialized = shouldNotThrowAny { mapper.readValue(serialized, Option::class.java) }
      deserialized shouldBe original
    }
  }
}
