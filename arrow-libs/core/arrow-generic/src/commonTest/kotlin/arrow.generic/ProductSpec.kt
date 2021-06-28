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

  fun <A> Generic<A>.id(): Generic.Product<Id<A>> =
    Generic.Product(
      Generic.ObjectInfo(Id::class.qualifiedName!!),
      listOf("value" to this@id)
    )

  "Generic - String" {
    checkAll(Arb.string()) { a ->
      Generic.encode(Id(a), serializersModule = serializersModule) shouldBe Generic.String(a).id()
    }
  }

  "Generic - Char" {
    checkAll(Arb.char()) { a ->
      Generic.encode(Id(a), serializersModule = serializersModule) shouldBe Generic.Char(a).id()
    }
  }

  "Generic - Byte" {
    checkAll(Arb.byte()) { a ->
      Generic.encode(Id(a), serializersModule = serializersModule) shouldBe Generic.Number.Byte(a).id()
    }
  }

  "Generic - Short" {
    checkAll(Arb.short()) { a ->
      Generic.encode(Id(a), serializersModule = serializersModule) shouldBe Generic.Number.Short(a).id()
    }
  }

  "Generic - Int" {
    checkAll(Arb.int()) { a ->
      Generic.encode(Id(a), serializersModule = serializersModule) shouldBe Generic.Number.Int(a).id()
    }
  }

  "Generic - Long" {
    checkAll(Arb.long()) { a ->
      Generic.encode(Id(a), serializersModule = serializersModule) shouldBe Generic.Number.Long(a).id()
    }
  }

  "Generic - Float" {
    checkAll(Arb.float()) { a ->
      Generic.encode(Id(a), serializersModule = serializersModule) shouldBe Generic.Number.Float(a).id()
    }
  }

  "Generic - Double" {
    checkAll(Arb.double()) { a ->
      Generic.encode(Id(a), serializersModule = serializersModule) shouldBe Generic.Number.Double(a).id()
    }
  }

  "Generic - Boolean" {
    checkAll(Arb.bool()) { a ->
      Generic.encode(Id(a), serializersModule = serializersModule) shouldBe Generic.Boolean(a).id()
    }
  }
})
