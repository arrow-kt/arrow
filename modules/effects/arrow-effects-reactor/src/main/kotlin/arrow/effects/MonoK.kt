package arrow.effects

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.Tuple2
import arrow.effects.CoroutineContextReactorScheduler.asScheduler
import arrow.effects.typeclasses.Disposable
import arrow.effects.typeclasses.ExitCase
import arrow.effects.typeclasses.MonadDefer
import arrow.effects.typeclasses.Proc
import arrow.effects.typeclasses.Fiber
import arrow.effects.typeclasses.ProcF
import reactor.core.publisher.MonoSink
import arrow.higherkind
import reactor.core.publisher.Mono
import reactor.core.publisher.*
import reactor.core.scheduler.Schedulers
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext

fun <A> Mono<A>.k(): MonoK<A> = MonoK(this)

fun <A> MonoKOf<A>.value(): Mono<A> =
  this.fix().mono

@higherkind
data class MonoK<A>(val mono: Mono<A>) : MonoKOf<A>, MonoKKindedJ<A> {
  fun <B> map(f: (A) -> B): MonoK<B> =
    mono.map(f).k()

  fun <B> ap(fa: MonoKOf<(A) -> B>): MonoK<B> =
    flatMap { a -> fa.fix().map { ff -> ff(a) } }

  fun <B> flatMap(f: (A) -> MonoKOf<B>): MonoK<B> =
    mono.flatMap { f(it).fix().mono }.k()

  /**
   * A way to safely acquire a resource and release in the face of errors and cancellation.
   * It uses [ExitCase] to distinguish between different exit cases when releasing the acquired resource.
   *
   * @param use is the action to consume the resource and produce an [MonoK] with the result.
   * Once the resulting [MonoK] terminates, either successfully, error or disposed,
   * the [release] function will run to clean up the resources.
   *
   * @param release the allocated resource after the resulting [MonoK] of [use] is terminates.
   *
   * {: data-executable='true'}
   * ```kotlin:ank
   * import arrow.effects.*
   * import arrow.effects.typeclasses.ExitCase
   *
   * class File(url: String) {
   *   fun open(): File = this
   *   fun close(): Unit {}
   *   fun content(): MonoK<String> =
   *     MonoK.just("This file contains some interesting content!")
   * }
   *
   * fun openFile(uri: String): MonoK<File> = MonoK { File(uri).open() }
   * fun closeFile(file: File): MonoK<Unit> = MonoK { file.close() }
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val safeComputation = openFile("data.json").bracketCase(
   *     release = { file, exitCase ->
   *       when (exitCase) {
   *         is ExitCase.Completed -> { /* do something */ }
   *         is ExitCase.Cancelled -> { /* do something */ }
   *         is ExitCase.Error -> { /* do something */ }
   *       }
   *       closeFile(file)
   *     },
   *     use = { file -> file.content() }
   *   )
   *   //sampleEnd
   *   println(safeComputation)
   * }
   *  ```
   */
  fun <B> bracketCase(use: (A) -> MonoKOf<B>, release: (A, ExitCase<Throwable>) -> MonoKOf<Unit>): MonoK<B> =
    flatMap { a ->
      Mono.create<B> { sink ->
        val d = use(a).fix().flatMap { b ->
          release(a, ExitCase.Completed)
            .fix().map { b }
        }.handleErrorWith { e ->
          if (e is ConnectionCancellationException) release(a, ExitCase.Cancelled).fix().flatMap { MonoK.raiseError<B>(e) }
          else release(a, ExitCase.Error(e)).fix().flatMap { MonoK.raiseError<B>(e) }
        }.mono.subscribe(
          sink::success,
          sink::error,
          sink::success
        )
        sink.onCancel(d)
        sink.onDispose { release(a, ExitCase.Cancelled).fix().mono.subscribe({}, sink::error) }
      }.k()
    }

  fun handleErrorWith(function: (Throwable) -> MonoK<A>): MonoK<A> =
    mono.onErrorResume { t: Throwable -> function(t).mono }.k()

  fun continueOn(ctx: CoroutineContext): MonoK<A> =
    mono.publishOn(ctx.asScheduler()).k()

  fun startF(ctx: CoroutineContext): MonoK<Fiber<ForMonoK, A>> = MonoK {
    val promise = MonoProcessor.create<A>()
    val disp = mono
      .publishOn(ctx.asScheduler())
      .subscribeOn(ctx.asScheduler())
      .subscribe({ a ->
        promise.onNext(a)
        promise.onComplete()
      }, promise::onError)

    Fiber(MonoK(promise), MonoK { disp.dispose(); promise.dispose() })
  }

  fun runAsync(cb: (Either<Throwable, A>) -> MonoKOf<Unit>): MonoK<Unit> =
    mono.flatMap { cb(Right(it)).value() }.onErrorResume { cb(Left(it)).value() }.k()

  fun runAsyncCancellable(cb: (Either<Throwable, A>) -> MonoKOf<Unit>): MonoK<Disposable> =
    Mono.fromCallable {
      val disposable: reactor.core.Disposable = runAsync(cb).value().subscribe()
      val dispose: Disposable = disposable::dispose
      dispose
    }.k()

  override fun equals(other: Any?): Boolean =
    when (other) {
      is MonoK<*> -> this.mono == other.mono
      is Mono<*> -> this.mono == other
      else -> false
    }

  override fun hashCode(): Int = mono.hashCode()

  companion object {
    fun <A> just(a: A): MonoK<A> =
      Mono.just(a).k()

    fun <A> raiseError(t: Throwable): MonoK<A> =
      Mono.error<A>(t).k()

    operator fun <A> invoke(fa: () -> A): MonoK<A> =
      defer { just(fa()) }

    fun <A> defer(fa: () -> MonoKOf<A>): MonoK<A> =
      Mono.defer { fa().value() }.k()

    fun <A, B> racePair(ctx: CoroutineContext, fa: MonoKOf<A>, fb: MonoKOf<B>): MonoK<Either<Tuple2<A, Fiber<ForMonoK, B>>, Tuple2<Fiber<ForMonoK, A>, B>>> {
      val promiseA = MonoProcessor.create<A>()
      val dispA = fa.value().publishOn(ctx.asScheduler()).subscribeOn(ctx.asScheduler())
        .subscribe({ a ->
          promiseA.onNext(a)
          promiseA.onComplete()
        }, promiseA::onError)

      val joinA = MonoK(promiseA)
      val cancelA = MonoK { dispA.dispose() }.flatMap { MonoK { promiseA.dispose() } }
      val fiberA = Fiber(joinA, cancelA)

      val promiseB = MonoProcessor.create<B>()
      val dispB = fb.value().publishOn(ctx.asScheduler()).subscribeOn(ctx.asScheduler())
        .subscribe({ b ->
          promiseB.onNext(b)
          promiseB.onComplete()
        }, promiseB::onError)

      val joinB = MonoK(promiseB)
      val cancelB = MonoK { dispB.dispose() }.flatMap { MonoK { promiseB.dispose() } }
      val fiberB = Fiber(joinB, cancelB)

      return MonoK(Mono.first(
        promiseA.map { a -> Left(Tuple2(a, fiberB)) },
        promiseB.map { b -> Right(Tuple2(fiberA, b)) }
      )
        .doOnCancel {
          cancelA.flatMap { cancelB }
            .mono.subscribeOn(Schedulers.immediate()).publishOn(Schedulers.immediate()).subscribe()
        }
        .doOnError {
          cancelA.flatMap { cancelB }
            .mono.subscribeOn(Schedulers.immediate()).publishOn(Schedulers.immediate()).subscribe()
        })
    }

    /**
     * Creates a [MonoK] that'll run [MonoKProc].
     *
     * {: data-executable='true'}
     *
     * ```kotlin:ank
     * import arrow.core.Either
     * import arrow.core.right
     * import arrow.effects.MonoK
     * import arrow.effects.MonoKConnection
     * import arrow.effects.value
     *
     * class Resource {
     *   fun asyncRead(f: (String) -> Unit): Unit = f("Some value of a resource")
     *   fun close(): Unit = Unit
     * }
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = MonoK.async { conn: MonoKConnection, cb: (Either<Throwable, String>) -> Unit ->
     *     val resource = Resource()
     *     conn.push(MonoK { resource.close() })
     *     resource.asyncRead { value -> cb(value.right()) }
     *   }
     *   //sampleEnd
     *   result.value().subscribe(::println)
     * }
     * ```
     */
    fun <A> async(fa: MonoKProc<A>): MonoK<A> =
      Mono.create<A> { sink ->
        val conn = MonoKConnection()
        val isCancelled = AtomicBoolean(false) //Sink is missing isCancelled so we have to do book keeping.
        conn.push(MonoK { if (!isCancelled.get()) sink.error(ConnectionCancellationException) })
        sink.onCancel {
          isCancelled.compareAndSet(false, true)
          conn.cancel().value().subscribe()
        }

        fa(conn) { either: Either<Throwable, A> ->
          either.fold({
            sink.error(it)
          }, {
            sink.success(it)
          })
        }
      }.k()

    fun <A> asyncF(fa: MonoKProcF<A>): MonoK<A> =
      Mono.create { sink: MonoSink<A> ->
        val conn = MonoKConnection()
        val isCancelled = AtomicBoolean(false) //Sink is missing isCancelled so we have to do book keeping.
        conn.push(MonoK { if (!isCancelled.get()) sink.error(ConnectionCancellationException) })
        sink.onCancel {
          isCancelled.compareAndSet(false, true)
          conn.cancel().value().subscribe()
        }

        fa(conn) { either: Either<Throwable, A> ->
          either.fold({
            sink.error(it)
          }, {
            sink.success(it)
          })
        }.fix().mono.subscribe({}, sink::error)
      }.k()

    tailrec fun <A, B> tailRecM(a: A, f: (A) -> MonoKOf<Either<A, B>>): MonoK<B> {
      val either = f(a).value().block()
      return when (either) {
        is Either.Left -> tailRecM(either.a, f)
        is Either.Right -> Mono.just(either.b).k()
      }
    }
  }
}
