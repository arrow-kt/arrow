package arrow.generic


import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.internal.TaggedEncoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer

@ExperimentalSerializationApi
class GenericEncoder<A>(
  val genericValue: A,
  val kSerializer: KSerializer<A>,
  override val serializersModule: SerializersModule = EmptySerializersModule,
) : AbstractEncoder() {

  var genericName: String? = kSerializer.descriptor.serialName
  var genericProperties: MutableMap<String, Generic<*>> = mutableMapOf()

  private var value: Any? = null
  private var index: Int = -1
  private var serializer: SerializationStrategy<*>? = null
  private var descriptor: SerialDescriptor? = null

  private var state: State = State.Init

  var generic: Generic<A>? = null

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

  override fun encodeValue(value: Any) {
    println("encodeValue: $value")
  }

  override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
    state = State.EncodeElement
    println("encodeElement: $descriptor, $index")
    this.descriptor = descriptor
    this.index = index
//    this.genericName = descriptor.serialName
//    genericProperties[descriptor.elementNames.toList()[index]] = result(descriptor.serialName)
    return true
  }

  override fun encodeInline(inlineDescriptor: SerialDescriptor): Encoder {
    state = State.EncodeInline
    println("encodeInline: $inlineDescriptor")
    this.descriptor = descriptor
    return super.encodeInline(inlineDescriptor)
  }

  override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
    state = State.EncodeSerializableValue
    this.serializer = serializer
    this.value = value
    println("encodeSerializableValue: $serializer, $value")
  }

  override fun <T : Any> encodeNullableSerializableValue(serializer: SerializationStrategy<T>, value: T?) {
    state = State.EncodeNullableSerializableValue
    this.serializer = serializer
    this.value = value
    println("encodeNullableSerializableValue: $serializer, $value")
  }

  override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
    state = State.BeginStructure
    this.descriptor = descriptor
    println("beginStructure: $descriptor")
    return this
  }

  override fun endStructure(descriptor: SerialDescriptor) {
    state = State.EndStructure
    this.descriptor = descriptor
    println("endStructure: $descriptor")
  }

  fun result(): Generic<A> = object : Generic<A> {
    override val name: String = genericName ?: throw IllegalStateException("Expected a generic name")
    override val value: A = genericValue ?: throw IllegalStateException("Expected a generic value")
    override val properties: Map<String, Generic<*>> = genericProperties
  }

}

interface Generic<A> {
  val name: String
  val value: A
  val properties: Map<String, Generic<*>>
}

inline fun <reified A> A.generic(): Generic<A> {
  val ser = serializer<A>()
  val genericEncoder = GenericEncoder<A>(this, ser)
  ser.serialize(genericEncoder, this)
  return genericEncoder.result()
}

@Serializable
data class Person(val name: String, val age: Int, val p: Person2? = null)

@Serializable
data class Person2(val name: String, val age: Int, val p: Person2? = null)

