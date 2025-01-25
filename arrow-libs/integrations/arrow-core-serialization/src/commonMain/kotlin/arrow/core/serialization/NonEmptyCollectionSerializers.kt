package arrow.core.serialization

import arrow.core.NonEmptyList
import arrow.core.NonEmptySet
import arrow.core.toNonEmptyListOrNull
import arrow.core.toNonEmptySetOrNull
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

public class NonEmptyListSerializer<T>(
  elementSerializer: KSerializer<T>
) : KSerializer<NonEmptyList<T>> {
  private val listSerializer: KSerializer<List<T>> = ListSerializer(elementSerializer)

  @OptIn(ExperimentalSerializationApi::class)
  override val descriptor: SerialDescriptor =
    SerialDescriptor("NonEmptyList", listSerializer.descriptor)
  override fun serialize(encoder: Encoder, value: NonEmptyList<T>) {
    listSerializer.serialize(encoder, value.toList())
  }
  override fun deserialize(decoder: Decoder): NonEmptyList<T> =
    listSerializer.deserialize(decoder).toNonEmptyListOrNull()
      ?: throw SerializationException("expected non-empty list")
}

public class NonEmptySetSerializer<T>(
  elementSerializer: KSerializer<T>
) : KSerializer<NonEmptySet<T>> {
  private val setSerializer: KSerializer<Set<T>> = SetSerializer(elementSerializer)

  @OptIn(ExperimentalSerializationApi::class)
  override val descriptor: SerialDescriptor =
    SerialDescriptor("NonEmptySet", setSerializer.descriptor)
  override fun serialize(encoder: Encoder, value: NonEmptySet<T>) {
    setSerializer.serialize(encoder, value.toSet())
  }
  override fun deserialize(decoder: Decoder): NonEmptySet<T> =
    setSerializer.deserialize(decoder).toNonEmptySetOrNull()
      ?: throw SerializationException("expected non-empty set")
}
