package arrow.integrations.jackson.module

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.Nulls
import tools.jackson.core.json.PackageVersion
import tools.jackson.databind.BeanDescription
import tools.jackson.databind.BeanProperty
import tools.jackson.databind.DeserializationConfig
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.JavaType
import tools.jackson.databind.MapperFeature
import tools.jackson.databind.SerializationConfig
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.ValueSerializer
import tools.jackson.databind.deser.Deserializers
import tools.jackson.databind.deser.ValueInstantiator
import tools.jackson.databind.deser.std.ReferenceTypeDeserializer
import tools.jackson.databind.jsontype.TypeDeserializer
import tools.jackson.databind.jsontype.TypeSerializer
import tools.jackson.databind.module.SimpleModule
import tools.jackson.databind.ser.Serializers
import tools.jackson.databind.ser.std.ReferenceTypeSerializer
import tools.jackson.databind.type.ReferenceType
import tools.jackson.databind.type.TypeBindings
import tools.jackson.databind.type.TypeFactory
import tools.jackson.databind.type.TypeModifier
import tools.jackson.databind.util.NameTransformer
import java.lang.reflect.Type

public object OptionModule :
  SimpleModule(OptionModule::class.java.canonicalName, PackageVersion.VERSION) {

  override fun setupModule(context: SetupContext) {
    super.setupModule(context)
    context.addSerializers(OptionSerializerResolver)
    context.addDeserializers(OptionDeserializerResolver)
    context.addTypeModifier(OptionTypeModifier)
  }
}

public object OptionSerializerResolver : Serializers.Base() {
  override fun findReferenceSerializer(
    config: SerializationConfig,
    type: ReferenceType,
    beanDesc: BeanDescription.Supplier?,
    formatOverrides: JsonFormat.Value?,
    contentTypeSerializer: TypeSerializer?,
    contentValueSerializer: ValueSerializer<Any>?,
  ): ValueSerializer<*>? {
    if (!Option::class.java.isAssignableFrom(type.rawClass)) return null
    val staticTyping = contentTypeSerializer == null && config.isEnabled(MapperFeature.USE_STATIC_TYPING)
    return OptionSerializer(type, staticTyping, contentTypeSerializer, contentValueSerializer)
  }
}

public object OptionDeserializerResolver : Deserializers.Base() {
  override fun hasDeserializerFor(config: DeserializationConfig, valueType: Class<*>): Boolean = Option::class.java.isAssignableFrom(valueType)

  override fun findBeanDeserializer(
    type: JavaType,
    config: DeserializationConfig,
    beanDescRef: BeanDescription.Supplier?,
  ): ValueDeserializer<*>? {
    if (!Option::class.java.isAssignableFrom(type.rawClass)) return null
    return OptionDeserializer(type, null, null, null)
  }

  override fun findReferenceDeserializer(
    type: ReferenceType,
    config: DeserializationConfig,
    beanDesc: BeanDescription.Supplier?,
    contentTypeDeserializer: TypeDeserializer?,
    contentDeserializer: ValueDeserializer<*>?,
  ): ValueDeserializer<*>? {
    if (!Option::class.java.isAssignableFrom(type.rawClass)) return null
    return OptionDeserializer(type, null, contentTypeDeserializer, contentDeserializer)
  }
}

public object OptionTypeModifier : TypeModifier() {
  override fun modifyType(
    type: JavaType,
    jdkType: Type,
    context: TypeBindings?,
    typeFactory: TypeFactory?,
  ): JavaType = when {
    type.isReferenceType || type.isContainerType -> type

    type.rawClass == Option::class.java ->
      ReferenceType.upgradeFrom(type, type.containedTypeOrUnknown(0))

    else -> type
  }
}

public class OptionSerializer : ReferenceTypeSerializer<Option<*>> {
  public constructor(
    fullType: ReferenceType,
    staticTyping: Boolean,
    typeSerializer: TypeSerializer?,
    jsonSerializer: ValueSerializer<Any>?,
  ) : super(fullType, staticTyping, typeSerializer, jsonSerializer)

  public constructor(
    base: OptionSerializer,
    property: BeanProperty?,
    typeSerializer: TypeSerializer?,
    valueSer: ValueSerializer<*>?,
    unwrapper: NameTransformer?,
    suppressableValue: Any?,
    suppressNulls: Boolean,
  ) : super(base, property, typeSerializer, valueSer, unwrapper, suppressableValue, suppressNulls)

  override fun withContentInclusion(
    suppressableValue: Any?,
    suppressNulls: Boolean,
  ): ReferenceTypeSerializer<Option<*>> = OptionSerializer(
    this,
    _property,
    _valueTypeSerializer,
    _valueSerializer,
    _unwrapper,
    suppressableValue,
    suppressNulls,
  )

  override fun _isValuePresent(value: Option<*>): Boolean = value.isSome()

  override fun _getReferenced(value: Option<*>): Any? = value.getOrNull()

  override fun _getReferencedIfPresent(value: Option<*>): Any? = value.getOrNull()

  override fun withResolved(
    prop: BeanProperty?,
    vts: TypeSerializer?,
    valueSer: ValueSerializer<*>?,
    unwrapper: NameTransformer?,
  ): ReferenceTypeSerializer<Option<*>> = OptionSerializer(this, prop, vts, valueSer, unwrapper, _suppressableValue, _suppressNulls)
}

public class OptionDeserializer : ReferenceTypeDeserializer<Option<*>> {
  public constructor(
    fullType: JavaType,
    valueInstantiator: ValueInstantiator?,
    typeDeserializer: TypeDeserializer?,
    jsonDeserializer: ValueDeserializer<*>?,
  ) : super(fullType, valueInstantiator, typeDeserializer, jsonDeserializer)

  override fun withResolved(typeDeser: TypeDeserializer?, valueDeser: ValueDeserializer<*>?): ReferenceTypeDeserializer<Option<*>> = OptionDeserializer(valueType, valueInstantiator, typeDeser, valueDeser)

  override fun findContentNullStyle(ctxt: DeserializationContext?, prop: BeanProperty?): Nulls = Nulls.AS_EMPTY
  override fun getNullValue(ctxt: DeserializationContext?): Option<*> = None
  override fun referenceValue(contents: Any): Option<*> = Some(contents)
  override fun updateReference(reference: Option<*>, contents: Any): Option<*> = reference.map { contents }
  override fun getReferenced(reference: Option<*>): Any? = reference.getOrNull()
}
