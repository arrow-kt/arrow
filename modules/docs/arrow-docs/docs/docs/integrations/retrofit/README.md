---
layout: docs
title: Retrofit
permalink: /docs/integrations/retrofit/
---

## Retrofit

{:.advanced}
advanced

Arrow contains a integration module for Retrofit so you can use any synchronous or asynchronous datatype of your choice, like [`Try`]({{ '/docs/datatypes/try' | relative_url }}), [`ObservableK`]({{ '/docs/integrations/rx2' | relative_url }}), [`IO`]({{ '/docs/effects/io' | relative_url }}) or [`DeferredK`]({{ '/docs/integrations/kotlinxcoroutines' | relative_url }}).

Define your endpoints and define for `CallK` as the return Type:

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

You can use `CallK` to have [`Async`]({{ '/docs/effects/async' | relative_url }}), [`MonadDefer`]({{ '/docs/effects/monaddefer' | relative_url }}) and [`MonadError`]({{ '/docs/effects/monaderror' | relative_url }}) intances as your data wrapper.

```kotlin
### Using CallK with IO
createApiClientTest(baseUrl)
        .testCallK() // CallK
        .async(IO.async()) // Kind<ForIO, Response<ResponseMock>>
        .fix() // IO<Response<ResponseMock>>
```

```kotlin
### Using CallK with ObservableK
createApiClientTest(baseUrl)
          .testCallK() // CallK
          .async(ObservableK.async()) // Kind<ForObservableK, Response<ResponseMock>>
          .fix() // ObservableK<Response<ResponseMock>>
```

### Handling `Response` with Arrow

Arrow provides a extension function for `Response<A>` to handle it with Typeclasses. With `unwrapBody` you can extract the body to any kind of ApplicativeError.

```kotlin
val ioResponse: IO<Response<ResponseMock>>
ioResponse.unsafeRunSync() //Response<ResponseMock>
            .unwrapBody(Either.applicativeError()) // Either<Throwable, ResponseMock>
            .fix()
            .fold({ throwable ->
              // Ops!
            }, {
              // Handle information
            })
```

### Using only extensions functions

It is possible to use extension functions for Retrofit's `Call` so the code for the definition of the endpoints doesn't have to change.

```kotlin
    val call : Call<Response<String>>
    call.runAsync(IO.async()) // Kind<ForIO, Response<String>>
          .fix() // IO<Response<String>>
          .unwrapBody(Either.applicativeError()) // Either<Throwable, ResponseMock>
          .fix()
          .fold({ throwable ->
            // Ops!
          }, {
            //Handle information
          })    		    
```

## Available Instances
* [MonadError]({{ '/docs/typeclasses/monaderror' | relative_url }})
* [MonadDefer]({{ '/docs/effects/monaddefer' | relative_url }})
* [Async]({{ '/docs/effects/async' | relative_url }})
* [Effect]({{ '/docs/effects/effect' | relative_url }})
