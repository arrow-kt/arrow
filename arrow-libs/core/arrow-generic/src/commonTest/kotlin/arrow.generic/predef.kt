package arrow.generic

import io.kotest.property.Arb
import io.kotest.property.arbitrary.element
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

fun <T> Arb.Companion.of(collection: Array<T>): Arb<T> =
  element(collection.toList())

val serializersModule = SerializersModule {
  polymorphic(Any::class) {
    subclass(Int.serializer())
    subclass(String.serializer())
    subclass(Char.serializer())
    subclass(Float.serializer())
    subclass(Double.serializer())
    subclass(Byte.serializer())
    subclass(Short.serializer())
    subclass(UByte.serializer())
    subclass(UShort.serializer())
    subclass(UInt.serializer())
    subclass(ULong.serializer())

    // TODO Caused by SerializationException: Class 'ArrayList' is not registered for polymorphic serialization in the scope of 'Any'.
    subclass(ListSerializer(PolymorphicSerializer(Any::class).nullable))
    // TODO Caused by SerializationException: Class 'LinkedHashMap' is not registered for polymorphic serialization in the scope of 'Any'.
    subclass(MapSerializer(PolymorphicSerializer(Any::class).nullable, PolymorphicSerializer(Any::class).nullable))
  }
}
