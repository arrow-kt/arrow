package arrow.effects

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.effects.CoroutineContextScheduler.asScheduler
import arrow.effects.typeclasses.Proc
import arrow.higherkind
import io.reactivex.Single
import io.reactivex.SingleEmitter
import kotlin.coroutines.experimental.CoroutineContext

fun <A> Single<A>.k(): SingleK<A> = SingleK(this)

fun <A> SingleKOf<A>.value(): Single<A> = this.fix().single

@higherkind
data class SingleK<A>(val single: Single<A>) : SingleKOf<A>, SingleKKindedJ<A> {

  fun <B> map(f: (A) -> B): SingleK<B> =
    single.map(f).k()

  fun <B> ap(fa: SingleKOf<(A) -> B>): SingleK<B> =
    flatMap { a -> fa.fix().map { ff -> ff(a) } }

  fun <B> flatMap(f: (A) -> SingleKOf<B>): SingleK<B> =
    single.flatMap { f(it).fix().single }.k()

  fun handleErrorWith(function: (Throwable) -> SingleK<A>): SingleK<A> =
    single.onErrorResumeNext { t: Throwable -> function(t).single }.k()

  fun continueOn(ctx: CoroutineContext): SingleK<A> =
    single.observeOn(ctx.asScheduler()).k()

  fun runAsync(cb: (Either<Throwable, A>) -> SingleKOf<Unit>): SingleK<Unit> =
    single.flatMap { cb(Right(it)).value() }.onErrorResumeNext(io.reactivex.functions.Function { cb(Left(it)).value() }).k()

  companion object {
    fun <A> just(a: A): SingleK<A> =
      Single.just(a).k()

    fun <A> raiseError(t: Throwable): SingleK<A> =
      Single.error<A>(t).k()

    operator fun <A> invoke(fa: () -> A): SingleK<A> =
      defer { just(fa()) }

    fun <A> defer(fa: () -> SingleKOf<A>): SingleK<A> =
      Single.defer { fa().value() }.k()

    fun <A> async(fa: Proc<A>): SingleK<A> =
      Single.create({ emitter: SingleEmitter<A> ->
        fa { either: Either<Throwable, A> ->
          either.fold({
            emitter.onError(it)
          }, {
            emitter.onSuccess(it)
          })

        }
      }).k()

    tailrec fun <A, B> tailRecM(a: A, f: (A) -> SingleKOf<Either<A, B>>): SingleK<B> {
      val either = f(a).fix().value().blockingGet()
      return when (either) {
        is Either.Left -> tailRecM(either.a, f)
        is Either.Right -> Single.just(either.b).k()
      }
    }
  }
}
