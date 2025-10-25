package arrow.integrations.jackson.module.internal

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.raise.option
import arrow.core.toOption
import tools.jackson.core.JsonParser
import tools.jackson.core.JsonToken
import tools.jackson.databind.BeanProperty
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.JavaType
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.jsontype.TypeDeserializer

public data class ElementDeserializer(
  val deserializer: Option<ValueDeserializer<*>>,
  val typeDeserializer: Option<TypeDeserializer>,
) {
  public companion object {
    public fun resolve(
      containedType: JavaType,
      context: DeserializationContext,
      property: BeanProperty?,
    ): ElementDeserializer = ElementDeserializer(
      deserializer = context.findContextualValueDeserializer(containedType, property).toOption(),
      typeDeserializer = option {
        val prop = property.toOption().bind()
        context.findPropertyTypeDeserializer(containedType, prop.member).toOption().bind()
      },
    )
  }

  public fun deserialize(
    javaType: JavaType,
    token: JsonToken,
    parser: JsonParser,
    context: DeserializationContext,
  ): Any? = when {
    token == JsonToken.VALUE_NULL -> null
    deserializer is Some && typeDeserializer is Some ->
      deserializer.value.deserializeWithType(parser, context, typeDeserializer.value)
    deserializer is Some && typeDeserializer is None ->
      deserializer.value.deserialize(parser, context)
    else ->
      context.handleUnexpectedToken(
        javaType,
        token,
        parser,
        "no deserializer was found for given type",
      )
  }
}
