package arrow.core.serialization

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.left
import arrow.core.none
import arrow.core.right
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

public class EitherSerializer<A, B>(
  private val errorSerializer: KSerializer<A>,
  private val elementSerializer: KSerializer<B>,
) : KSerializer<Either<A, B>> {

  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Either") {
    element("left", errorSerializer.descriptor, isOptional = true)
    element("right", elementSerializer.descriptor, isOptional = true)
  }
  override fun serialize(encoder: Encoder, value: Either<A, B>) {
    encoder.encodeStructure(descriptor) {
      when (value) {
        is Either.Left -> encodeSerializableElement(descriptor, 0, errorSerializer, value.value)
        is Either.Right -> encodeSerializableElement(descriptor, 1, elementSerializer, value.value)
      }
    }
  }
  override fun deserialize(decoder: Decoder): Either<A, B> {
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
      leftValue is None && rightValue is None -> throw SerializationException("No information found for this Either")
      leftValue is Some && rightValue is Some -> throw SerializationException("Both Left and Right specified for Either")
      leftValue is Some -> (leftValue as Some<A>).value.left()
      rightValue is Some -> (rightValue as Some<B>).value.right()
      else -> error("this should never happen")
    }
  }
}
