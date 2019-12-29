---
layout: docs-core
title: Reactor
permalink: /docs/integrations/reactor/
---

## Project Reactor




Arrow aims to enhance the user experience when using Project Reactor. While providing other datatypes that are capable of handling effects, like IO, the style of programming encouraged by the library allows users to generify behavior for any existing abstractions.

One of these abstractions is Project Reactor, a library that, like RxJava, offers reactive streams.

```kotlin
val flux = Flux.just(7, 4, 11 ,3)
    .map { it + 1 }
    .filter { it % 2 == 0 }
    .scan { acc, value -> acc + value }
    .collectList()
    .subscribeOn(Schedulers.parallel())
    .block()
//[8, 20, 24]
```

### Integration with your existing Flux chains

The largest quality of life improvement when using Flux streams in Arrow is the introduction of the [Monad Comprehension]({{ '/docs/patterns/monad_comprehensions' | relative_url }}). This library construct allows expressing asynchronous Flux sequences as synchronous code using binding/bind.

#### Arrow Wrapper

To wrap any existing Flux in its Arrow Wrapper counterpart, you can use the extension function `k()`.

```kotlin:ank
import arrow.fx.reactor.*
import reactor.core.publisher.*

val flux = Flux.just(1, 2, 3, 4, 5).k()
flux
```

```kotlin:ank
val mono = Mono.just(1).k()
mono
```

You can return to their regular forms using the function `value()`.

```kotlin:ank
flux.value()
```

```kotlin:ank
mono.value()
```

### Observable comprehensions

The library provides instances of [`MonadError`]({{ '/docs/arrow/typeclasses/monaderror' | relative_url }}) and [`MonadDefer`]({{ '/docs/effects/monaddefer' | relative_url }}).

[`Async`]({{ '/docs/effects/async' | relative_url }}) allows you to generify over datatypes that can run asynchronous code. You can use it with `FluxK` or `MonoK`.

```kotlin
fun <F> getSongUrlAsync(MS: MonadDefer<F>) =
  MS { getSongUrl() }

val songFlux: FluxKOf<Url> = getSongUrlAsync(FluxK.monadDefer())
val songMono: MonoKOf<Url> = getSongUrlAsync(MonoK.monadDefer())
```

`MonadThrow` can be used to start a [Monad Comprehension]({{ '/docs/patterns/monad_comprehensions' | relative_url }}) using the method `fx.monadThrow`, with all its benefits.

Let's take an example and convert it to a comprehension. We'll create an observable that loads a song from a remote location, and then reports the current play % every 100 milliseconds until the percentage reaches 100%:

```kotlin
getSongUrlAsync()
  .map { songUrl -> MediaPlayer.load(songUrl) }
  .flatMap {
    val totalTime = musicPlayer.getTotaltime()
    Flux.interval(Duration.ofMillis(100))
      .flatMap {
        Flux.create { musicPlayer.getCurrentTime() }
          .map { tick -> (tick / totalTime * 100).toInt() }
      }
      .takeUntil { percent -> percent >= 100 }
  }
```

When rewritten using `fx.monadThrow`, it becomes:

```kotlin
import arrow.fx.reactor.*
import arrow.typeclasses.*
import arrow.fx.reactor.extensions.fluxk.monadThrow.monadThrow

FluxK.monadThrow().fx.monadThrow {
  val (songUrl) = getSongUrlAsync()
  val musicPlayer = MediaPlayer.load(songUrl)
  val totalTime = musicPlayer.getTotaltime()

  val end = DirectProcessor.create<Unit>()
  Flux.interval(Duration.ofMillis(100)).takeUntilOther(end).bind()

  val (tick) = musicPlayer.getCurrentTime()
  val percent = (tick / totalTime * 100).toInt()
  if (percent >= 100) {
    end.onNext(Unit)
  }
  percent
}
```

Note that any unexpected exception, like `ArithmeticException` when `totalTime` is 0, is automatically caught and wrapped inside the flux.

### Subscription and cancellation

Flux streams created with comprehensions like `fx.monadThrow` behave the same way regular flux streams do, including cancellation by disposing the subscription.

```kotlin
val disposable =
  songFlux.value()
    .subscribe({ println("Song $it") }, { System.err.println("Error $it") })

disposable.dispose()
```

### Stack safety

While [`MonadDefer`]({{ '/docs/effects/monaddefer' | relative_url }}) usually guarantees stack safety, this does not apply for the reactor wrapper types.
This is a limitation on reactor's side. See the corresponding GitHub [issue]({{ 'https://github.com/reactor/reactor-core/issues/1441' }}).

To overcome this limitation and run code in a stack in a safe way, one can make use of `fx.stackSafe`, which is provided for every instance of [`Monad`]({{ '/docs/typeclasses/monad' | relative_url }}) when you have `arrow-free` included.

```kotlin:ank:playground
import arrow.Kind
import arrow.fx.reactor.MonoK
import arrow.fx.reactor.ForMonoK
import arrow.fx.reactor.fix
import arrow.fx.reactor.extensions.monok.monad.monad
import arrow.free.stackSafe

fun main() {
  //sampleStart
  // This will not result in a stack overflow
  val result = MonoK.monad().fx.stackSafe {
    (1..50000).fold(just(0)) { acc: Kind<ForMonoK, Int>, x: Int ->
      just(acc.bind() + 1)
    }.bind()
  }.run(MonoK.monad())
  //sampleEnd
  println(result.fix().mono.block()!!)
}
```

```kotlin:ank:fail
import arrow.core.Try

// This will result in a stack overflow
Try {
  MonoK.monad().fx.monad {
    (1..50000).fold(just(0)) { acc: Kind<ForMonoK, Int>, x: Int ->
      just(acc.bind() + 1)
    }.bind()
  }.fix().mono.block()
}
```
