package arrow.integrations.jackson.module.internal

import arrow.core.Option
import tools.jackson.core.JsonGenerator
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ser.std.StdSerializer

public class UnionTypeSerializer<T>(clazz: Class<T>, private val fields: List<ProjectField<T>>) : StdSerializer<T>(clazz) {
  public class ProjectField<T>(
    public val fieldName: String,
    public val getOption: (T) -> Option<*>,
  )

  override fun serialize(value: T, gen: JsonGenerator, provider: SerializationContext) {
    val project =
      requireNotNull(fields.firstOrNull { it.getOption(value).isSome() }) {
        "unexpected failure when attempting projection during serialization"
      }
    gen.writeStartObject()
    project.getOption(value).onSome {
      provider.defaultSerializeProperty(project.fieldName, it, gen)
    }
    gen.writeEndObject()
  }
}
