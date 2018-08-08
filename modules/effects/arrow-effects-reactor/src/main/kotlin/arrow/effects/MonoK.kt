package arrow.effects

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.effects.CoroutineContextReactorScheduler.asScheduler
import arrow.effects.typeclasses.Proc
import arrow.higherkind
import reactor.core.publisher.Mono
import reactor.core.publisher.MonoSink
import kotlin.coroutines.experimental.CoroutineContext

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

  fun handleErrorWith(function: (Throwable) -> MonoK<A>): MonoK<A> =
      mono.onErrorResume { t: Throwable -> function(t).mono }.k()

  fun continueOn(ctx: CoroutineContext): MonoK<A> =
    mono.publishOn(ctx.asScheduler()).k()

  fun runAsync(cb: (Either<Throwable, A>) -> MonoKOf<Unit>): MonoK<Unit> =
      mono.flatMap { cb(Right(it)).value() }.onErrorResume { cb(Left(it)).value() }.k()

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
      val either = f(a).fix().value().block()
      return when (either) {
        is Either.Left -> tailRecM(either.a, f)
        is Either.Right -> Mono.just(either.b).k()
      }
    }
  }
}
