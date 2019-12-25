package arrow.integrations.jackson.module

import arrow.core.None
import arrow.core.Option
import arrow.syntax.function.pipe
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.json.PackageVersion
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import com.fasterxml.jackson.databind.deser.Deserializers
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.databind.util.StdConverter

object OptionModule : SimpleModule(PackageVersion.VERSION) {

  init {
    addSerializer(Option::class.java, StdDelegatingSerializer(OptionSerializationConverter))
  }

  override fun setupModule(context: SetupContext) {
    super.setupModule(context)
    context.addDeserializers(OptionDeserializerResolver)
  }
}

private object OptionSerializationConverter : StdConverter<Option<*>, Any>() {
  override fun convert(value: Option<*>?): Any? = value?.orNull()
}

private class OptionDeserializer(
  private val fullType: JavaType,
  private val valueTypeDeserializer: TypeDeserializer?,
  private val valueDeserializer: JsonDeserializer<*>?,
  private val beanProperty: BeanProperty? = null
) : StdDeserializer<Option<*>>(fullType), ContextualDeserializer {

  override fun getValueType(): JavaType = fullType

  override fun getNullValue(): Option<*> = None

  private fun withResolved(
    fullType: JavaType,
    typeDeserializer: TypeDeserializer?,
    valueDeserializer: JsonDeserializer<*>?,
    beanProperty: BeanProperty?
  ): OptionDeserializer {
    return if (fullType == this.fullType &&
      typeDeserializer == this.valueTypeDeserializer &&
      valueDeserializer == this.valueDeserializer &&
      beanProperty == this.beanProperty) {
      this
    } else {
      OptionDeserializer(fullType, typeDeserializer, valueDeserializer, beanProperty)
    }
  }

  override fun createContextual(ctxt: DeserializationContext, property: BeanProperty?): JsonDeserializer<Option<*>> {
    val typeDeser = valueTypeDeserializer?.pipe { it.forProperty(property) }
    var deser = valueDeserializer
    var typ = fullType

    fun refdType(): JavaType = typ.contentType ?: TypeFactory.unknownType()

    if (deser == null) {
      if (property != null) {
        val intr = ctxt.annotationIntrospector
        val member = property.member
        if (intr != null && member != null) {
          typ = intr.refineDeserializationType(ctxt.config, member, typ)
        }
        deser = ctxt.findContextualValueDeserializer(refdType(), property)
      }
    } else { // otherwise directly assigned, probably not contextual yet:
      deser = ctxt.handleSecondaryContextualization(deser, property, refdType()) as JsonDeserializer<*>
    }

    return withResolved(typ, typeDeser, deser, property)
  }

  override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Option<*> {
    val deser = valueDeserializer ?: ctxt.findContextualValueDeserializer(fullType.contentType, beanProperty)
    val refd = valueTypeDeserializer?.pipe { deser.deserializeWithType(p, ctxt, it) } ?: deser.deserialize(p, ctxt)

    return Option.fromNullable(refd)
  }

  override fun deserializeWithType(
    jp: JsonParser,
    ctxt: DeserializationContext,
    typeDeserializer: TypeDeserializer
  ): Option<*> {
    val t = jp.currentToken
    return if (t == JsonToken.VALUE_NULL) {
      getNullValue(ctxt)
    } else {
      typeDeserializer.deserializeTypedFromAny(jp, ctxt) as Option<*>
    }
  }
}

private object OptionDeserializerResolver : Deserializers.Base() {

  private val OPTION = Option::class.java

  override fun findBeanDeserializer(
    type: JavaType,
    config: DeserializationConfig,
    beanDesc: BeanDescription
  ): JsonDeserializer<Option<*>>? {
    val rawClass = type.rawClass

    return if (!OPTION.isAssignableFrom(rawClass)) {
      null
    } else {
      val elementType: JavaType = type.bindings.getBoundType(0)

      val typeDeser: TypeDeserializer? = elementType.getTypeHandler<TypeDeserializer>()
      val valDeser: JsonDeserializer<*>? = elementType.getValueHandler()
      OptionDeserializer(
        config.typeFactory.constructReferenceType(Option::class.java, elementType),
        typeDeser,
        valDeser
      )
    }
  }
}
