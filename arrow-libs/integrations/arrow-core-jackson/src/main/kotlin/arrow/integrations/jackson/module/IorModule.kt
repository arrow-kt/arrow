package arrow.integrations.jackson.module

import arrow.core.Ior
import arrow.core.combine
import arrow.core.leftIor
import arrow.core.none
import arrow.core.rightIor
import arrow.core.some
import arrow.integrations.jackson.module.internal.ProductTypeDeserializer
import arrow.integrations.jackson.module.internal.ProductTypeSerializer
import com.fasterxml.jackson.core.json.PackageVersion
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.deser.Deserializers
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.Serializers

public class IorModule(private val leftFieldName: String, private val rightFieldName: String) :
  SimpleModule(IorModule::class.java.canonicalName, PackageVersion.VERSION) {
  override fun setupModule(context: SetupContext) {
    super.setupModule(context)
    context.addDeserializers(IorDeserializerResolver(leftFieldName, rightFieldName))
    context.addSerializers(IorSerializerResolver(leftFieldName, rightFieldName))
  }
}

public class IorSerializerResolver(leftFieldName: String, rightFieldName: String) :
  Serializers.Base() {
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
    beanDesc: BeanDescription?,
  ): JsonSerializer<*>? =
    when {
      Ior::class.java.isAssignableFrom(type.rawClass) -> serializer
      else -> null
    }
}

public class IorDeserializerResolver(
  private val leftFieldName: String,
  private val rightFieldName: String,
) : Deserializers.Base() {

  override fun findBeanDeserializer(
    javaType: JavaType,
    config: DeserializationConfig,
    beanDesc: BeanDescription?,
  ): JsonDeserializer<*>? =
    when {
      Ior::class.java.isAssignableFrom(javaType.rawClass) ->
        ProductTypeDeserializer(
          Ior::class.java,
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
