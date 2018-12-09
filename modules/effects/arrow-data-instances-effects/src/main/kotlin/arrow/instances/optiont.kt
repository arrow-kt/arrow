package arrow.effects.instances

import arrow.core.None
import arrow.data.*
import arrow.effects.Ref
import arrow.effects.typeclasses.*
import arrow.extension
import arrow.instances.OptionTMonadError
import arrow.typeclasses.MonadError
import kotlin.coroutines.CoroutineContext

@extension
interface OptionTBracketInstance<F> : Bracket<OptionTPartialOf<F>, Throwable>, OptionTMonadError<F, Throwable> {

  fun MD(): MonadDefer<F>

  override fun ME(): MonadError<F, Throwable> = MD()

  override fun <A, B> OptionTOf<F, A>.bracketCase(release: (A, ExitCase<Throwable>) -> OptionTOf<F, Unit>, use: (A) -> OptionTOf<F, B>): OptionT<F, B> = MD().run {
    OptionT(Ref.of(false, this).flatMap { ref ->
      value().bracketCase(use = {
        it.fold(
          { just(None) },
          { a -> use(a).value() }
        )
      }, release = { option, exitCase ->
        option.fold(
          { just(Unit) },
          { a ->
            when (exitCase) {
              is ExitCase.Completed -> release(a, exitCase).value().flatMap {
                it.fold({ ref.set(true) }, { just(Unit) })
              }
              else -> release(a, exitCase).value().void()
            }
          }
        )
      }).flatMap { option ->
        option.fold(
          { just(None) },
          { ref.get.map { b -> if (b) None else option } }
        )
      }
    })
  }

}

@extension
interface OptionTMonadDeferInstance<F> : MonadDefer<OptionTPartialOf<F>>, OptionTBracketInstance<F> {

  override fun MD(): MonadDefer<F>

  override fun <A> defer(fa: () -> OptionTOf<F, A>): OptionT<F, A> =
    OptionT(MD().defer { fa().value() })

}

@extension
interface OptionTAsyncInstance<F> : Async<OptionTPartialOf<F>>, OptionTMonadDeferInstance<F> {

  fun AS(): Async<F>

  override fun MD(): MonadDefer<F> = AS()

  override fun <A> async(fa: Proc<A>): OptionT<F, A> = AS().run {
    OptionT.liftF(this, async(fa))
  }

  override fun <A> OptionTOf<F, A>.continueOn(ctx: CoroutineContext): OptionT<F, A> = AS().run {
    OptionT(value().continueOn(ctx))
  }

}
