//[arrow-core-retrofit](../../../index.md)/[arrow.retrofit.adapter.either](../index.md)/[EitherCallAdapterFactory](index.md)

# EitherCallAdapterFactory

[jvm]\
class [EitherCallAdapterFactory](index.md) : CallAdapter.Factory

A CallAdapter.Factory which supports suspend + [Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md) as the return type

Adding this to Retrofit will enable you to return [Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md) from your service methods.

interface MyService {\
  @GET("/user/me")\
  suspend fun user(): Either&lt;ErrorBody, User&gt;\
\
  @GET("/user/me")\
  suspend fun userResponse(): EitherR&lt;ErrorBody, User&gt;\
}<!--- KNIT example-arrow-retrofit-01.kt -->

Using [Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md) as the return type means that 200 status code and HTTP errors return a value, other exceptions will throw.

[ResponseE](../-response-e/index.md) is similar to retrofit2.Response but uses [Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md) for the response body.

## Constructors

| | |
|---|---|
| [EitherCallAdapterFactory](-either-call-adapter-factory.md) | [jvm]<br>fun [EitherCallAdapterFactory](-either-call-adapter-factory.md)() |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [jvm]<br>object [Companion](-companion/index.md) |

## Functions

| Name | Summary |
|---|---|
| [get](get.md) | [jvm]<br>open operator override fun [get](get.md)(returnType: [Type](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Type.html), annotations: [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)&lt;[Annotation](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-annotation/index.html)&gt;, retrofit: Retrofit): CallAdapter&lt;*, *&gt;? |
