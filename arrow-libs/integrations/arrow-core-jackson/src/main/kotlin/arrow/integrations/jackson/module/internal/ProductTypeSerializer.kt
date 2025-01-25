package arrow.integrations.jackson.module.internal

import arrow.core.Option
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer

public class ProductTypeSerializer<T>(clazz: Class<T>, private val fields: List<ProjectField<T>>) :
  StdSerializer<T>(clazz) {
  public class ProjectField<T>(
    public val fieldName: String,
    public val getOption: (T) -> Option<*>,
  )

  override fun serialize(value: T, gen: JsonGenerator, provider: SerializerProvider) {
    gen.writeStartObject()
    for (projector in fields) {
      projector.getOption(value).map {
        provider.defaultSerializeField(projector.fieldName, it, gen)
      }
    }
    gen.writeEndObject()
  }
}
