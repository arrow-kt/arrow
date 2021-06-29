package arrow.generic

import arrow.generic.Generic.*
import arrow.generic.Generic.Number.*
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
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.short
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlin.Boolean
import kotlin.Byte
import kotlin.Char
import kotlin.Double
import kotlin.Float
import kotlin.Int
import kotlin.Long
import kotlin.Short
import kotlin.String
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

@Serializable
inline class IBoolean(val value: Boolean)

@Serializable
inline class IString(val value: String)

@Serializable
inline class IChar(val value: Char)

@Serializable
inline class IByte(val value: Byte)

@Serializable
inline class IShort(val value: Short)

@Serializable
inline class IInt(val value: Int)

@Serializable
inline class ILong(val value: Long)

@Serializable
inline class IFloat(val value: Float)

@Serializable
inline class IDouble(val value: Double)

@Serializable
inline class ITree(val value: Tree<String>)

@Serializable
inline class IPerson(val value: Person)

class InlineSpec : StringSpec({
  testInline(Arb.bool().map(::IBoolean)) { Inline<IBoolean>(ObjectInfo(IBoolean::class.qualifiedName!!), Boolean(it.value)) }
  testInline(Arb.string().map(::IString)) { Inline<IString>(ObjectInfo(IString::class.qualifiedName!!), String(it.value)) }
  testInline(Arb.char().map(::IChar)) { Inline<IChar>(ObjectInfo(IChar::class.qualifiedName!!), Char(it.value)) }
  testInline(Arb.byte().map(::IByte)) { Inline<IByte>(ObjectInfo(IByte::class.qualifiedName!!), Byte(it.value)) }
  testInline(Arb.short().map(::IShort)) { Inline<IShort>(ObjectInfo(IShort::class.qualifiedName!!), Short(it.value)) }
  testInline(Arb.int().map(::IInt)) { Inline<IInt>(ObjectInfo(IInt::class.qualifiedName!!), Int(it.value)) }
  testInline(Arb.long().map(::ILong)) { Inline<ILong>(ObjectInfo(ILong::class.qualifiedName!!), Long(it.value)) }
  testInline(Arb.float().map(::IFloat)) { Inline<IFloat>(ObjectInfo(IFloat::class.qualifiedName!!), Float(it.value)) }
  testInline(Arb.double().map(::IDouble)) { Inline<IDouble>(ObjectInfo(IDouble::class.qualifiedName!!), Double(it.value)) }

  testInline(Arb.bind(Arb.string(), Arb.int(), ::Person).map(::IPerson)) {
    val (name, age, p) = it.value
    Inline<IPerson>(ObjectInfo(IPerson::class.qualifiedName!!), person(name, age, p))
  }

  testInline(Arb.bind(Arb.string(), Arb.string(), Arb.string()) { a, b, c ->
    ITree(Branch(Leaf(a), Branch(Leaf(b), Leaf(c))))
  }, serializersModule) { Inline<ITree>(ObjectInfo(ITree::class.qualifiedName!!), tree(it.value)) }
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
