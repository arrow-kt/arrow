package arrow.integrations.retrofit.adapter.either

import arrow.core.Either
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * A [CallAdapter.Factory] which supports suspend + [Either] as the return type
 *
 * Adding this to [Retrofit] will enable you to return [Either] from your service methods.
 *
 * ```kotlin
 * interface MyService {
 *   @GET("/user/me")
 *   suspend fun user(): Either<ErrorBody, User>
 *
 *   @GET("/user/me")
 *   suspend fun userResponse(): EitherR<ErrorBody, User>
 * }
 * ```
 *
 * Using [Either] as the return type means that 200 status code and HTTP errors return a value,
 * other exceptions will throw.
 *
 * [ResponseE] is similar to [retrofit2.Response] but uses [Either] for the response body.
 */
class EitherCallAdapterFactory : CallAdapter.Factory() {

  companion object {
    fun create(): EitherCallAdapterFactory = EitherCallAdapterFactory()
  }

  override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
    val rawType = getRawType(returnType)

    if (returnType !is ParameterizedType) {
      val name = parseTypeName(returnType)
      throw IllegalArgumentException("Return type must be parameterized as " +
        "$name<Foo> or $name<out Foo>")
    }

    return when (rawType) {
      Call::class.java -> eitherAdapter(returnType, retrofit)
      else -> null
    }
  }

  private fun eitherAdapter(returnType: ParameterizedType, retrofit: Retrofit): CallAdapter<Type, out Call<out Any>>? {
    val wrapperType = getParameterUpperBound(0, returnType)
    return when (getRawType(wrapperType)) {
      Either::class.java -> {
        val (errorType, bodyType) = extractErrorAndReturnType(wrapperType, returnType)
        ArrowEitherCallAdapter<Any, Type>(retrofit, errorType, bodyType)
      }
      ResponseE::class.java -> {
        val (errorType, bodyType) = extractErrorAndReturnType(wrapperType, returnType)
        ArrowResponseECallAdapter<Any, Type>(retrofit, errorType, bodyType)
      }
      else -> null
    }
  }

  private inline fun extractErrorAndReturnType(wrapperType: Type, returnType: ParameterizedType): Pair<Type, Type> {
    if (wrapperType !is ParameterizedType) {
      val name = parseTypeName(returnType)
      throw IllegalArgumentException("Return type must be parameterized as " +
        "$name<ErrorBody, ResponseBody> or $name<out ErrorBody, out ResponseBody>")
    }
    val errorType = getParameterUpperBound(0, wrapperType)
    val bodyType = getParameterUpperBound(1, wrapperType)
    return Pair(errorType, bodyType)
  }
}

private fun parseTypeName(type: Type) =
  type.toString()
    .split(".")
    .last()
