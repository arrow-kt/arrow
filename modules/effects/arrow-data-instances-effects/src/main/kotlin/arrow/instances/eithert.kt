package arrow.instances

import arrow.core.*
import arrow.data.*
import arrow.effects.Ref
import arrow.effects.typeclasses.*
import arrow.instances.either.monad.flatten
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Monad
import kotlin.coroutines.CoroutineContext

interface EitherTBracketInstance<F> : Bracket<EitherTPartialOf<F, Throwable>, Throwable>, EitherTMonadThrowInstance<F> {

  fun MDF(): MonadDefer<F>

  override fun MF(): Monad<F> = MDF()

  override fun AE(): ApplicativeError<F, Throwable> = MDF()

  override fun <A, B> EitherTOf<F, Throwable, A>.bracketCase(
    release: (A, ExitCase<Throwable>) -> EitherTOf<F, Throwable, Unit>,
    use: (A) -> EitherTOf<F, Throwable, B>): EitherT<F, Throwable, B> =

    EitherT.liftF<F, Throwable, Ref<F, Option<Throwable>>>(MDF(), Ref.of(None, MDF())).flatMap(MDF()) { ref ->
      EitherT(
        MDF().run {
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
                else -> release(eith.b, exitCase).value().void()
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
          }
        }
      )
    }

}

fun <F> EitherT.Companion.bracket(MD: MonadDefer<F>): Bracket<EitherTPartialOf<F, Throwable>, Throwable> = object : EitherTBracketInstance<F> {
  override fun MDF(): MonadDefer<F> = MD
}

interface EitherTMonadDeferInstance<F> : MonadDefer<EitherTPartialOf<F, Throwable>>, EitherTBracketInstance<F> {

  override fun MDF(): MonadDefer<F>

  override fun <A> defer(fa: () -> EitherTOf<F, Throwable, A>): EitherT<F, Throwable, A> =
    EitherT(MDF().defer { fa().value() })

}

fun <F> EitherT.Companion.monadDefer(MD: MonadDefer<F>): MonadDefer<EitherTPartialOf<F, Throwable>> = object : EitherTMonadDeferInstance<F> {
  override fun MDF(): MonadDefer<F> = MD
}

interface EitherTAsyncInstance<F> : Async<EitherTPartialOf<F, Throwable>>, EitherTMonadDeferInstance<F> {

  fun ASF(): Async<F>

  override fun MDF(): MonadDefer<F> = ASF()

  override fun <A> async(fa: Proc<A>): EitherT<F, Throwable, A> = ASF().run {
    EitherT.liftF(this, async(fa))
  }

  override fun <A> asyncF(k: ProcF<EitherTPartialOf<F, Throwable>, A>): EitherT<F, Throwable, A> = ASF().run {
    EitherT.liftF(this, asyncF { cb: (Either<Throwable, A>) -> Unit ->
      k(cb).value().map { Unit }
    })
  }

  override fun <A> EitherTOf<F, Throwable, A>.continueOn(ctx: CoroutineContext): EitherT<F, Throwable, A> = ASF().run {
    EitherT(value().continueOn(ctx))
  }

}

fun <F> EitherT.Companion.async(AS: Async<F>): Async<EitherTPartialOf<F, Throwable>> = object : EitherTAsyncInstance<F> {
  override fun ASF(): Async<F> = AS
}

interface EitherTEffectInstance<F> : Effect<EitherTPartialOf<F, Throwable>>, EitherTAsyncInstance<F> {

  fun EFF(): Effect<F>

  override fun ASF(): Async<F> = EFF()

  override fun <A> EitherTOf<F, Throwable, A>.runAsync(cb: (Either<Throwable, A>) -> EitherTOf<F, Throwable, Unit>): EitherT<F, Throwable, Unit> = EFF().run {
    EitherT(value().runAsync { a ->
      cb(a.flatten())
        .value()
        .void()
    }.attempt())
  }

}

fun <F> EitherT.Companion.effect(EFF: Effect<F>): Effect<EitherTPartialOf<F, Throwable>> = object : EitherTEffectInstance<F> {
  override fun EFF(): Effect<F> = EFF
}

interface EitherTConcurrentEffectInstance<F> : ConcurrentEffect<EitherTPartialOf<F, Throwable>>, EitherTEffectInstance<F> {

  fun CEFF(): ConcurrentEffect<F>

  override fun EFF(): Effect<F> = CEFF()

  override fun <A> EitherTOf<F, Throwable, A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> EitherTOf<F, Throwable, Unit>): EitherT<F, Throwable, Disposable> = CEFF().run {
    EitherT(value().runAsyncCancellable { a ->
      cb(a.flatten())
        .value()
        .void()
    }.attempt())
  }

}

fun <F> EitherT.Companion.concurrentEffect(CEFF: ConcurrentEffect<F>): ConcurrentEffect<EitherTPartialOf<F, Throwable>> = object : EitherTConcurrentEffectInstance<F> {
  override fun CEFF(): ConcurrentEffect<F> = CEFF
}
