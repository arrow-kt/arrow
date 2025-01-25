package arrow.integrations.jackson.module.internal

import arrow.core.firstOrNone
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import com.fasterxml.jackson.databind.deser.std.StdDeserializer

public class UnionTypeDeserializer<T>(
  private val clazz: Class<T>,
  private val javaType: JavaType,
  private val fields: List<InjectField<T>>,
) : StdDeserializer<T>(clazz), ContextualDeserializer {
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
        ctxt.handleUnexpectedToken(clazz, parser.currentToken, parser, message) as T
      }
    }
  }

  override fun createContextual(
    ctxt: DeserializationContext,
    property: BeanProperty?,
  ): JsonDeserializer<*> =
    UnionTypeDeserializer(clazz, javaType, fields).also { deserializer ->
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
