package arrow.integrations.jackson.module.internal

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.raise.option
import arrow.core.toOption
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer

public data class ElementDeserializer(
  val deserializer: Option<JsonDeserializer<*>>,
  val typeDeserializer: Option<TypeDeserializer>,
) {
  public companion object {
    public fun resolve(
      containedType: JavaType,
      context: DeserializationContext,
      property: BeanProperty?,
    ): ElementDeserializer =
      ElementDeserializer(
        deserializer = context.findContextualValueDeserializer(containedType, property).toOption(),
        typeDeserializer =
          option {
            val prop = property.toOption().bind()
            BeanDeserializerFactory.instance
              .findPropertyTypeDeserializer(context.config, containedType, prop.member)
              .toOption()
              .bind()
          },
      )
  }

  public fun deserialize(
    javaType: JavaType,
    token: JsonToken,
    parser: JsonParser,
    context: DeserializationContext,
  ): Any? =
    when {
      token == JsonToken.VALUE_NULL -> null
      deserializer is Some && typeDeserializer is Some ->
        deserializer.value.deserializeWithType(parser, context, typeDeserializer.value)
      deserializer is Some && typeDeserializer is None ->
        deserializer.value.deserialize(parser, context)
      else ->
        context.handleUnexpectedToken(
          javaType.rawClass,
          token,
          parser,
          "no deserializer was found for given type",
        )
    }
}
