package arrow.fx.mtl

import arrow.core.None
import arrow.mtl.OptionT
import arrow.mtl.OptionTOf
import arrow.mtl.OptionTPartialOf
import arrow.mtl.extensions.OptionTMonadError
import arrow.mtl.value
import arrow.fx.Ref
import arrow.fx.typeclasses.Async
import arrow.fx.typeclasses.Bracket
import arrow.fx.typeclasses.ExitCase
import arrow.fx.typeclasses.MonadDefer
import arrow.fx.typeclasses.Proc
import arrow.fx.typeclasses.ProcF
import arrow.extension
import arrow.typeclasses.MonadError
import arrow.undocumented
import kotlin.coroutines.CoroutineContext

@extension
@undocumented
interface OptionTBracket<F> : Bracket<OptionTPartialOf<F>, Throwable>, OptionTMonadError<F, Throwable> {

  fun MD(): MonadDefer<F>

  override fun ME(): MonadError<F, Throwable> = MD()

  override fun <A, B> OptionTOf<F, A>.bracketCase(release: (A, ExitCase<Throwable>) -> OptionTOf<F, Unit>, use: (A) -> OptionTOf<F, B>): OptionT<F, B> = MD().run {
    OptionT(Ref(this, false).flatMap { ref ->
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
              else -> release(a, exitCase).value().unit()
            }
          }
        )
      }).flatMap { option ->
        option.fold(
          { just(None) },
          { ref.get().map { b -> if (b) None else option } }
        )
      }
    })
  }
}

@extension
@undocumented
interface OptionTMonadDefer<F> : MonadDefer<OptionTPartialOf<F>>, OptionTBracket<F> {

  override fun MD(): MonadDefer<F>

  override fun <A> defer(fa: () -> OptionTOf<F, A>): OptionT<F, A> =
    OptionT(MD().defer { fa().value() })
}

@extension
@undocumented
interface OptionTAsync<F> : Async<OptionTPartialOf<F>>, OptionTMonadDefer<F> {

  fun AS(): Async<F>

  override fun MD(): MonadDefer<F> = AS()

  override fun <A> async(fa: Proc<A>): OptionT<F, A> = AS().run {
    OptionT.liftF(this, async(fa))
  }

  override fun <A> asyncF(k: ProcF<OptionTPartialOf<F>, A>): OptionT<F, A> = AS().run {
    OptionT.liftF(this, asyncF { cb -> k(cb).value().unit() })
  }

  override fun <A> OptionTOf<F, A>.continueOn(ctx: CoroutineContext): OptionT<F, A> = AS().run {
    OptionT(value().continueOn(ctx))
  }
}
