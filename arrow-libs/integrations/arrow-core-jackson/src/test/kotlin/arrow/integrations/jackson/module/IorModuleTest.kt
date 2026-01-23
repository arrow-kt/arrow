package arrow.integrations.jackson.module

import arrow.core.Ior
import arrow.core.Option
import arrow.core.bothIor
import arrow.core.leftIor
import arrow.core.rightIor
import com.fasterxml.jackson.annotation.JsonProperty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.alphanumeric
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.az
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.of
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.jacksonTypeRef
import kotlin.test.Test

class IorModuleTest {
  val mapper = basicKotlinArrowMapper()

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
      Arb.ior(Arb.ior(arbFoo, Arb.int()).orNull(), Arb.ior(Arb.string(), arbBar.orNull())),
    ) { ior: Ior<Ior<Foo, Int>?, Ior<String, Bar?>> ->
      ior.shouldRoundTrip(mapper)
    }
  }

  @Test
  fun `should serialize with configurable left - right field name`() = runTest {
    checkAll(
      Arb.pair(Arb.string(10, Codepoint.az()), Arb.string(10, Codepoint.az())).filter {
        it.first != it.second
      },
    ) { (leftFieldName, rightFieldName) ->
      val mapper: JsonMapper = basicKotlinArrowMapper(iorModuleConfig = IorModuleConfig(leftFieldName, rightFieldName))
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
      val mapper: JsonMapper = basicKotlinArrowMapper(eitherModuleConfig = EitherModuleConfig(leftFieldName, rightFieldName))
      testClass.shouldRoundTrip(mapper)
    }
  }

  @Test
  fun `should round-trip with wildcard types`() = runTest {
    checkAll(Arb.ior(Arb.int(1..10), Arb.string(10, Codepoint.az()))) { original: Ior<*, *> ->
      val serialized = mapper.writeValueAsString(original)
      val deserialized: Ior<*, *>? = shouldNotThrowAny {
        mapper.readValue(serialized, Ior::class.java)
      }
      deserialized.shouldNotBeNull() shouldBe original
    }
  }

  private enum class IorPolarity {
    Left,
    Both,
    Right,
  }

  private val arbTestClassJsonString = arbitrary {
    when (Arb.of(*IorPolarity.entries.toTypedArray()).bind()) {
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

  @Test
  fun `works with Map, issue #131, part 1`() = runTest {
    checkAll(arbMapContainer(Arb.ior(arbFoo, arbBar))) { original ->
      val serialized = mapper.writeValueAsString(original)
      val deserialized = shouldNotThrowAny {
        mapper.readValue(serialized, jacksonTypeRef<MapContainer<Ior<Foo, Bar>>>())
      }
      deserialized shouldBe original
    }
  }

  @Test
  fun `works with Map, issue #131, part 2`() = runTest {
    checkAll(arbMapContainer(Arb.ior(arbFoo, arbBar))) { original ->
      val serialized = mapper.writeValueAsString(original.value)
      val deserialized = shouldNotThrowAny {
        mapper.readValue(serialized, jacksonTypeRef<Map<MapContainer.Key, Ior<Foo, Bar>>>())
      }
      deserialized shouldBe original.value
    }
  }
}
