package arrow.effects

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.effects.CoroutineContextReactorScheduler.asScheduler
import arrow.effects.typeclasses.Disposable
import arrow.effects.typeclasses.ExitCase
import arrow.effects.typeclasses.Proc
import arrow.higherkind
import reactor.core.publisher.Mono
import reactor.core.publisher.MonoSink
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
          release(a, ExitCase.Error(e))
            .fix().flatMap { MonoK.raiseError<B>(e) }
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

    fun <A> async(fa: Proc<A>): MonoK<A> =
      Mono.create { emitter: MonoSink<A> ->
        fa { either: Either<Throwable, A> ->
          either.fold({
            emitter.error(it)
          }, {
            emitter.success(it)
          })
        }
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
