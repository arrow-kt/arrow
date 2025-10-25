package arrow.integrations.jackson.module.internal

import arrow.core.Option
import tools.jackson.core.JsonGenerator
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ser.std.StdSerializer

public class ProductTypeSerializer<T>(clazz: Class<T>, private val fields: List<ProjectField<T>>) : StdSerializer<T>(clazz) {
  public class ProjectField<T>(
    public val fieldName: String,
    public val getOption: (T) -> Option<*>,
  )

  override fun serialize(value: T, gen: JsonGenerator, provider: SerializationContext) {
    gen.writeStartObject()
    for (projector in fields) {
      projector.getOption(value).onSome {
        provider.defaultSerializeProperty(projector.fieldName, it, gen)
      }
    }
    gen.writeEndObject()
  }
}
