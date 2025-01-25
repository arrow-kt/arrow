package arrow.integrations.jackson.module

import arrow.core.NonEmptySet
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
import kotlin.test.Ignore
import kotlin.test.Test
import kotlinx.coroutines.test.runTest

@Ignore
class NonEmptySetModuleTest {
  private val mapper = ObjectMapper().registerKotlinModule()

  @Test
  fun `serializing NonEmptySet should be the same as serializing the underlying set`() = runTest {
    checkAll(
      Arb.nonEmptySet(Arb.choice(Arb.someObject(), Arb.int(), Arb.string(), Arb.boolean()))
    ) { set ->
      val actual = mapper.writeValueAsString(set)
      val expected = mapper.writeValueAsString(set.toSet())

      actual shouldBe expected
    }
  }

  @Test
  fun `serializing NonEmptySet and then deserialize it should be the same as before the deserialization`() =
    runTest {
      checkAll(
        Arb.choice(
          arbitrary {
            Arb.nonEmptySet(Arb.someObject()).bind() to jacksonTypeRef<NonEmptySet<SomeObject>>()
          },
          arbitrary { Arb.nonEmptySet(Arb.int()).bind() to jacksonTypeRef<NonEmptySet<Int>>() },
          arbitrary {
            Arb.nonEmptySet(Arb.string()).bind() to jacksonTypeRef<NonEmptySet<String>>()
          },
          arbitrary {
            Arb.nonEmptySet(Arb.boolean()).bind() to jacksonTypeRef<NonEmptySet<Boolean>>()
          },
        )
      ) { (set, typeReference) ->
        val encoded: String = mapper.writeValueAsString(set)
        val decoded: NonEmptySet<Any> = mapper.readValue(encoded, typeReference)

        decoded shouldBe set
      }
    }

  @Test
  fun `serializing NonEmptySet in an object should round trip`() = runTest {
    data class Wrapper(val nel: NonEmptySet<SomeObject>)

    checkAll(arbitrary { Wrapper(Arb.nonEmptySet(Arb.someObject()).bind()) }) { wrapper ->
      val encoded: String = mapper.writeValueAsString(wrapper)
      val decoded: Wrapper = mapper.readValue(encoded, Wrapper::class.java)

      decoded shouldBe wrapper
    }
  }

  @Test
  fun `should round trip on NonEmptySet with wildcard type`() = runTest {
    checkAll(Arb.nonEmptySet(Arb.string())) { original: NonEmptySet<*> ->
      val deserialized: NonEmptySet<*> = shouldNotThrowAny {
        val serialized: String = mapper.writeValueAsString(original)
        mapper.readValue(serialized, NonEmptySet::class.java)
      }

      deserialized shouldBe original
    }
  }
}
