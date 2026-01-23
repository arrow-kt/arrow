package arrow.integrations.jackson.module

import arrow.core.Ior
import arrow.core.combine
import arrow.core.leftIor
import arrow.core.none
import arrow.core.rightIor
import arrow.core.some
import arrow.integrations.jackson.module.internal.ProductTypeDeserializer
import arrow.integrations.jackson.module.internal.ProductTypeSerializer
import com.fasterxml.jackson.annotation.JsonFormat
import tools.jackson.core.json.PackageVersion
import tools.jackson.databind.BeanDescription
import tools.jackson.databind.DeserializationConfig
import tools.jackson.databind.JavaType
import tools.jackson.databind.SerializationConfig
import tools.jackson.databind.ValueDeserializer
import tools.jackson.databind.ValueSerializer
import tools.jackson.databind.deser.Deserializers
import tools.jackson.databind.module.SimpleModule
import tools.jackson.databind.ser.Serializers

public class IorModule(private val leftFieldName: String, private val rightFieldName: String) : SimpleModule(IorModule::class.java.canonicalName, PackageVersion.VERSION) {
  override fun setupModule(context: SetupContext) {
    super.setupModule(context)
    context.addDeserializers(IorDeserializerResolver(leftFieldName, rightFieldName))
    context.addSerializers(IorSerializerResolver(leftFieldName, rightFieldName))
  }
}

public class IorSerializerResolver(leftFieldName: String, rightFieldName: String) : Serializers.Base() {
  private val serializer =
    ProductTypeSerializer(
      Ior::class.java,
      listOf(
        ProductTypeSerializer.ProjectField(leftFieldName) { ior ->
          ior.fold({ it.some() }, { none() }, { l, _ -> l.some() })
        },
        ProductTypeSerializer.ProjectField(rightFieldName) { ior ->
          ior.fold({ none() }, { it.some() }, { _, r -> r.some() })
        },
      ),
    )

  override fun findSerializer(
    config: SerializationConfig,
    type: JavaType,
    beanDescRef: BeanDescription.Supplier?,
    formatOverrides: JsonFormat.Value?,
  ): ValueSerializer<*>? = when {
    Ior::class.java.isAssignableFrom(type.rawClass) -> serializer
    else -> null
  }
}

public class IorDeserializerResolver(
  private val leftFieldName: String,
  private val rightFieldName: String,
) : Deserializers.Base() {

  override fun hasDeserializerFor(config: DeserializationConfig, valueType: Class<*>): Boolean = Ior::class.java.isAssignableFrom(valueType)

  override fun findBeanDeserializer(
    javaType: JavaType,
    config: DeserializationConfig,
    beanDesc: BeanDescription.Supplier?,
  ): ValueDeserializer<*>? = when {
    Ior::class.java.isAssignableFrom(javaType.rawClass) ->
      ProductTypeDeserializer(
        javaType,
        listOf(
          ProductTypeDeserializer.InjectField(leftFieldName) { firstValue ->
            firstValue.leftIor()
          },
          ProductTypeDeserializer.InjectField(rightFieldName) { secondValue ->
            secondValue.rightIor()
          },
        ),
      ) { iors ->
        // this reduce is safe because an Ior will always have either a left or a right
        iors.reduce { first, second ->
          first.combine(second, { x, y -> y ?: x }, { x, y -> x ?: y })
        }
      }

    else -> null
  }
}
