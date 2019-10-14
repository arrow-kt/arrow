package arrow.fx.mtl

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
interface EitherTBracket<F> : Bracket<EitherTPartialOf<F, Throwable>, Throwable>, EitherTMonadThrow<F> {

  fun MDF(): MonadDefer<F>

  override fun MF(): Monad<F> = MDF()

  override fun AE(): ApplicativeError<F, Throwable> = MDF()

  override fun <A, B> EitherTOf<F, Throwable, A>.bracketCase(
    release: (A, ExitCase<Throwable>) -> EitherTOf<F, Throwable, Unit>,
    use: (A) -> EitherTOf<F, Throwable, B>
  ): EitherT<F, Throwable, B> = MDF().run {

    EitherT.liftF<F, Throwable, Ref<F, Option<Throwable>>>(this, Ref(this, None)).flatMap(this) { ref ->
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
interface EitherTMonadDefer<F> : MonadDefer<EitherTPartialOf<F, Throwable>>, EitherTBracket<F> {

  override fun MDF(): MonadDefer<F>

  override fun <A> defer(fa: () -> EitherTOf<F, Throwable, A>): EitherT<F, Throwable, A> =
    EitherT(MDF().defer { fa().value() })
}

@extension
@undocumented
interface EitherTAsync<F> : Async<EitherTPartialOf<F, Throwable>>, EitherTMonadDefer<F> {

  fun ASF(): Async<F>

  override fun MDF(): MonadDefer<F> = ASF()

  override fun <A> async(fa: Proc<A>): EitherT<F, Throwable, A> = ASF().run {
    EitherT.liftF(this, async(fa))
  }

  override fun <A> asyncF(k: ProcF<EitherTPartialOf<F, Throwable>, A>): EitherT<F, Throwable, A> = ASF().run {
    EitherT.liftF(this, asyncF { cb -> k(cb).value().unit() })
  }

  override fun <A> EitherTOf<F, Throwable, A>.continueOn(ctx: CoroutineContext): EitherT<F, Throwable, A> = ASF().run {
    EitherT(value().continueOn(ctx))
  }
}

@extension
@undocumented
interface EitherTEffect<F> : Effect<EitherTPartialOf<F, Throwable>>, EitherTAsync<F> {

  fun EFF(): Effect<F>

  override fun ASF(): Async<F> = EFF()

  override fun <A> EitherTOf<F, Throwable, A>.runAsync(cb: (Either<Throwable, A>) -> EitherTOf<F, Throwable, Unit>): EitherT<F, Throwable, Unit> = EFF().run {
    EitherT(value().runAsync { a ->
      cb(a.flatten())
        .value()
        .unit()
    }.attempt())
  }
}

@extension
@undocumented
interface EitherTConcurrentEffect<F> : ConcurrentEffect<EitherTPartialOf<F, Throwable>>, EitherTEffect<F> {

  fun CEFF(): ConcurrentEffect<F>

  override fun EFF(): Effect<F> = CEFF()

  override fun <A> EitherTOf<F, Throwable, A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> EitherTOf<F, Throwable, Unit>): EitherT<F, Throwable, Disposable> = CEFF().run {
    EitherT(value().runAsyncCancellable { a ->
      cb(a.flatten())
        .value()
        .unit()
    }.attempt())
  }
}
