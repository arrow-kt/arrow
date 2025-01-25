package arrow.integrations.jackson.module

import arrow.core.Ior
import arrow.core.Option
import arrow.core.bothIor
import arrow.core.leftIor
import arrow.core.rightIor
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.alphanumeric
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.az
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlin.test.Test
import kotlinx.coroutines.test.runTest

class IorModuleTest {
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
    checkAll(Arb.ior(arbFoo.orNull(), arbBar.orNull())) { ior: Ior<Foo?, Bar?> ->
      ior.shouldRoundTrip(mapper)
    }
  }

  @Test
  fun `should round-trip nested ior types`() = runTest {
    checkAll(
      Arb.ior(Arb.ior(arbFoo, Arb.int()).orNull(), Arb.ior(Arb.string(), arbBar.orNull()))
    ) { ior: Ior<Ior<Foo, Int>?, Ior<String, Bar?>> ->
      ior.shouldRoundTrip(mapper)
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
          .registerArrowModule(iorModuleConfig = IorModuleConfig(leftFieldName, rightFieldName))

      mapper.writeValueAsString(5.leftIor()) shouldBe """{"$leftFieldName":5}"""
      mapper.writeValueAsString("hello".rightIor()) shouldBe """{"$rightFieldName":"hello"}"""
      mapper.writeValueAsString(Pair(5, "hello").bothIor()) shouldBe
        """{"$leftFieldName":5,"$rightFieldName":"hello"}"""
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
  fun `should round-trip with wildcard types`() = runTest {
    checkAll(Arb.ior(Arb.int(1..10), Arb.string(10, Codepoint.az()))) { original: Ior<*, *> ->
      val mapper = ObjectMapper().registerKotlinModule().registerArrowModule()
      val serialized = mapper.writeValueAsString(original)
      val deserialized: Ior<*, *> = shouldNotThrowAny {
        mapper.readValue(serialized, Ior::class.java)
      }
      deserialized shouldBe original
    }
  }

  private enum class IorPolarity {
    Left,
    Both,
    Right,
  }

  private val arbTestClassJsonString = arbitrary {
    when (Arb.enum<IorPolarity>().bind()) {
      IorPolarity.Left -> {
        val foo = arbFoo.bind()
        """
        {
          "ior": {
            "left": {
              "foo": ${foo.fooValue.getOrNull()},
              "otherValue": ${mapper.writeValueAsString(foo.otherValue)}
            }
          }
        }
      """
          .trimIndent()
      }
      IorPolarity.Both -> {
        val foo = arbFoo.bind()
        val bar = arbBar.bind()
        """
        {
          "ior": {
            "left": {
              "foo": ${foo.fooValue.getOrNull()},
              "otherValue": ${mapper.writeValueAsString(foo.otherValue)}
            },
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
      IorPolarity.Right -> {
        val bar = arbBar.bind()
        """
        {
          "ior": {
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
  }

  private data class Foo(
    @get:JsonProperty("foo") val fooValue: Option<Int>,
    val otherValue: String,
  )

  private data class Bar(val first: Int, val second: String, val third: Boolean)

  private data class TestClass(val ior: Ior<Foo, Bar>)

  private val arbFoo: Arb<Foo> = arbitrary {
    Foo(Arb.option(Arb.int()).bind(), Arb.string().bind())
  }

  private val arbBar: Arb<Bar> = arbitrary {
    Bar(Arb.int().bind(), Arb.string(0..100, Codepoint.alphanumeric()).bind(), Arb.boolean().bind())
  }

  private val arbTestClass: Arb<TestClass> = arbitrary { TestClass(Arb.ior(arbFoo, arbBar).bind()) }

  private fun <L, R> Arb.Companion.ior(arbL: Arb<L>, arbR: Arb<R>): Arb<Ior<L, R>> =
    Arb.choice(
      arbitrary { arbL.bind().leftIor() },
      arbitrary { arbR.bind().rightIor() },
      arbitrary { (arbL.bind() to arbR.bind()).bothIor() },
    )

  private val mapper = ObjectMapper().registerKotlinModule().registerArrowModule()
}
