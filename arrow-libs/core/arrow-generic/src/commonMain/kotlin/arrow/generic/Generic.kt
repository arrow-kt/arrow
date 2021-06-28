package arrow.generic


import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.internal.TaggedEncoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer

@ExperimentalSerializationApi
class GenericEncoder<A>(
  var genericValue: A,
//  val kSerializer: KSerializer<Any?>,
  override val serializersModule: SerializersModule,
) : AbstractEncoder() {

  val genericProperties: MutableMap<String, Generic<*>> = mutableMapOf()

  private var value: Any? = null
  private var index: Int = -1
  private var serializer: SerializationStrategy<*>? = null
  private var descriptor: SerialDescriptor? = null
  private var propertyDescriptor: SerialDescriptor? = null
//  private val genericName: String?
//    get() = descriptor?.elementNames?.toList()[index] ?:

  private var state: State = State.Init

  var generic: Generic<Any?>? = null

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
    when (state) {
      State.Init -> {
        genericValue = value as A
      }
      else -> genericProperties[descriptor?.elementNames?.toList()?.get(index)!!] =
        Generic(propertyDescriptor?.serialName ?: value::class.qualifiedName!!, value, emptyMap())
    }
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
    val propertyName: String = descriptor?.elementNames?.toList()?.get(index)!!
    this.serializer = serializer
    this.value = value
    this.propertyDescriptor = serializer.descriptor
    //todo
    val encoder = GenericEncoder(value, serializersModule)
    serializer.serialize(encoder, value)
    genericProperties[propertyName] = encoder.result(serializer.descriptor.serialName)
    println("encodeSerializableValue: $serializer, $value")
  }

  override fun <T : Any> encodeNullableSerializableValue(serializer: SerializationStrategy<T>, value: T?) {
    state = State.EncodeNullableSerializableValue
    val propertyName: String = descriptor?.elementNames?.toList()?.get(index)!!
    this.serializer = serializer
    this.value = value
    this.propertyDescriptor = serializer.descriptor
    if (value != null) {
      //todo
      val encoder = GenericEncoder(value, serializersModule)
      serializer.serialize(encoder, value)
      genericProperties[propertyName] = encoder.result(serializer.descriptor.serialName)
    } else {
      //todo
    }
    println("encodeNullableSerializableValue: $serializer, $value")
  }

  override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
    state = State.BeginStructure
    this.descriptor = descriptor
    println("beginStructure: $descriptor")
    return this //GenericEncoder(serializersModule)
  }

  override fun endStructure(descriptor: SerialDescriptor) {
    state = State.EndStructure
    this.descriptor = descriptor
    println("endStructure: $descriptor")
  }

  fun result(serialName: String): Generic<Any?> = Generic<Any?>(
    name = serialName,
    value = genericValue ?: throw IllegalStateException("Expected a generic value"),
    properties = genericProperties
  )
}

data class Generic<A>(
  val name: String,
  val value: A,
  val properties: Map<String, Generic<*>>,
)


inline fun <reified A> A.generic(
  ser: KSerializer<A> = serializer<A>(),
  serializersModule: SerializersModule = EmptySerializersModule
): Generic<A> {
  val genericEncoder = GenericEncoder<A>(this, serializersModule)
  ser.serialize(genericEncoder, this)
  return genericEncoder.result(ser.descriptor.serialName) as Generic<A>
}

@Serializable
data class Person(val name: String, val age: Int, val p: Person2? = null)

@Serializable
data class Person2(val name: String, val age: Int, val p: Person2? = null)

