package arrow.integrations.jackson.module

import arrow.core.Either
import arrow.core.Option
import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.alphanumeric
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.az
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlin.test.Test
import kotlinx.coroutines.test.runTest

class EitherModuleTest {
  @Test
  fun `should round-trip on mandatory types`() = runTest {
    checkAll(arbTestClass) { it.shouldRoundTrip(mapper) }
  }

  @Test
  fun `should serialize in the expected format`() = runTest {
    checkAll(arbTestClassJsonString) { it.shouldRoundTripOtherWay<TestClass>(mapper) }
  }

  @Test
  fun `should round-trip nullable types`() = runTest {
    checkAll(Arb.either(arbFoo.orNull(), arbBar.orNull())) { either: Either<Foo?, Bar?> ->
      either.shouldRoundTrip(mapper)
    }
  }

  @Test
  fun `should round-trip nested either types`() = runTest {
    checkAll(
      Arb.either(Arb.either(arbFoo, Arb.int()).orNull(), Arb.either(Arb.string(), arbBar.orNull()))
    ) { either: Either<Either<Foo, Int>?, Either<String, Bar?>> ->
      either.shouldRoundTrip(mapper)
    }
  }

  @Test
  fun `should serialize with configurable left - right field name`() = runTest {
    checkAll(
      Arb.pair(Arb.string(10, Codepoint.az()), Arb.string(10, Codepoint.az())).filter {
        it.first != it.second
      }
    ) { (leftFieldName, rightFieldName) ->
      val mapper =
        ObjectMapper()
          .registerKotlinModule()
          .registerArrowModule(EitherModuleConfig(leftFieldName, rightFieldName))

      mapper.writeValueAsString(5.left()) shouldBe """{"$leftFieldName":5}"""
      mapper.writeValueAsString("hello".right()) shouldBe """{"$rightFieldName":"hello"}"""
    }
  }

  @Test
  fun `should round-trip with configurable left - right field name`() = runTest {
    checkAll(
      Arb.pair(Arb.string(10, Codepoint.az()), Arb.string(10, Codepoint.az())).filter {
        it.first != it.second
      },
      arbTestClass,
    ) { (leftFieldName, rightFieldName), testClass ->
      val mapper =
        ObjectMapper()
          .registerKotlinModule()
          .registerArrowModule(
            eitherModuleConfig = EitherModuleConfig(leftFieldName, rightFieldName)
          )

      testClass.shouldRoundTrip(mapper)
    }
  }

  @Test
  fun `should round-trip on wildcard types`() = runTest {
    val mapper = ObjectMapper().registerArrowModule()
    checkAll(Arb.either(Arb.int(1..10), Arb.string(5))) { original: Either<*, *> ->
      val serialized = mapper.writeValueAsString(original)
      val deserialized = shouldNotThrowAny { mapper.readValue(serialized, Either::class.java) }
      deserialized shouldBe original
    }
  }

  @Test
  fun `should round-trip when inside a collection`() = runTest {
    val mapper = ObjectMapper().registerArrowModule()
    checkAll(Arb.list(Arb.either(Arb.int(1..10), Arb.string(5)))) {
      original: List<Either<Int, String>> ->
      val serialized: String = mapper.writeValueAsString(original)
      val deserialized = shouldNotThrowAny {
        mapper.readValue<List<Either<Int, String>>>(serialized)
      }
      deserialized shouldBe original
    }
  }

  private val arbTestClassJsonString = arbitrary {
    if (Arb.boolean().bind()) {
      val foo = arbFoo.bind()
      """
        {
          "either": {
            "left": {
              "foo": ${foo.fooValue.getOrNull()},
              "otherValue": ${mapper.writeValueAsString(foo.otherValue)}
            }
          }
        }
      """
        .trimIndent()
    } else {
      val bar = arbBar.bind()
      """
        {
          "either": {
            "right": {
              "first": ${bar.first},
              "second": "${bar.second}",
              "third": ${bar.third}
            }
          }
        }
      """
        .trimIndent()
    }
  }

  private data class Foo(
    @get:JsonProperty("foo") val fooValue: Option<Int>,
    val otherValue: String,
  )

  private data class Bar(val first: Int, val second: String, val third: Boolean)

  private data class TestClass(val either: Either<Foo, Bar>)

  private val arbFoo: Arb<Foo> = arbitrary {
    Foo(Arb.option(Arb.int()).bind(), Arb.string().bind())
  }

  private val arbBar: Arb<Bar> = arbitrary {
    Bar(Arb.int().bind(), Arb.string(0..100, Codepoint.alphanumeric()).bind(), Arb.boolean().bind())
  }

  private val arbTestClass: Arb<TestClass> = arbitrary {
    TestClass(Arb.either(arbFoo, arbBar).bind())
  }

  private val mapper = ObjectMapper().registerKotlinModule().registerArrowModule()
}
