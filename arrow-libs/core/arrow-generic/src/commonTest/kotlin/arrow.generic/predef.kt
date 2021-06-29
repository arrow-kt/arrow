package arrow.generic

import io.kotest.property.Arb
import io.kotest.property.arbitrary.element
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

fun <T> Arb.Companion.of(collection: Array<T>): Arb<T> =
  element(collection.toList())

val serializersModule = SerializersModule {
  polymorphic(Any::class) {
    subclass(Int::class, Int.serializer())
    subclass(String::class, String.serializer())
    subclass(Char::class, Char.serializer())
    subclass(Float::class, Float.serializer())
    subclass(Double::class, Double.serializer())
    subclass(Byte::class, Byte.serializer())
    subclass(Short::class, Short.serializer())

    subclass(UByte::class, UByte.serializer())
    subclass(UShort::class, UShort.serializer())
    subclass(UInt::class, UInt.serializer())
    subclass(ULong::class, ULong.serializer())

    subclass(List::class, ListSerializer(PolymorphicSerializer(Any::class).nullable))
  }
}
