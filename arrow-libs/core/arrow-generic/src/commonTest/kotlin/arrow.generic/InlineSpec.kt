package arrow.generic

import arrow.generic.Generic.Inline
import arrow.generic.Generic.String
import arrow.generic.Generic.Boolean
import arrow.generic.Generic.Char
import arrow.generic.Generic.Info
import arrow.generic.Generic.Number.Int
import arrow.generic.Generic.Number.Long
import arrow.generic.Generic.Number.Double
import arrow.generic.Generic.Number.Float
import arrow.generic.Generic.Number.Byte
import arrow.generic.Generic.Number.Short
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.bool
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.float
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.short
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

@Serializable
inline class IBoolean(val value: kotlin.Boolean)

@Serializable
inline class IString(val value: kotlin.String)

@Serializable
inline class IChar(val value: kotlin.Char)

@Serializable
inline class IByte(val value: kotlin.Byte)

@Serializable
inline class IShort(val value: kotlin.Short)

@Serializable
inline class IInt(val value: kotlin.Int)

@Serializable
inline class ILong(val value: kotlin.Long)

@Serializable
inline class IFloat(val value: kotlin.Float)

@Serializable
inline class IDouble(val value: kotlin.Double)

@Serializable
inline class ITree(val value: Tree<kotlin.String>)

@Serializable
inline class IPerson(val value: Person)

class InlineSpec : StringSpec({
  testInline(Arb.bool().map(::IBoolean)) { Inline<IBoolean>(Info(IBoolean::class.qualifiedName!!), Boolean(it.value)) }
  testInline(Arb.string().map(::IString)) { Inline<IString>(Info(IString::class.qualifiedName!!), String(it.value)) }
  testInline(Arb.char().map(::IChar)) { Inline<IChar>(Info(IChar::class.qualifiedName!!), Char(it.value)) }
  testInline(Arb.byte().map(::IByte)) { Inline<IByte>(Info(IByte::class.qualifiedName!!), Byte(it.value)) }
  testInline(Arb.short().map(::IShort)) { Inline<IShort>(Info(IShort::class.qualifiedName!!), Short(it.value)) }
  testInline(Arb.int().map(::IInt)) { Inline<IInt>(Info(IInt::class.qualifiedName!!), Int(it.value)) }
  testInline(Arb.long().map(::ILong)) { Inline<ILong>(Info(ILong::class.qualifiedName!!), Long(it.value)) }
  testInline(Arb.float().map(::IFloat)) { Inline<IFloat>(Info(IFloat::class.qualifiedName!!), Float(it.value)) }
  testInline(Arb.double().map(::IDouble)) { Inline<IDouble>(Info(IDouble::class.qualifiedName!!), Double(it.value)) }

  testInline(Arb.bind(Arb.string(), Arb.int(), ::Person).map(::IPerson)) {
    val (name, age, p) = it.value
    Inline<IPerson>(Info(IPerson::class.qualifiedName!!), person(name, age, p))
  }

  testInline(Arb.bind(Arb.string(), Arb.string(), Arb.string()) { a, b, c ->
    ITree(Branch(Leaf(a), Branch(Leaf(b), Leaf(c))))
  }, serializersModule) { Inline<ITree>(Info(ITree::class.qualifiedName!!), tree(it.value)) }
})

inline fun <reified A> StringSpec.testInline(
  arb: Arb<A>,
  serializersModule: SerializersModule = EmptySerializersModule,
  noinline expected: (A) -> Generic<*> // Uses & because it will result in the Generic,
): Unit {
  "${A::class.qualifiedName}" {
    checkAll(arb) { inlined ->
      Generic.encode(inlined, serializersModule = serializersModule) shouldBe expected(inlined)
    }
  }

  "Nested in Id - ${A::class.qualifiedName}" {
    checkAll(arb) { inlined ->
      Generic.encode(Id(inlined), serializersModule = serializersModule) shouldBe expected(inlined).id()
    }
  }
}
