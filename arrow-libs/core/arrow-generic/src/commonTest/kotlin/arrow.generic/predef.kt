package arrow.generic

import io.kotest.property.Arb
import io.kotest.property.arbitrary.element
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.modules.SerializersModule

fun <T> Arb.Companion.of(collection: Array<T>): Arb<T> =
  element(collection.toList())

val serializersModule = SerializersModule {
  polymorphic(Any::class, Int::class, Int.serializer())
  polymorphic(Any::class, String::class, String.serializer())
  polymorphic(Any::class, Char::class, Char.serializer())
  polymorphic(Any::class, Float::class, Float.serializer())
  polymorphic(Any::class, Double::class, Double.serializer())
  polymorphic(Any::class, Byte::class, Byte.serializer())
  polymorphic(Any::class, Short::class, Short.serializer())

  polymorphic(Any::class, UByte::class, UByte.serializer())
  polymorphic(Any::class, UShort::class, UShort.serializer())
  polymorphic(Any::class, UInt::class, UInt.serializer())
  polymorphic(Any::class, ULong::class, ULong.serializer())
}
