package arrow.instances

import arrow.core.*
import arrow.data.*
import arrow.effects.Ref
import arrow.effects.typeclasses.*
import arrow.extension
import arrow.typeclasses.Monad
import kotlin.coroutines.CoroutineContext

@extension
interface EitherTBracketInstance<F> : Bracket<EitherTPartialOf<F, Throwable>, Throwable>, EitherTMonadErrorInstance<F, Throwable> {

  fun MDF(): MonadDefer<F>

  override fun MF(): Monad<F> = MDF()

  override fun <A, B> EitherTOf<F, Throwable, A>.bracketCase(
    release: (A, ExitCase<Throwable>) -> EitherTOf<F, Throwable, Unit>,
    use: (A) -> EitherTOf<F, Throwable, B>): EitherTOf<F, Throwable, B> =

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

@extension
interface EitherTMonadDeferInstance<F> : MonadDefer<EitherTPartialOf<F, Throwable>>, EitherTBracketInstance<F> {

  override fun <A> defer(fa: () -> EitherTOf<F, Throwable, A>): EitherT<F, Throwable, A> =
    EitherT(MDF().defer { fa().value() })

}

@extension
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
