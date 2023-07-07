package arrow.core.serialization

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.Validated
import arrow.core.invalid
import arrow.core.none
import arrow.core.some
import arrow.core.valid
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

public class ValidatedSerializer<A, B>(
  private val errorSerializer: KSerializer<A>,
  private val elementSerializer: KSerializer<B>,
) : KSerializer<Validated<A, B>> {

  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Validated") {
    element("invalid", errorSerializer.descriptor, isOptional = true)
    element("valid", elementSerializer.descriptor, isOptional = true)
  }
  override fun serialize(encoder: Encoder, value: Validated<A, B>) {
    encoder.encodeStructure(descriptor) {
      when (value) {
        is Validated.Invalid -> encodeSerializableElement(descriptor, 0, errorSerializer, value.value)
        is Validated.Valid -> encodeSerializableElement(descriptor, 1, elementSerializer, value.value)
      }
    }
  }
  override fun deserialize(decoder: Decoder): Validated<A, B> {
    var invalidValue: Option<A> = none()
    var validValue: Option<B> = none()
    decoder.decodeStructure(descriptor) {
      while (true) {
        when (val index = decodeElementIndex(descriptor)) {
          0 -> {
            invalidValue = decodeSerializableElement(descriptor, 0, errorSerializer).some()
          }
          1 -> {
            validValue = decodeSerializableElement(descriptor, 1, elementSerializer).some()
          }
          CompositeDecoder.DECODE_DONE -> break
          else -> error("unexpected index: $index")
        }
      }
    }
    return when {
      invalidValue is None && validValue is None -> throw SerializationException("No information found for this Either")
      invalidValue is Some && validValue is Some -> throw SerializationException("Both Left and Right specified for Either")
      invalidValue is Some -> (invalidValue as Some<A>).value.invalid()
      validValue is Some -> (validValue as Some<B>).value.valid()
      else -> error("this should never happen")
    }
  }
}
