package arrow.integrations.jackson.module

import arrow.core.NonEmptyCollection
import arrow.core.NonEmptyList
import arrow.core.NonEmptySet
import arrow.core.PotentiallyUnsafeNonEmptyOperation
import arrow.core.wrapAsNonEmptyListOrNull
import arrow.core.wrapAsNonEmptySetOrNull
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
    NonEmptyList::class.java.isAssignableFrom(type.rawClass) -> NonEmptyListDeserializer(type.contentType)
    NonEmptySet::class.java.isAssignableFrom(type.rawClass) -> NonEmptySetDeserializer(type.contentType)
    else -> null
  }
}

public object NonEmptyCollectionSerializer : StdSerializer<NonEmptyCollection<*>>(NonEmptyCollection::class.java) {
  @Suppress("unused")
  private fun readResolve(): Any = NonEmptyCollectionSerializer
  override fun serialize(value: NonEmptyCollection<*>, gen: JsonGenerator, provider: SerializerProvider) {
    provider.defaultSerializeValue(value.toList(), gen)
  }
}

public class NonEmptyListDeserializer(private val contentType: JavaType) : StdDeserializer<NonEmptyList<*>>(NonEmptyList::class.java) {
  @OptIn(PotentiallyUnsafeNonEmptyOperation::class)
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext): NonEmptyList<*>? {
    val bindings = TypeBindings.create(ArrayList::class.java, contentType)
    val superClass = TypeFactory.defaultInstance().constructParametricType(List::class.java, contentType)
    val collection = CollectionType.construct(ArrayList::class.java, bindings, superClass, null, contentType)
    return ctxt.readValue<List<*>>(p, collection).wrapAsNonEmptyListOrNull()
  }
}

public class NonEmptySetDeserializer(private val contentType: JavaType) : StdDeserializer<NonEmptySet<*>>(NonEmptySet::class.java) {
  @OptIn(PotentiallyUnsafeNonEmptyOperation::class)
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext): NonEmptySet<*>? {
    val bindings = TypeBindings.create(LinkedHashSet::class.java, contentType)
    val superClass = TypeFactory.createDefaultInstance().constructParametricType(Set::class.java, contentType)
    val collection = CollectionType.construct(LinkedHashSet::class.java, bindings, superClass, null, contentType)
    return ctxt.readValue<Set<*>>(p, collection).wrapAsNonEmptySetOrNull()
  }
}
