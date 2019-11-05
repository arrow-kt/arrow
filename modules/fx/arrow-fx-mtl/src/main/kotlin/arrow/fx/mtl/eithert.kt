package arrow.fx.mtl

import arrow.Kind
import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.extensions.either.monad.flatten
import arrow.core.left
import arrow.mtl.EitherT
import arrow.mtl.EitherTOf
import arrow.mtl.EitherTPartialOf
import arrow.mtl.extensions.EitherTMonadThrow
import arrow.mtl.value
import arrow.fx.Ref
import arrow.fx.typeclasses.Async
import arrow.fx.typeclasses.Bracket
import arrow.fx.typeclasses.ConcurrentEffect
import arrow.fx.typeclasses.Disposable
import arrow.fx.typeclasses.Effect
import arrow.fx.typeclasses.ExitCase
import arrow.fx.typeclasses.MonadDefer
import arrow.fx.typeclasses.Proc
import arrow.fx.typeclasses.ProcF
import arrow.extension
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Monad
import arrow.undocumented
import kotlin.coroutines.CoroutineContext

@extension
@undocumented
interface EitherTBracket<F> : Bracket<EitherTPartialOf<Throwable, F>, Throwable>, EitherTMonadThrow<F> {

  fun MDF(): MonadDefer<F>

  override fun MF(): Monad<F> = MDF()

  override fun AE(): ApplicativeError<F, Throwable> = MDF()

  override fun <A, B> EitherTOf<Throwable, F, A>.bracketCase(
    release: (A, ExitCase<Throwable>) -> EitherTOf<Throwable, F, Unit>,
    use: (A) -> EitherTOf<Throwable, F, B>
  ): EitherT<Throwable, F, B> = MDF().run {

    EitherT.liftF<Throwable, F, Ref<F, Option<Throwable>>>(this, Ref(this, None)).flatMap(this) { ref ->
      EitherT(
        value().bracketCase(use = { eith ->
          when (eith) {
            is Either.Right -> use(eith.b).value()
            is Either.Left -> just(eith)
          }
        }, release = { eith, exitCase ->
          when (eith) {
            is Either.Right -> when (exitCase) {
              is ExitCase.Completed -> {
                release(eith.b, ExitCase.Completed).value().flatMap {
                  it.fold(
                    { l -> ref.set(Some(l)) },
                    { just(Unit) }
                  )
                }
              }
              else -> release(eith.b, exitCase).value().unit()
            }
            is Either.Left -> just(Unit)
          }
        }).flatMap { eith ->
          when (eith) {
            is Either.Right -> ref.get().map {
              it.fold(
                { eith },
                { throwable -> throwable.left() })
            }
            is Either.Left -> just(eith)
          }
        })
    }
  }
}

@extension
@undocumented
interface EitherTMonadDefer<F> : MonadDefer<EitherTPartialOf<Throwable, F>>, EitherTBracket<F> {

  override fun MDF(): MonadDefer<F>

  override fun <A> defer(fa: () -> EitherTOf<Throwable, F, A>): EitherT<Throwable, F, A> =
    EitherT(MDF().defer { fa().value() })
}

@extension
@undocumented
interface EitherTAsync<F> : Async<EitherTPartialOf<Throwable, F>>, EitherTMonadDefer<F> {

  fun ASF(): Async<F>

  override fun MDF(): MonadDefer<F> = ASF()

  override fun <A> async(fa: Proc<A>): EitherT<Throwable, F, A> = ASF().run {
    EitherT.liftF(this, async(fa))
  }

  override fun <A> asyncF(k: ProcF<EitherTPartialOf<Throwable, F>, A>): EitherT<Throwable, F, A> = ASF().run {
    EitherT.liftF(this, asyncF { cb -> k(cb).value().unit() })
  }

  override fun <A> Kind<EitherTPartialOf<Throwable, F>, A>.continueOn(ctx: CoroutineContext): Kind<EitherTPartialOf<Throwable, F>, A> = ASF().run {
    EitherT(value().continueOn(ctx))
  }
}

@extension
@undocumented
interface EitherTEffect<F> : Effect<EitherTPartialOf<Throwable, F>>, EitherTAsync<F> {

  fun EFF(): Effect<F>

  override fun ASF(): Async<F> = EFF()

  override fun <A> EitherTOf<Throwable, F, A>.runAsync(cb: (Either<Throwable, A>) -> EitherTOf<Throwable, F, Unit>): EitherT<Throwable, F, Unit> = EFF().run {
    EitherT(value().runAsync { a ->
      cb(a.flatten())
        .value()
        .unit()
    }.attempt())
  }
}

@extension
@undocumented
interface EitherTConcurrentEffect<F> : ConcurrentEffect<EitherTPartialOf<Throwable, F>>, EitherTEffect<F> {

  fun CEFF(): ConcurrentEffect<F>

  override fun EFF(): Effect<F> = CEFF()

  override fun <A> EitherTOf<Throwable, F, A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> EitherTOf<Throwable, F, Unit>): EitherT<Throwable, F, Disposable> = CEFF().run {
    EitherT(value().runAsyncCancellable { a ->
      cb(a.flatten())
        .value()
        .unit()
    }.attempt())
  }
}
