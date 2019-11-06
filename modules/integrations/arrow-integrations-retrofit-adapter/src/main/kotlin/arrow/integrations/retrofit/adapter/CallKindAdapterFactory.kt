package arrow.integrations.retrofit.adapter

import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class CallKindAdapterFactory : CallAdapter.Factory() {

  companion object {
    fun create(): CallKindAdapterFactory = CallKindAdapterFactory()
  }

  override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
    val rawType = CallAdapter.Factory.getRawType(returnType)

    if (returnType !is ParameterizedType) {
      val name = parseTypeName(returnType)
      throw IllegalArgumentException("Return type must be parameterized as " +
        "$name<Foo> or $name<out Foo>")
    }

    val effectType = CallAdapter.Factory.getParameterUpperBound(0, returnType)

    return if (rawType == CallK::class.java) {
      CallKind2CallAdapter<Type>(effectType)
    } else {
      null
    }
  }
}

private fun parseTypeName(type: Type) =
  type.toString()
    .split(".")
    .last()
