package arrow.integrations.jackson.module

import arrow.core.Nel
import arrow.syntax.function.pipe
import com.fasterxml.jackson.core.json.PackageVersion
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.Deserializers
import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.databind.util.StdConverter

object NonEmptyListModule : SimpleModule(PackageVersion.VERSION) {

  init {
    addSerializer(Nel::class.java, StdDelegatingSerializer(NelSerializationConverter))
  }

  override fun setupModule(context: SetupContext) {
    super.setupModule(context)
    context.addDeserializers(NonEmptyListDeserializerResolver)
  }
}

private object NelSerializationConverter : StdConverter<Nel<*>, List<*>>() {
  override fun convert(value: Nel<*>?): List<*>? = value?.all.orEmpty()
}

private class NelDeserializationConverter(private val elementType: JavaType) : StdConverter<List<Any?>, Nel<Any?>?>() {

  override fun convert(value: List<*>?): Nel<*>? = value?.pipe { Nel.fromList(it).orNull() }

  override fun getInputType(typeFactory: TypeFactory): JavaType =
    typeFactory.constructCollectionType(List::class.java, elementType)

  override fun getOutputType(typeFactory: TypeFactory): JavaType =
    typeFactory.constructCollectionLikeType(Nel::class.java, elementType)
}

private object NonEmptyListDeserializerResolver : Deserializers.Base() {

  private val NON_EMPTY_LIST = Nel::class.java

  override fun findBeanDeserializer(
    type: JavaType,
    config: DeserializationConfig,
    beanDesc: BeanDescription
  ): JsonDeserializer<Nel<*>>? {
    val rawClass = type.rawClass

    return if (!NON_EMPTY_LIST.isAssignableFrom(rawClass)) {
      null
    } else {
      StdDelegatingDeserializer<Nel<*>>(NelDeserializationConverter(type.bindings.getBoundType(0)))
    }
  }
}
