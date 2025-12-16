package arrow.integrations.jackson.module.internal

import arrow.core.firstOrNone
import tools.jackson.core.JsonParser
import tools.jackson.databind.BeanProperty
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.JavaType
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.deser.std.StdDeserializer

public class UnionTypeDeserializer<T>(
  private val javaType: JavaType,
  private val fields: List<InjectField<T>>,
) : StdDeserializer<T>(javaType) {
  public class InjectField<T>(public val fieldName: String, public val point: (Any?) -> T)

  private val deserializers: MutableMap<String, ElementDeserializer> = mutableMapOf()

  override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): T {
    parser.nextToken()
    return when (val field = fields.firstOrNone { parser.currentName() == it.fieldName }) {
      is arrow.core.Some -> {
        val injectField = field.value
        val elementDeserializer =
          requireNotNull(deserializers[injectField.fieldName]) {
            "unexpected deserializer not found"
          }
        val value = elementDeserializer.deserialize(javaType, parser.nextToken(), parser, ctxt)
        parser.nextToken()
        injectField.point(value)
      }
      is arrow.core.None -> {
        val validFields = fields.map { it.fieldName }
        val message = "Cannot deserialize $javaType. Make sure json fields are valid: $validFields."
        @Suppress("UNCHECKED_CAST")
        ctxt.handleUnexpectedToken(javaType, parser.currentToken(), parser, message) as T
      }
    }
  }

  override fun createContextual(
    ctxt: DeserializationContext,
    property: BeanProperty?,
  ): ValueDeserializer<*> = UnionTypeDeserializer(javaType, fields).also { deserializer ->
    fields.forEachIndexed { index, field ->
      deserializer.deserializers[field.fieldName] =
        ElementDeserializer.resolve(
          ctxt.contextualType.containedTypeOrUnknown(index),
          ctxt,
          property,
        )
    }
  }
}
