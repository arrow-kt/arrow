package arrow.fx.mtl

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.Tuple2
import arrow.core.getOrElse
import arrow.mtl.StateT
import arrow.mtl.StateTOf
import arrow.mtl.StateTPartialOf
import arrow.mtl.extensions.StateTMonadThrow
import arrow.mtl.fix
import arrow.fx.Ref
import arrow.fx.typeclasses.Async
import arrow.fx.typeclasses.Bracket
import arrow.fx.typeclasses.ExitCase
import arrow.fx.typeclasses.MonadDefer
import arrow.fx.typeclasses.Proc
import arrow.fx.typeclasses.ProcF
import arrow.extension
import arrow.mtl.runM
import arrow.typeclasses.MonadError
import arrow.undocumented
import kotlin.coroutines.CoroutineContext

@extension
@undocumented
interface StateTBracket<S, F> : Bracket<StateTPartialOf<S, F>, Throwable>, StateTMonadThrow<S, F> {

  fun MD(): MonadDefer<F>

  override fun ME(): MonadError<F, Throwable> = MD()

  override fun <A, B> StateTOf<S, F, A>.bracketCase(
    release: (A, ExitCase<Throwable>) -> StateTOf<S, F, Unit>,
    use: (A) -> StateTOf<S, F, B>
  ): StateT<S, F, B> = MD().run {

    StateT.liftF<S, F, Ref<F, Option<S>>>(this, Ref(this, None)).flatMap { ref ->
      StateT<S, F, B>(this) { startS ->
        runM(this, startS).bracketCase(use = { (s, a) ->
          use(a).runM(this, s).flatMap { sa ->
            ref.set(Some(sa.a)).map { sa }
          }
        }, release = { (s0, a), exitCase ->
          when (exitCase) {
            is ExitCase.Completed ->
              ref.get().map { it.getOrElse { s0 } }.flatMap { s1 ->
                release(a, ExitCase.Completed).fix().runS(this, s1).flatMap { s2 ->
                  ref.set(Some(s2))
                }
              }
            else -> release(a, exitCase).runM(this, s0).unit()
          }
        }).flatMap { (s, b) -> ref.get().map { it.getOrElse { s } }.tupleRight(b) }
      }
    }
  }
}

@extension
@undocumented
interface StateTMonadDefer<S, F> : MonadDefer<StateTPartialOf<S, F>>, StateTBracket<S, F> {

  override fun MD(): MonadDefer<F>

  override fun <A> defer(fa: () -> StateTOf<S, F, A>): StateT<S, F, A> = MD().run {
    StateT(this) { s -> defer { fa().runM(this, s) } }
  }
}

@extension
@undocumented
interface StateTAsyncInstane<S, F> : Async<StateTPartialOf<S, F>>, StateTMonadDefer<S, F> {

  fun AS(): Async<F>

  override fun MD(): MonadDefer<F> = AS()

  override fun <A> async(fa: Proc<A>): StateT<S, F, A> = AS().run {
    StateT.liftF(this, async(fa))
  }

  override fun <A> asyncF(k: ProcF<StateTPartialOf<S, F>, A>): StateT<S, F, A> = AS().run {
    StateT.invoke(this) { s ->
      asyncF<A> { cb -> k(cb).fix().runA(this, s) }
        .map { Tuple2(s, it) }
    }
  }

  override fun <A> StateTOf<S, F, A>.continueOn(ctx: CoroutineContext): StateT<S, F, A> = AS().run {
    StateT(this) { s -> runM(this, s).continueOn(ctx) }
  }
}
