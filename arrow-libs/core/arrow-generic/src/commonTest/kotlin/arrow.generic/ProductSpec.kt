package arrow.generic

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bool
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.float
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.short
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.serialization.Serializable

@Serializable
data class Id<A>(val value: A)

@Serializable
data class Person(val name: String, val age: Int, val p: Person2? = null)

@Serializable
data class Person2(val name: String, val age: Int, val p: Person2? = null)

class ProductSpec : StringSpec({

  "Pair" {
    checkAll(Arb.int(), Arb.string()) { a, b ->
      Generic.encode(Pair(a, b)) shouldBe
        Generic.Product(
          Generic.ObjectInfo("kotlin.Pair"),
          listOf(
            "first" to Generic.Number.Int(a),
            "second" to Generic.String(b)
          )
        )
    }
  }

  "Nested Pair" {
    checkAll(Arb.int(), Arb.string(), Arb.float()) { a, b, c ->
      Generic.encode(Pair(a, Pair(b, c))) shouldBe
        Generic.Product(
          Generic.ObjectInfo("kotlin.Pair"),
          "first" to Generic.Number.Int(a),
          "second" to Generic.Product(
            Generic.ObjectInfo("kotlin.Pair"),
            "first" to Generic.String(b),
            "second" to Generic.Number.Float(c)
          )
        )
    }
  }

  "Person" {
    val res = Generic.encode(Person(name = "X", age = 98, p = Person2(name = "Y", age = 99, p = null)))
    val expected = Generic.Product(
      Generic.ObjectInfo(Person::class.qualifiedName!!),
      "name" to Generic.String("X"),
      "age" to Generic.Number.Int(98),
      "p" to Generic.Product(Generic.ObjectInfo(Person2::class.qualifiedName!!),
        "name" to Generic.String("Y"),
        "age" to Generic.Number.Int(99)
      )
    )
    res shouldBe expected
  }

  "Serializable Person, without Module Config" {
    val res = Generic.encode(Id(Person(name = "X", age = 98, p = Person2(name = "Y", age = 99, p = null))))
    val expected = Generic.Product(
      Generic.ObjectInfo(Person::class.qualifiedName!!),
      "name" to Generic.String("X"),
      "age" to Generic.Number.Int(98),
      "p" to Generic.Product(Generic.ObjectInfo(Person2::class.qualifiedName!!),
        "name" to Generic.String("Y"),
        "age" to Generic.Number.Int(99)
      )
    ).id()
    res shouldBe expected
  }

  testIdProduct(Arb.bool()) { Generic.Boolean(it) }
  testIdProduct(Arb.string()) { Generic.String(it) }
  testIdProduct(Arb.char()) { Generic.Char(it) }
  testIdProduct(Arb.byte()) { Generic.Number.Byte(it) }
  testIdProduct(Arb.short()) { Generic.Number.Short(it) }
  testIdProduct(Arb.int()) { Generic.Number.Int(it) }
  testIdProduct(Arb.long()) { Generic.Number.Long(it) }
  testIdProduct(Arb.float()) { Generic.Number.Float(it) }
  testIdProduct(Arb.double()) { Generic.Number.Double(it) }
})

fun <A> Generic<A>.id(): Generic.Product<Id<A>> =
  Generic.Product(
    Generic.ObjectInfo(Id::class.qualifiedName!!),
    listOf("value" to this@id)
  )

inline fun <reified A> StringSpec.testIdProduct(
  arb: Arb<A>,
  noinline expected: (A) -> Generic<A>
): Unit =
  "Id - ${A::class.qualifiedName!!}" {
    checkAll(arb) { a ->
      Generic.encode(Id(a), serializersModule = serializersModule) shouldBe expected(a).id()
    }
  }
