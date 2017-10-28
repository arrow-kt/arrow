---
layout: docs
title: Rx2
permalink: /docs/integrations/rx2/
---

## RxJava 2

Kategory aims to enhance the user experience when using RxJava. While providing other datatypes that are capable of handling effects, like IO, the style of programming encouraged by the library allows users to generify behavior for any existing abstractions.

One of such abstractions is RxJava, a library focused on providing composable streams that enable reactive programming. Observable streams are created by chaining operators into what are called observable chains.

```kotlin
Observable.from(7, 4, 11, 3)
  .map { it + 1 }
  .filter { it % 2 == 0 }
  .scan { acc, value -> acc + value }
  .toList()
  .subscribeOn(Schedulers.computation())
  .blockingFirst()
// [8, 20, 24]
```

### Integration with your existing Observable chains

The largest quality of life improvement when using Observables in Kategory is the introduction of the [Monad Comprehension]({{ '/docs/patterns/monadcomprehensions' | relative_url }}). This library construct allows expressing asynchronous Observable sequences as synchronous code using binding/bind.

#### Kategory Wrapper

To wrap any existing Observable in its Kategory Wrapper counterpart you can use the extension function `k()`.

```kotlin:ank
import kategory.effects.*
import io.reactivex.*

val obs = Observable.fromArray(1, 2, 3, 4, 5).k()
obs
```

```kotlin:ank
val flow = Flowable.fromArray(1, 2, 3, 4, 5).k()
flow
```

```kotlin:ank
val subject = PublishSubject.create().k()
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
subject.value()
```

### Observable comprehensions

The library provides instances of [`MonadError`]({{ '/docs/typeclasses/monaderror' | relative_url }}) and [`AsyncContext`]({{ '/docs/effects/async' | relative_url }}).

[`AsyncContext`]({{ '/docs/effects/async' | relative_url }}) allows you to generify over datatypes that can run asynchronous code. You can use it with `ObservableKW` and `FlowableKW`.

```kotlin
fun <F> getSongUrlAsync(AC: AsyncContext<F> = asycContext()) =
  AC.runAsync { getSongUrl() }

val songObservable: ObservableKW<Url> = getUsersAsync().ev()
val songFlowable: FlowableKW<Url> = getUsersAsync().ev()
```

[`MonadError`]({{ '/docs/typeclasses/monaderror' | relative_url }}) can be used to start a [Monad Comprehension]({{ '/docs/patterns/monadcomprehensions' | relative_url }}) using the method `bindingE`, with all its benefits.

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

When rewritten using `bindingE` it becomes. Note that any unexpected exception, like AritmeticException when totalTime is 0, is automatically caught and wrapped inside the observable. 

```kotlin
ObservableKW.errorMonad().bindingE {
  val songUrl = getSongUrlAsync().bind()
  val musicPlayer = MediaPlayer.load(songUrl)
  val totalTime = musicPlayer.getTotaltime()

  val end = PublishSubject.create<Unit>()
  Observable.interval(100, Milliseconds).takeUntil(end).bind()

  val tick = bindIn(UI) { Observable.create { musicPlayer.getCurrentTime() } }
  val percent = (tick / totalTime * 100).toInt()
  if (percent >= 100) {
    end.onNext(Unit)
  }

  yield(percent)
}.ev()
```

### Subscription and cancellation

Observables created with `bindingE` behave the same way regular observables do, including cancellation by disposing the subscription.

```kotlin
val disposable = 
  songObservable.value()
    .subscribe({ Log.d("Song $it") } , { prinln("Error $it") })

disposable.dispose()
```

Note that [`MonadError`]({{ '/docs/typeclasses/monaderror' | relative_url }}) provides an alternative to `bindingE` called `bindingECancellable` returning a `kategory.Disposable`. Invoking this `Disposable` causes an `InterruptedException` in the chain which needs to be handled by the subscriber. This behavior is similar to cancelling an `rx.Single`.

```kotlin
val (observable, disposable) = 
  ObservableKW.monadError().bindingECancellable {
    val userProfile = Observable.create { getUserProfile("123") }
    val friendProfiles = userProfile.friends().map { friend ->
        bindAsync(observableAsync) { getProfile(friend.id) }
    }
    yields(listOf(userProfile) + friendProfiles)
  }

observable.value()
  .subscribe({ Log.d("Song $it") } , { prinln("Boom! caused by $it") })

disposable()
// Boom! caused by InterruptedException
```
