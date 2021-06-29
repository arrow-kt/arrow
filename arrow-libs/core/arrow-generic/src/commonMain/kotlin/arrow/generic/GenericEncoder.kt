package arrow.generic

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.elementDescriptors
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

@ExperimentalSerializationApi
class GenericEncoder(
//  val kSerializer: KSerializer<Any?>,
  override val serializersModule: SerializersModule,
) : AbstractEncoder() {

  val genericProperties: MutableMap<String, Generic<*>> = mutableMapOf()

  // When this is set, it means that one of the primitive `encodeX` methods was called
  private var genericValue: Generic<*>? = null

  private var value: Any? = null
  private var index: Int = -1
  private var serializer: SerializationStrategy<*>? = null
  private var descriptor: SerialDescriptor? = null
//  private var propertyDescriptor: SerialDescriptor? = null
//  private val genericName: String?
//    get() = descriptor?.elementNames?.toList()[index] ?:

  private var state: State = State.Init

  private enum class State {
    Init,
    BeginStructure,
    EndStructure,
    EncodeValue,
    EncodeElement,
    EncodeInline,
    EncodeSerializableValue,
    EncodeNullableSerializableValue
  }

  fun encodeValue(generic: Generic<*>): Unit =
    if (state == State.Init || state == State.EncodeInline) {
      genericValue = generic
    } else {
      genericProperties[descriptor?.elementNames?.toList()?.get(index)!!] = generic
    }

  override fun encodeString(value: String) {
    encodeValue(Generic.String(value))
  }

  override fun encodeBoolean(value: Boolean) {
    encodeValue(Generic.Boolean(value))
  }

  override fun encodeChar(value: Char) {
    encodeValue(Generic.Char(value))
  }

  override fun encodeByte(value: Byte) {
    encodeValue(Generic.Number.Byte(value))
  }

  override fun encodeShort(value: Short) {
    encodeValue(Generic.Number.Short(value))
  }

  override fun encodeInt(value: Int) {
    encodeValue(Generic.Number.Int(value))
  }

  override fun encodeLong(value: Long) {
    encodeValue(Generic.Number.Long(value))
  }

  override fun encodeFloat(value: Float) {
    encodeValue(Generic.Number.Float(value))
  }

  override fun encodeDouble(value: Double) {
    encodeValue(Generic.Number.Double(value))
  }

  override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
    encodeValue(
      Generic.Enum<Any?>(
        Generic.ObjectInfo(enumDescriptor.serialName),
        enumDescriptor.elementNames.mapIndexed { ord, name -> Generic.EnumValue(name, ord) },
        index
      )
    )
  }

  override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
    state = State.EncodeElement
//    println("encodeElement: $descriptor, $index")
    this.descriptor = descriptor
    this.index = index
//    this.genericName = descriptor.serialName
//    genericProperties[descriptor.elementNames.toList()[index]] = result(descriptor.serialName)
    return true
  }

  override fun encodeInline(inlineDescriptor: SerialDescriptor): Encoder {
    state = State.EncodeInline
//    println("encodeInline: $inlineDescriptor")
    this.descriptor = inlineDescriptor
    return super.encodeInline(inlineDescriptor)
  }

  @InternalSerializationApi
  override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
    state = State.EncodeSerializableValue
    val propertyName: String = descriptor?.elementNames?.toList()?.get(index)!!
    this.serializer = serializer
    this.value = value
    // this.propertyDescriptor = serializer.descriptor
    // todo
    val encoder = GenericEncoder(serializersModule)

    val elementDescriptors = serializer
      .descriptor
      .elementDescriptors
      .lastOrNull()
      ?.elementNames
      ?.toList() ?: emptyList()

    serializer.serialize(encoder, value)
    genericProperties[propertyName] = encoder.result(serializer)
//    println("encodeSerializableValue: $serializer, $value")
  }

  override fun <T : Any> encodeNullableSerializableValue(serializer: SerializationStrategy<T>, value: T?) {
    state = State.EncodeNullableSerializableValue
    val propertyName: String = descriptor?.elementNames?.toList()?.get(index)!!
    this.serializer = serializer
    this.value = value
    // this.propertyDescriptor = serializer.descriptor
    if (value != null) {
      // todo
      val encoder = GenericEncoder(serializersModule)
      serializer.serialize(encoder, value)
      genericProperties[propertyName] = encoder.result(serializer)
    } else {
      // todo
    }
//    println("encodeNullableSerializableValue: $serializer, $value")
  }

  override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
    state = State.BeginStructure
    this.descriptor = descriptor
//    println("beginStructure: $descriptor")
    return this
  }

  override fun endStructure(descriptor: SerialDescriptor) {
    state = State.EndStructure
    this.descriptor = descriptor
//    println("endStructure: $descriptor")
  }

  fun result(serializer: SerializationStrategy<*>): Generic<*> =
    genericValue ?: when (descriptor?.kind) {
      StructureKind.CLASS -> Generic.Product(
        Generic.ObjectInfo(serializer.descriptor.serialName),
        genericProperties.toList()
      )
      StructureKind.OBJECT -> Generic.Product(
        Generic.ObjectInfo(serializer.descriptor.serialName),
        genericProperties.toList()
      )

      // Probably similar to SEALED. Extracting the values.
      PolymorphicKind.OPEN -> genericProperties["value"] ?: throw RuntimeException()
      SerialKind.CONTEXTUAL -> TODO()

      StructureKind.LIST -> TODO()
      StructureKind.MAP -> TODO()

      PolymorphicKind.SEALED ->
        Generic.Coproduct<Any?>(
          Generic.ObjectInfo(serializer.descriptor.serialName),
          Generic.ObjectInfo(this.serializer?.descriptor?.serialName!!),
          // genericProperties contains `value` and `type`
          // Where `type` is a label of the case representing the sum
          // And `value` is the actual instance, we want to extract the fields of the actual instance.
          (genericProperties["value"] as Generic.Product).fields,
          serializer
            .descriptor
            .elementDescriptors
            .last()
            .elementNames
            .indexOf(this.serializer?.descriptor?.serialName!!)
        )
      null -> TODO()
      else -> TODO("Internal error: primitives & enum should be handeled.")
    }
}
