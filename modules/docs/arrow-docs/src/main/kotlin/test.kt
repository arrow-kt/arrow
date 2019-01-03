import arrow.Kind
import arrow.core.Either.Right
import arrow.effects.*
import arrow.effects.singlek.concurrent.concurrent
import arrow.test.generators.genEither
import arrow.test.generators.genThrowable
import arrow.test.laws.equalUnderTheLaw
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.properties.map
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.TimeUnit
import arrow.effects.CoroutineContextRx2Scheduler.asScheduler
import arrow.effects.internal.ImmediateContext
import arrow.effects.singlek.bracket.bracket
import arrow.effects.typeclasses.milliseconds
import arrow.effects.typeclasses.seconds
import arrow.test.laws.ConcurrentLaws.acquireBracketIsNotCancelable
import arrow.test.laws.ConcurrentLaws.cancelOnBracketReleases
import arrow.test.laws.ConcurrentLaws.racePairCancelCancelsBoth
import arrow.typeclasses.Applicative
import io.reactivex.SingleSource
import io.reactivex.SingleTransformer
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicBoolean

fun <T> EQ(): Eq<SingleKOf<T>> = object : Eq<SingleKOf<T>> {
  override fun SingleKOf<T>.eqv(b: SingleKOf<T>): Boolean =
    try {
      this.value().blockingGet() == b.value().blockingGet()
    } catch (throwable: Throwable) {
      val errA = try {
        this.value().blockingGet()
        throw IllegalArgumentException()
      } catch (err: Throwable) {
        err
      }
      val errB = try {
        b.value().blockingGet()
        throw IllegalStateException()
      } catch (err: Throwable) {
        err
      }
      errA == errB
    }

}

//ObservableTransformer<T, R> nonDisposableWork = upstream -> Observable.defer(() -> {
//  Subject<T> input = PublishSubject.create();
//  Subject<R> output = PublishSubject.create();
//
//  input
//  .flatMap(v -> operation1())
//  .flatMap(w -> operation2())
//  .subscribe(output);
//
//  Disposable d = upstream.subscribe(input);
//
//  return output.doFinally(() -> d.dispose());
//});

fun main(args: Array<String>) = runBlocking<Unit> {

  SingleK(Single.timer(1, TimeUnit.SECONDS))
    .flatMap { SingleK { println("Hello I am here") } }
    .startF(Dispatchers.Default)
    .flatMap { (join, cancel) -> cancel }
    .value()
    .subscribe(::println, ::println)

  SingleK.just(1)
    .startF(Dispatchers.Default)
    .flatMap { (join, cancel) -> cancel }
    .value()
    .subscribe(::println, ::println)

  delay(1000)

//  var count = 0
//  while (true) {
//    val ctx = Dispatchers.Default
//    SingleK.concurrent().run {
//      forAll(Gen.int().map(::just)) { fa ->
//        fa.startF(ctx).flatMap { it.cancel() }.equalUnderTheLaw(just(Unit), EQ())
//      }
//    }
//    delay(500)
//  }
}

//fun <A> nonDisposableWork(fa: Single<A>) = SingleTransformer<Unit, A> { upstream ->
//  val input = PublishSubject.create<Unit>()
//  val output = PublishSubject.create<A>()
//
//  val wasDisposed = AtomicBoolean(false)
//
//  input.flatMap {
//    fa.toObservable()
//      .doAfterTerminate {
//        println("Yea I terminated: wasDisposed.get(): ${wasDisposed.get()} output.hasComplete: ${output.hasComplete()}, output.hasThrowable(): ${output.hasThrowable()}")
//        if (wasDisposed.get()) output.onError(ConnectionCancellationException()) else output.onComplete()
//      }
//  }
//    .subscribe(output)
//
//  val d = upstream
//    .subscribe(input::onNext, input::onError)
//
//  Single.unsafeCreate<Unit> { observer ->
//    output.subscribe()
//  }
//
//  Single.fromObservable(output
//    .doOnError { println("Hello $it") })
//    .doFinally { println("doFinally"); d.dispose() }
//    .doOnDispose { wasDisposed.set(true) }
//}
//
//val d = Single.just(Unit)
//  .compose(nonDisposableWork(Single.timer(2, TimeUnit.SECONDS).flatMap { SingleK { println("Hello from non disposable on ${Thread.currentThread().name}") }.single }))
//  .flatMap {
//    Single.timer(1, TimeUnit.SECONDS).flatMap {
//      SingleK { println("I should be disposed..") }.single
//    }
//  }
//  .subscribeOn(Schedulers.computation())
//  .observeOn(Schedulers.computation())
//  .subscribe(::println, ::println)
//
//delay(1000)
//d.dispose()
//delay(3000)