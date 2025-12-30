package arrow.integrations.jackson.module

import arrow.core.NonEmptyCollection
import arrow.core.NonEmptyList
import arrow.core.NonEmptySet
import arrow.core.PotentiallyUnsafeNonEmptyOperation
import arrow.core.wrapAsNonEmptyListOrNull
import arrow.core.wrapAsNonEmptySetOrNull
import com.fasterxml.jackson.annotation.JsonFormat
import tools.jackson.core.JsonGenerator
import tools.jackson.core.JsonParser
import tools.jackson.core.json.PackageVersion
import tools.jackson.databind.BeanDescription
import tools.jackson.databind.DeserializationConfig
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.JavaType
import tools.jackson.databind.SerializationConfig
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.ValueSerializer
import tools.jackson.databind.deser.Deserializers
import tools.jackson.databind.deser.std.StdDeserializer
import tools.jackson.databind.jsontype.TypeDeserializer
import tools.jackson.databind.jsontype.TypeSerializer
import tools.jackson.databind.module.SimpleModule
import tools.jackson.databind.ser.Serializers
import tools.jackson.databind.ser.std.StdSerializer
import tools.jackson.databind.type.CollectionType
import tools.jackson.databind.type.TypeBindings
import tools.jackson.databind.type.TypeFactory

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
    beanDescRef: BeanDescription.Supplier?,
    formatOverrides: JsonFormat.Value?,
    elementTypeSerializer: TypeSerializer?,
    elementValueSerializer: ValueSerializer<Any>?,
  ): ValueSerializer<*>? = when {
    NonEmptyCollection::class.java.isAssignableFrom(type.rawClass) -> NonEmptyCollectionSerializer
    else -> null
  }
}

public object NonEmptyCollectionDeserializerResolver : Deserializers.Base() {
  override fun hasDeserializerFor(config: DeserializationConfig, valueType: Class<*>): Boolean = NonEmptyList::class.java.isAssignableFrom(valueType) ||
    NonEmptySet::class.java.isAssignableFrom(valueType)

  @OptIn(PotentiallyUnsafeNonEmptyOperation::class)
  override fun findCollectionDeserializer(
    type: CollectionType,
    config: DeserializationConfig,
    beanDescRef: BeanDescription.Supplier?,
    elementTypeDeserializer: TypeDeserializer?,
    elementDeserializer: ValueDeserializer<*>?,
  ): ValueDeserializer<*>? = when {
    NonEmptyList::class.java.isAssignableFrom(type.rawClass) -> NonEmptyListDeserializer(type.contentType)
    NonEmptySet::class.java.isAssignableFrom(type.rawClass) -> NonEmptySetDeserializer(type.contentType)
    else -> null
  }
}

public object NonEmptyCollectionSerializer : StdSerializer<NonEmptyCollection<*>>(NonEmptyCollection::class.java) {
  override fun serialize(value: NonEmptyCollection<*>, gen: JsonGenerator, provider: SerializationContext) {
    provider.writeValue(gen, value.toList())
  }
}

public class NonEmptyListDeserializer(private val contentType: JavaType) : StdDeserializer<NonEmptyList<*>>(NonEmptyList::class.java) {
  @OptIn(PotentiallyUnsafeNonEmptyOperation::class)
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext): NonEmptyList<*>? {
    val bindings = TypeBindings.create(ArrayList::class.java, contentType)
    val superClass = TypeFactory.createDefaultInstance().constructParametricType(List::class.java, contentType)
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
