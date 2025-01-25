package arrow.integrations.jackson.module

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.left
import arrow.core.right
import arrow.integrations.jackson.module.internal.UnionTypeDeserializer
import arrow.integrations.jackson.module.internal.UnionTypeSerializer
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

public class EitherModule(private val leftFieldName: String, private val rightFieldName: String) :
  SimpleModule(EitherModule::class.java.canonicalName, PackageVersion.VERSION) {
  override fun setupModule(context: SetupContext) {
    super.setupModule(context)
    context.addDeserializers(EitherDeserializerResolver(leftFieldName, rightFieldName))
    context.addSerializers(EitherSerializerResolver(leftFieldName, rightFieldName))
  }
}

public class EitherSerializerResolver(leftFieldName: String, rightFieldName: String) :
  Serializers.Base() {
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
    beanDesc: BeanDescription?,
  ): JsonSerializer<*>? =
    when {
      Either::class.java.isAssignableFrom(javaType.rawClass) -> serializer
      else -> null
    }
}

private fun <E, A> Either<E, A>.orNone(): Option<A> = fold({ None }, ::Some)

public class EitherDeserializerResolver(
  private val leftFieldName: String,
  private val rightFieldName: String,
) : Deserializers.Base() {
  override fun findBeanDeserializer(
    type: JavaType,
    config: DeserializationConfig,
    beanDesc: BeanDescription?,
  ): JsonDeserializer<*>? =
    when {
      Either::class.java.isAssignableFrom(type.rawClass) ->
        UnionTypeDeserializer(
          Either::class.java,
          type,
          listOf(
            UnionTypeDeserializer.InjectField(leftFieldName) { leftValue -> leftValue.left() },
            UnionTypeDeserializer.InjectField(rightFieldName) { rightValue -> rightValue.right() },
          ),
        )
      else -> null
    }
}
