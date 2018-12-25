package arrow.effects

import arrow.core.*
import arrow.effects.CoroutineContextRx2Scheduler.asScheduler
import arrow.effects.typeclasses.ExitCase
import arrow.effects.typeclasses.MonadDefer
import arrow.effects.typeclasses.Proc
import arrow.effects.typeclasses.Fiber
import arrow.effects.typeclasses.ProcF
import arrow.higherkind
import io.reactivex.Maybe
import io.reactivex.MaybeEmitter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.CoroutineContext

fun <A> Maybe<A>.k(): MaybeK<A> = MaybeK(this)

fun <A> MaybeKOf<A>.value(): Maybe<A> = fix().maybe

@higherkind
data class MaybeK<A>(val maybe: Maybe<A>) : MaybeKOf<A>, MaybeKKindedJ<A> {

  fun <B> map(f: (A) -> B): MaybeK<B> =
    maybe.map(f).k()

  fun <B> ap(fa: MaybeKOf<(A) -> B>): MaybeK<B> =
    flatMap { a -> fa.fix().map { ff -> ff(a) } }

  fun <B> flatMap(f: (A) -> MaybeKOf<B>): MaybeK<B> =
    maybe.flatMap { f(it).value() }.k()

  /**
   * A way to safely acquire a resource and release in the face of errors and cancellation.
   * It uses [ExitCase] to distinguish between different exit cases when releasing the acquired resource.
   *
   * @param use is the action to consume the resource and produce an [MaybeK] with the result.
   * Once the resulting [MaybeK] terminates, either successfully, error or disposed,
   * the [release] function will run to clean up the resources.
   *
   * @param release the allocated resource after the resulting [MaybeK] of [use] is terminates.
   *
   * {: data-executable='true'}
   * ```kotlin:ank
   * import arrow.effects.*
   * import arrow.effects.typeclasses.ExitCase
   *
   * class File(url: String) {
   *   fun open(): File = this
   *   fun close(): Unit {}
   *   fun content(): MaybeK<String> =
   *     MaybeK.just("This file contains some interesting content!")
   * }
   *
   * fun openFile(uri: String): MaybeK<File> = MaybeK { File(uri).open() }
   * fun closeFile(file: File): MaybeK<Unit> = MaybeK { file.close() }
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
  fun <B> bracketCase(use: (A) -> MaybeKOf<B>, release: (A, ExitCase<Throwable>) -> MaybeKOf<Unit>): MaybeK<B> =
    flatMap { a ->
      Maybe.create<B> { emitter ->
        val d = use(a).fix()
          .flatMap { b ->
            release(a, ExitCase.Completed)
              .fix().map { b }
          }.handleErrorWith { e ->
            release(a, ExitCase.Error(e))
              .fix().flatMap { MaybeK.raiseError<B>(e) }
          }.maybe.subscribe(emitter::onSuccess, emitter::onError, emitter::onComplete)
        emitter.setDisposable(d.onDispose { release(a, ExitCase.Cancelled).fix().maybe.subscribe({}, emitter::onError) })
      }.k()
    }

  fun <B> fold(ifEmpty: () -> B, ifSome: (A) -> B): B = maybe.blockingGet().let {
    if (it == null) ifEmpty() else ifSome(it)
  }

  fun <B> foldLeft(b: B, f: (B, A) -> B): B =
    fold({ b }, { a -> f(b, a) })

  fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    Eval.defer { fold({ lb }, { a -> f(a, lb) }) }

  fun isEmpty(): Boolean = maybe.isEmpty.blockingGet()

  fun nonEmpty(): Boolean = !isEmpty()

  fun exists(predicate: Predicate<A>): Boolean = fold({ false }, { a -> predicate(a) })

  fun forall(p: Predicate<A>): Boolean = fold({ true }, p)

  fun handleErrorWith(function: (Throwable) -> MaybeKOf<A>): MaybeK<A> =
    maybe.onErrorResumeNext { t: Throwable -> function(t).value() }.k()

  fun continueOn(ctx: CoroutineContext): MaybeK<A> =
    maybe.observeOn(ctx.asScheduler()).k()

  fun startF(ctx: CoroutineContext): MaybeK<Fiber<ForMaybeK, A>> = MaybeK {
    val join = BehaviorSubject.create<A>()
    val disposable = maybe.subscribeOn(ctx.asScheduler()).subscribe({
      join.onNext(it)
      join.onComplete()
    }, join::onError, join::onComplete)
    val cancel = MaybeK { disposable.dispose() }
    Fiber(join.firstElement().k(), cancel)
  }

  fun runAsync(cb: (Either<Throwable, A>) -> MaybeKOf<Unit>): MaybeK<Unit> =
    maybe.flatMap { cb(Right(it)).value() }.onErrorResumeNext(io.reactivex.functions.Function { cb(Left(it)).value() }).k()

  override fun equals(other: Any?): Boolean =
    when (other) {
      is MaybeK<*> -> this.maybe == other.maybe
      is Maybe<*> -> this.maybe == other
      else -> false
    }

  override fun hashCode(): Int = maybe.hashCode()

  companion object {
    fun <A> just(a: A): MaybeK<A> =
      Maybe.just(a).k()

    fun <A> raiseError(t: Throwable): MaybeK<A> =
      Maybe.error<A>(t).k()

    operator fun <A> invoke(fa: () -> A): MaybeK<A> =
      defer { just(fa()) }

    fun <A> defer(fa: () -> MaybeKOf<A>): MaybeK<A> =
      Maybe.defer { fa().value() }.k()

    fun <A, B> racePair(ctx: CoroutineContext, fa: MaybeKOf<A>, fb: MaybeKOf<B>): MaybeK<Either<Tuple2<A, Fiber<ForMaybeK, B>>, Tuple2<Fiber<ForMaybeK, A>, B>>> = MaybeK.async { conn, cb ->
      val disposable = CompositeDisposable()
      val cancel = MaybeK { disposable.dispose() }
      conn.push(cancel)
      val active = AtomicReference(true)
      val promiseA = BehaviorSubject.create<A>()
      val promiseB = BehaviorSubject.create<B>()
      disposable.add(fa.value()
        .subscribeOn(ctx.asScheduler())
        .subscribe({ a ->
          cb(Right(Left(Tuple2(a, Fiber(promiseB.firstElement().k(), cancel)))))
        }, { e ->
          if (active.getAndSet(false)) {
            disposable.dispose()
            conn.pop()
            cb(Left(e))
          } else {
            promiseA.onError(e)
          }
        }))
      disposable.add(fb.value()
        .subscribeOn(ctx.asScheduler())
        .subscribe({ b ->
          disposable.dispose()
          conn.pop()
          cb(Right(Right(Tuple2(Fiber(promiseA.firstElement().k(), cancel), b))))
        }, { e ->
          if (active.getAndSet(false)) {
            disposable.dispose()
            conn.pop()
            cb(Left(e))
          } else {
            promiseB.onError(e)
          }
        }))
    }

    /**
     * Creates a [MaybeK] that'll run [MaybeKProc].
     *
     * {: data-executable='true'}
     *
     * ```kotlin:ank
     * import arrow.core.Either
     * import arrow.core.right
     * import arrow.effects.MaybeK
     * import arrow.effects.MaybeKConnection
     * import arrow.effects.value
     *
     * class Resource {
     *   fun asyncRead(f: (String) -> Unit): Unit = f("Some value of a resource")
     *   fun close(): Unit = Unit
     * }
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = MaybeK.async { conn: MaybeKConnection, cb: (Either<Throwable, String>) -> Unit ->
     *     val resource = Resource()
     *     conn.push(MaybeK { resource.close() })
     *     resource.asyncRead { value -> cb(value.right()) }
     *   }
     *   //sampleEnd
     *   result.value().subscribe(::println)
     * }
     * ```
     */
    fun <A> async(fa: MaybeKProc<A>): MaybeK<A> =
      Maybe.create<A> { emitter ->
        val conn = MaybeKConnection()
        //On disposing of the upstream stream this will be called by `setCancellable` so check if upstream is already disposed or not because
        //on disposing the stream will already be in a terminated state at this point so calling onError, in a terminated state, will blow everything up.
        conn.push(MaybeK { if (!emitter.isDisposed) emitter.onError(ConnectionCancellationException) })
        emitter.setCancellable { conn.cancel().value().subscribe() }

        fa(conn) { either: Either<Throwable, A> ->
          either.fold({
            emitter.onError(it)
          }, {
            emitter.onSuccess(it)
          })

        }
      }.k()

    fun <A> asyncF(fa: MaybeKProcF<A>): MaybeK<A> =
      Maybe.create { emitter: MaybeEmitter<A> ->
        val conn = MaybeKConnection()
        //On disposing of the upstream stream this will be called by `setCancellable` so check if upstream is already disposed or not because
        //on disposing the stream will already be in a terminated state at this point so calling onError, in a terminated state, will blow everything up.
        conn.push(MaybeK { if (!emitter.isDisposed) emitter.onError(ConnectionCancellationException) })
        emitter.setCancellable { conn.cancel().value().subscribe() }

        fa(conn) { either: Either<Throwable, A> ->
          either.fold({
            emitter.onError(it)
          }, {
            emitter.onSuccess(it)
          })
        }.fix().maybe.subscribe({}, emitter::onError)
      }.k()

    tailrec fun <A, B> tailRecM(a: A, f: (A) -> MaybeKOf<Either<A, B>>): MaybeK<B> {
      val either = f(a).value().blockingGet()
      return when (either) {
        is Either.Left -> tailRecM(either.a, f)
        is Either.Right -> Maybe.just(either.b).k()
      }
    }
  }
}
