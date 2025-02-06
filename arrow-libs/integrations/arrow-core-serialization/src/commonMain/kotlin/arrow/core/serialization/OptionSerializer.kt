package arrow.core.serialization

import arrow.core.Option
import arrow.core.toOption
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

public class OptionSerializer<T>(
  elementSerializer: KSerializer<T>
) : KSerializer<Option<T>> {
  private val nullableSerializer: KSerializer<T?> = elementSerializer.nullable

  override val descriptor: SerialDescriptor = nullableSerializer.descriptor
  override fun serialize(encoder: Encoder, value: Option<T>) {
    nullableSerializer.serialize(encoder, value.getOrNull())
  }
  override fun deserialize(decoder: Decoder): Option<T> =
    nullableSerializer.deserialize(decoder).toOption()
}

@OptIn(ExperimentalSerializationApi::class)
private val <T> KSerializer<T>.nullable get() =
  @Suppress("UNCHECKED_CAST")
  if (descriptor.isNullable) (this as KSerializer<T?>)
  else (this as KSerializer<T & Any>).nullable
