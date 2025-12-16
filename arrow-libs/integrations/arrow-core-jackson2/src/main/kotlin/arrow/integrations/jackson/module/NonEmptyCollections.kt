package arrow.integrations.jackson.module

import arrow.core.NonEmptyCollection
import arrow.core.NonEmptyList
import arrow.core.NonEmptySet
import arrow.core.toNonEmptyListOrNull
import arrow.core.toNonEmptySetOrNull
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.json.PackageVersion
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.Deserializers
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer
import com.fasterxml.jackson.databind.jsontype.TypeSerializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.Serializers
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.databind.type.CollectionType
import com.fasterxml.jackson.databind.type.TypeBindings
import com.fasterxml.jackson.databind.type.TypeFactory

public class NonEmptyCollectionsModule : SimpleModule(NonEmptyCollectionsModule::class.java.name, PackageVersion.VERSION) {
  override fun setupModule(context: SetupContext) {
    super.setupModule(context)
    context.addSerializers(NonEmptyCollectionSerializerResolver)
    context.addDeserializers(NonEmptyCollectionDeserializerResolver)
  }
}

public object NonEmptyCollectionSerializerResolver : Serializers.Base() {
  override fun findCollectionSerializer(
    config: SerializationConfig,
    type: CollectionType,
    beanDesc: BeanDescription?,
    elementTypeSerializer: TypeSerializer?,
    elementValueSerializer: JsonSerializer<Any>?,
  ): JsonSerializer<*>? = when {
    NonEmptyCollection::class.java.isAssignableFrom(type.rawClass) -> NonEmptyCollectionSerializer
    else -> null
  }
}

public object NonEmptyCollectionDeserializerResolver : Deserializers.Base() {
  override fun findCollectionDeserializer(
    type: CollectionType,
    config: DeserializationConfig,
    beanDesc: BeanDescription?,
    elementTypeDeserializer: TypeDeserializer?,
    elementDeserializer: JsonDeserializer<*>?,
  ): JsonDeserializer<*>? = when {
    NonEmptyList::class.java.isAssignableFrom(type.rawClass) ->
      NonEmptyCollectionDeserializer(type.contentType, NonEmptyList::class.java) { it.toNonEmptyListOrNull() }
    NonEmptySet::class.java.isAssignableFrom(type.rawClass) ->
      NonEmptyCollectionDeserializer(type.contentType, NonEmptySet::class.java) { it.toNonEmptySetOrNull() }
    else -> null
  }
}

public object NonEmptyCollectionSerializer : StdSerializer<NonEmptyCollection<*>>(NonEmptyCollection::class.java) {
  override fun serialize(value: NonEmptyCollection<*>, gen: JsonGenerator, provider: SerializerProvider) {
    provider.defaultSerializeValue(value.toList(), gen)
  }
}

public class NonEmptyCollectionDeserializer<T : NonEmptyCollection<*>>(
  private val contentType: JavaType,
  klass: Class<T>,
  private val converter: (List<*>) -> T?,
) : StdDeserializer<T>(klass) {
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext): T? {
    val bindings = TypeBindings.create(ArrayList::class.java, contentType)
    val superClass = TypeFactory.defaultInstance().constructParametricType(List::class.java, contentType)
    val collection = CollectionType.construct(ArrayList::class.java, bindings, superClass, null, contentType)
    return converter(ctxt.readValue(p, collection))
  }
}
