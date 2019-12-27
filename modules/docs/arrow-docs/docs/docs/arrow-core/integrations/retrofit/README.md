---
layout: docs-core
title: Retrofit
permalink: /docs/integrations/retrofit/
---

## Retrofit




Arrow contains an integration module for Retrofit so you can use any synchronous or asynchronous datatype of your choice, like [`Try`]({{ '/docs/arrow/core/try' | relative_url }}), [`ObservableK`]({{ '/docs/integrations/rx2' | relative_url }}), or [`IO`]({{ '/docs/effects/io' | relative_url }}).


### Using `Call` directly with extensions functions

It is possible to use extension functions for Retrofit's `Call` so the code for the definition of the endpoints doesn't have to change.

```kotlin
val call : Call<Response<String>>
call.runAsync(IO.async()) // Kind<ForIO, Response<String>>
    .fix() // IO<Nothing, Response<String>> 		    
```

```kotlin
val call : Call<Response<String>>
call.runSyncDeferred(IO.monadDefer()) // Kind<ForIO, Response<String>>
    .fix() // IO<Nothing, Response<String>> 		    
```

```kotlin
val call : Call<Response<String>>
call.runSyncCatch(IO.monadError()) // Kind<ForIO, Response<String>>
    .fix() // IO<Nothing, Response<String>> 		    
```

### Using the wrapper `CallK`

Use `CallKindAdapterFactory.create()` to register the Arrow adapter with `Retrofit`. Afterwards, you can start defining your endpoints using `CallK` as the return type:

```kotlin
interface ApiClientTest {

  @GET("test")
  fun testCallK(): CallK<ResponseMock>

  @GET("testCallKResponse")
  fun testCallKResponse(): CallK<ResponseMock>

  @POST("testResponsePOST")
  fun testIOResponsePost(): CallK<Unit>

}
```

You can use `CallK` to have [`Async`]({{ '/docs/effects/async' | relative_url }}), [`MonadDefer`]({{ '/docs/effects/monaddefer' | relative_url }}) and [`MonadError`]({{ '/docs/arrow/typeclasses/monaderror/' | relative_url }}) instances as your data wrapper.

### Using `CallK` with `IO`

```kotlin
createApiClientTest(baseUrl)
  .testCallK() // CallK
  .async(IO.async()) // Kind<ForIO, Response<ResponseMock>>
  .fix() // IO<Nothing, Response<ResponseMock>>
```

### Using `CallK` with `ObservableK`

```kotlin
createApiClientTest(baseUrl)
  .testCallK() // CallK
  .async(ObservableK.async()) // Kind<ForObservableK, Response<ResponseMock>>
  .fix() // ObservableK<Response<ResponseMock>>
```

### Handling `Response` with Arrow

Arrow provides the extension function `unwrapBody()` for `Response<A>` to handle it using [`ApplicativeError<F, Throwable>`]({{ '/docs/arrow/typeclasses/applicativeerror/' | relative_url }}). It wraps any failed response into an `HttpException`, and a missing body with `IllegalStateException`.

```kotlin
val ioResponse: IO<Nothing, Response<ResponseMock>>
ioResponse
  .unsafeRunSync() //Response<ResponseMock>
  .unwrapBody(Either.applicativeError()) // Either<Throwable, ResponseMock>
  .fix()
  .fold({ throwable ->
    // Oops!
  }, {
    // Handle information
  })
```
