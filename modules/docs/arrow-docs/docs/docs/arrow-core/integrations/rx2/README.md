---
layout: docs-core
title: Rx2
permalink: /docs/integrations/rx2/
---

## RxJava 2




Arrow aims to enhance the user experience when using RxJava. While providing other datatypes that are capable of handling effects, like IO, the style of programming encouraged by the library allows users to generify behavior for any existing abstractions.

One of these abstractions is RxJava, a library focused on providing composable streams that enable reactive programming. Observable streams are created by chaining operators into what are called observable chains.

```kotlin
Observable.from(7, 4, 11, 3)
  .map { it + 1 }
  .filter { it % 2 == 0 }
  .scan { acc, value -> acc + value }
  .toList()
  .subscribeOn(Schedulers.computation())
  .blockingFirst()
//[8, 20, 24]
```

### Integration with your existing Observable chains

The largest quality of life improvement when using Observables in Arrow is the introduction of the [Monad Comprehension]({{ '/docs/patterns/monad_comprehensions' | relative_url }}). This library construct allows expressing asynchronous Observable sequences as synchronous code using binding/bind.

#### Arrow Wrapper

To wrap any existing Observable in its Arrow Wrapper counterpart, you can use the extension function `k()`.

```kotlin:ank
import arrow.fx.rx2.*
import io.reactivex.*
import io.reactivex.subjects.*

val obs = Observable.fromArray(1, 2, 3, 4, 5).k()
obs
```

```kotlin:ank
val flow = Flowable.fromArray(1, 2, 3, 4, 5).k()
flow
```

```kotlin:ank
val single = Single.fromCallable { 1 }.k()
single
```

```kotlin:ank
val maybe = Maybe.fromCallable { 1 }.k()
maybe
```

```kotlin:ank
val subject = PublishSubject.create<Int>().k()
subject
```

You can return to their regular forms using the function `value()`.

```kotlin:ank
obs.value()
```

```kotlin:ank
flow.value()
```

```kotlin:ank
single.value()
```

```kotlin:ank
maybe.value()
```

```kotlin:ank
subject.value()
```

### Support for suspend functions

Arrow adds a new constructor `effect` that allows using suspend functions with `Observable`, `Single`, and `Flowable`.

```kotlin:ank
suspend fun sideEffect(): Unit = println("Hello!")
```


```kotlin
ObservableK.async().effect {
 sideEffect()
}
```

```kotlin
SingleK.async().effect {
 sideEffect()
}
```

```kotlin
FlowableK.async().effect {
 sideEffect()
}
```

### Observable comprehensions

The library provides instances of [`MonadError`]({{ '/docs/arrow/typeclasses/monaderror' | relative_url }}) and [`MonadDefer`]({{ '/docs/effects/monaddefer' | relative_url }}).

[`Async`]({{ '/docs/effects/async' | relative_url }}) allows you to generify over datatypes that can run asynchronous code. You can use it with `ObservableK`, `FlowableK`, or `SingleK`.

```kotlin
fun <F> getSongUrlAsync(MS: MonadDefer<F>) =
  MS { getSongUrl() }

val songObservable: ObservableKOf<Url> = getSongUrlAsync(ObservableK.monadDefer())
val songFlowable: FlowableKOf<Url> = getSongUrlAsync(FlowableK.monadDefer())
val songSingle: SingleKOf<Url> = getSongUrlAsync(SingleK.monadDefer())
val songMaybe: MaybeKOf<Url> = getSongUrlAsync(MaybeK.monadDefer())
```

[`Monad`]({{ '/docs/arrow/typeclasses/monad' | relative_url }}) can be used to start a [Monad Comprehension]({{ '/docs/patterns/monad_comprehensions' | relative_url }}) using the method `fx`, with all its benefits.

Let's take an example and convert it to a comprehension. We'll create an observable that loads a song from a remote location, and then reports the current play % every 100 milliseconds until the percentage reaches 100%:

```kotlin
getSongUrlAsync()
  .map { songUrl -> MediaPlayer.load(songUrl) }
  .flatMap {
    val totalTime = musicPlayer.getTotaltime()
    Observable.interval(100, Milliseconds)
      .flatMap {
        Observable.create { musicPlayer.getCurrentTime() }
          .subscribeOn(AndroidSchedulers.mainThread())
          .map { tick -> (tick / totalTime * 100).toInt() }
      }
      .takeUntil { percent -> percent >= 100 }
      .observeOn(Schedulers.immediate())
  }
```

When rewritten using `fx` it becomes:

```kotlin
import arrow.fx.rx2.*
import arrow.fx.rx2.extensions.fx
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

ObservableK.fx {
  val (songUrl) = getSongUrlAsync()
  val musicPlayer = MediaPlayer.load(songUrl)
  val totalTime = musicPlayer.getTotaltime()

  val end = PublishSubject.create<Unit>()
  !Observable.interval(100, TimeUnit.MILLISECONDS).takeUntil(end).k()

  val tick = !delay(UI) { musicPlayer.getCurrentTime() }
  val percent = (tick / totalTime * 100).toInt()
  if (percent >= 100) {
    end.onNext(Unit)
  }

  percent
}
```

Note that any unexpected exception, like `ArithmeticException` when `totalTime` is 0, is automatically caught and wrapped inside the observable.

### Subscription and cancellation

Observables created with comprehensions like `fx` behave the same way regular observables do, including cancellation by disposing the subscription.

```kotlin
val disposable =
  songObservable.value()
    .subscribe({ Log.d("Song $it") } , { println("Error $it") })

disposable.dispose()
```

### Stack safety

While [`MonadDefer`]({{ '/docs/effects/monaddefer' | relative_url }}) usually guarantees stack safety, this does not apply for the rx2 wrapper types.
This is a limitation on rx2's side. See the corresponding github [issue]({{ 'https://github.com/ReactiveX/RxJava/issues/6322' }}).

To overcome this limitation and run code in a stack in a safe way, one can make use of `fx.stackSafe` which is provided for every instance of [`Monad`]({{ '/docs/typeclasses/monad' | relative_url }}) when you have `arrow-free` included.

```kotlin:ank:playground
import arrow.Kind
import arrow.fx.rx2.FlowableK
import arrow.fx.rx2.ForFlowableK
import arrow.fx.rx2.fix
import arrow.fx.rx2.extensions.flowablek.monad.monad
import arrow.free.stackSafe

fun main() {
  //sampleStart
  // This will not result in a stack overflow
  val result = FlowableK.monad().fx.stackSafe {
    (1..50000).fold(just(0)) { acc: Kind<ForFlowableK, Int>, x: Int ->
      just(acc.bind() + 1)
    }.bind()
  }.run(FlowableK.monad())
  //sampleEnd
  println(result.fix().flowable.blockingFirst()!!)
}
```

```kotlin:ank:fail
import arrow.core.Try
// This will result in a stack overflow

Try {
  FlowableK.monad().fx.monad {
    (1..50000).fold(just(0)) { acc: Kind<ForFlowableK, Int>, x: Int ->
      just(acc.bind() + 1)
    }.bind()
  }.fix().flowable.blockingFirst()
}
```
