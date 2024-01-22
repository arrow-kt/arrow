package arrow.core.serialization

import arrow.core.Option
import arrow.core.toOption
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

public class OptionSerializer<T : Any>(
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
