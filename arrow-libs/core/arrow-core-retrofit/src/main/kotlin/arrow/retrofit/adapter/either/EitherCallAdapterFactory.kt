package arrow.retrofit.adapter.either

import arrow.core.Either
import arrow.retrofit.adapter.either.networkhandling.CallError
import arrow.retrofit.adapter.either.networkhandling.NetworkEitherCallAdapter
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * A [CallAdapter.Factory] which supports suspend + [Either] as the return type
 *
 * Adding this to [Retrofit] will enable you to return [Either] or [ResponseE] from your service
 * methods. [ResponseE] is similar to [retrofit2.Response] but uses [Either] for the response
 * body.
 *
 * ```kotlin
 * import arrow.core.Either
 * import arrow.retrofit.adapter.either.ResponseE
 * import retrofit2.http.GET
 *
 * data class User(val name: String)
 * data class ErrorBody(val msg: String)
 *
 * interface MyService {
 *
 *   @GET("/user/me")
 *   suspend fun getUser(): Either<ErrorBody, User>
 *
 *   @GET("/user/me")
 *   suspend fun getUserResponseE(): ResponseE<ErrorBody, User>
 * }
 * ```
 * <!--- KNIT example-arrow-retrofit-01.kt -->
 *
 * Using [Either] or [ResponseE] as the return type means that 200 status code and HTTP errors
 * return a value, other exceptions will throw.
 *
 * If you want an adapter that never throws but instead wraps all errors (including no network,
 * timeout, malformed JSON) in a dedicated type then define [CallError] as your error type
 * argument. Note that this adapter only supports [Either] as the response wrapper (it does not
 * support [ResponseE]):
 *
 * ```kotlin
 * import arrow.core.Either
 * import arrow.retrofit.adapter.either.networkhandling.CallError
 * import retrofit2.http.Body
 * import retrofit2.http.GET
 * import retrofit2.http.POST
 *
 * data class User(val name: String)
 *
 * interface MyService {
 *
 *   @GET("/user/me")
 *   suspend fun getUser(): Either<CallError, User>
 *
 *   // Set the expected response type as Unit if you expect a null response body
 *   // (e.g. for 204 No Content response)
 *   @POST("/")
 *   suspend fun postSomething(@Body something: String): Either<CallError, Unit>
 * }
 * ```
 * <!--- KNIT example-arrow-retrofit-02.kt -->
 */
public class EitherCallAdapterFactory : CallAdapter.Factory() {

  public companion object {
    public fun create(): EitherCallAdapterFactory = EitherCallAdapterFactory()
  }

  override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
    val rawType = getRawType(returnType)

    if (returnType !is ParameterizedType) {
      val name = parseTypeName(returnType)
      throw IllegalArgumentException(
        "Return type must be parameterized as " +
          "$name<Foo> or $name<out Foo>",
      )
    }

    return when (rawType) {
      Call::class.java -> eitherAdapter(returnType, retrofit)
      else -> null
    }
  }

  private fun eitherAdapter(
    returnType: ParameterizedType,
    retrofit: Retrofit,
  ): CallAdapter<Type, out Call<out Any>>? {
    val wrapperType = getParameterUpperBound(0, returnType)
    return when (getRawType(wrapperType)) {
      Either::class.java -> {
        val (errorType, bodyType) = extractErrorAndReturnType(wrapperType, returnType)
        if (errorType == CallError::class.java) {
          NetworkEitherCallAdapter(bodyType)
        } else {
          ArrowEitherCallAdapter<Any, Type>(retrofit, errorType, bodyType)
        }
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
      throw IllegalArgumentException(
        "Return type must be parameterized as " +
          "$name<ErrorBody, ResponseBody> or $name<out ErrorBody, out ResponseBody>",
      )
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
