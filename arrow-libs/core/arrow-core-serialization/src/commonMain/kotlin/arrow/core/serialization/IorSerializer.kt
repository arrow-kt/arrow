package arrow.core.serialization

import arrow.core.Ior
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.none
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

public class IorSerializer<A, B>(
  private val errorSerializer: KSerializer<A>,
  private val elementSerializer: KSerializer<B>,
) : KSerializer<Ior<A, B>> {

  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Ior") {
    element("left", errorSerializer.descriptor, isOptional = true)
    element("right", elementSerializer.descriptor, isOptional = true)
  }
  override fun serialize(encoder: Encoder, value: Ior<A, B>) {
    encoder.encodeStructure(descriptor) {
      when (value) {
        is Ior.Left -> encodeSerializableElement(descriptor, 0, errorSerializer, value.value)
        is Ior.Right -> encodeSerializableElement(descriptor, 1, elementSerializer, value.value)
        is Ior.Both -> {
          encodeSerializableElement(descriptor, 0, errorSerializer, value.leftValue)
          encodeSerializableElement(descriptor, 1, elementSerializer, value.rightValue)
        }
      }
    }
  }
  override fun deserialize(decoder: Decoder): Ior<A, B> {
    var leftValue: Option<A> = none()
    var rightValue: Option<B> = none()
    decoder.decodeStructure(descriptor) {
      while (true) {
        when (val index = decodeElementIndex(descriptor)) {
          0 -> {
            leftValue = Some(decodeSerializableElement(descriptor, 0, errorSerializer))
          }
          1 -> {
            rightValue = Some(decodeSerializableElement(descriptor, 1, elementSerializer))
          }
          CompositeDecoder.DECODE_DONE -> break
          else -> error("unexpected index: $index")
        }
      }
    }
    return when {
      leftValue is None && rightValue is None -> throw SerializationException("No information found for this Ior")
      leftValue is Some && rightValue is Some -> Ior.Both((leftValue as Some<A>).value, (rightValue as Some<B>).value)
      leftValue is Some -> Ior.Left((leftValue as Some<A>).value)
      rightValue is Some -> Ior.Right((rightValue as Some<B>).value)
      else -> error("this should never happen")
    }
  }
}
