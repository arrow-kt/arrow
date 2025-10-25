package arrow.integrations.jackson.module

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.left
import arrow.core.right
import arrow.integrations.jackson.module.internal.UnionTypeDeserializer
import arrow.integrations.jackson.module.internal.UnionTypeSerializer
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

public class EitherModule(private val leftFieldName: String, private val rightFieldName: String) : SimpleModule(EitherModule::class.java.canonicalName, PackageVersion.VERSION) {
  override fun setupModule(context: SetupContext) {
    super.setupModule(context)
    context.addDeserializers(EitherDeserializerResolver(leftFieldName, rightFieldName))
    context.addSerializers(EitherSerializerResolver(leftFieldName, rightFieldName))
  }
}

public class EitherSerializerResolver(leftFieldName: String, rightFieldName: String) : Serializers.Base() {
  private val serializer =
    UnionTypeSerializer(
      Either::class.java,
      listOf(
        UnionTypeSerializer.ProjectField(leftFieldName) { either -> either.swap().orNone() },
        UnionTypeSerializer.ProjectField(rightFieldName) { either -> either.orNone() },
      ),
    )

  override fun findSerializer(
    config: SerializationConfig,
    javaType: JavaType,
    beanDescRef: BeanDescription.Supplier?,
    formatOverrides: JsonFormat.Value?,
  ): ValueSerializer<*>? = when {
    Either::class.java.isAssignableFrom(javaType.rawClass) -> serializer
    else -> null
  }
}

private fun <E, A> Either<E, A>.orNone(): Option<A> = fold({ None }, ::Some)

public class EitherDeserializerResolver(
  private val leftFieldName: String,
  private val rightFieldName: String,
) : Deserializers.Base() {

  override fun hasDeserializerFor(config: DeserializationConfig, valueType: Class<*>): Boolean = Either::class.java.isAssignableFrom(valueType)

  override fun findBeanDeserializer(
    type: JavaType,
    config: DeserializationConfig,
    beanDesc: BeanDescription.Supplier?,
  ): ValueDeserializer<*>? = when {
    Either::class.java.isAssignableFrom(type.rawClass) ->
      UnionTypeDeserializer(
        type,
        listOf(
          UnionTypeDeserializer.InjectField(leftFieldName) { leftValue -> leftValue.left() },
          UnionTypeDeserializer.InjectField(rightFieldName) { rightValue -> rightValue.right() },
        ),
      )
    else -> null
  }
}
