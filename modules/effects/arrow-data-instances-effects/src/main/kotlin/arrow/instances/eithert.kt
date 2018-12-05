package arrow.instances

import arrow.core.*
import arrow.data.*
import arrow.effects.Ref
import arrow.effects.typeclasses.Bracket
import arrow.effects.typeclasses.ExitCase
import arrow.effects.typeclasses.MonadDefer
import arrow.extension
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad

@extension
interface EitherTBracketInstance<F> : Bracket<EitherTPartialOf<F, Throwable>, Throwable>, EitherTMonadErrorInstance<F, Throwable> {

  fun MDF(): MonadDefer<F>

  override fun FF(): Functor<F> = MDF()

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
interface EitherTMonadDeferInstance<F> : MonadDefer<EitherTPartialOf<F, Throwable>>, EitherTBracketInstance<F>  {

  override fun MDF(): MonadDefer<F>

  override fun FF(): Functor<F> = MDF()

  override fun MF(): Monad<F> = MDF()

  override fun <A> defer(fa: () -> EitherTOf<F, Throwable, A>): EitherT<F, Throwable, A> =
    EitherT(MDF().defer { fa().value() })

}
