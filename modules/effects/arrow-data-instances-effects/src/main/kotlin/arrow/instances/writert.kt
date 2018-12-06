package arrow.instances

import arrow.Kind
import arrow.core.Tuple2
import arrow.data.WriterT
import arrow.data.WriterTOf
import arrow.data.WriterTPartialOf
import arrow.data.value
import arrow.effects.Ref
import arrow.effects.typeclasses.*
import arrow.extension
import arrow.typeclasses.MonadError
import arrow.typeclasses.Monoid
import kotlin.coroutines.CoroutineContext

@extension
interface WriterTBrackInstance<F, W> : Bracket<WriterTPartialOf<F, W>, Throwable>, WriterTMonadThrow<F, W> {

  fun MD(): MonadDefer<F>

  override fun MM(): Monoid<W>

  override fun ME(): MonadError<F, Throwable> = MD()

  override fun <A, B> WriterTOf<F, W, A>.bracketCase(
    release: (A, ExitCase<Throwable>) -> WriterTOf<F, W, Unit>,
    use: (A) -> WriterTOf<F, W, B>): WriterT<F, W, B> = MM().run {
    MD().run {
      WriterT(Ref.of(empty(), this).flatMap { ref ->
        value().bracketCase(use = { wa ->
          WriterT(wa.just()).flatMap(use).value
        }, release = { wa, exitCase ->
          val r = release(wa.b, exitCase).value()
          when (exitCase) {
            is ExitCase.Completed -> r.flatMap { (l, _) -> ref.set(l) }
            else -> r.void()
          }
        }).flatMap { (w, b) ->
          ref.get().map { ww -> Tuple2(w.combine(ww), b) }
        }
      })
    }
  }

}

@extension
interface WriterTMonadDeferInstance<F, W> : MonadDefer<WriterTPartialOf<F, W>>, WriterTBrackInstance<F, W> {

  override fun MD(): MonadDefer<F>

  override fun MM(): Monoid<W>

  override fun <A> defer(fa: () -> Kind<WriterTPartialOf<F, W>, A>): Kind<WriterTPartialOf<F, W>, A> =
    WriterT(MD().defer { fa().value() })

}

@extension
interface WriterTAsyncInstance<F, W> : Async<WriterTPartialOf<F, W>>, WriterTMonadDeferInstance<F, W> {

  fun AS(): Async<F>

  override fun MM(): Monoid<W>

  override fun MD(): MonadDefer<F> = AS()

  override fun <A> async(fa: Proc<A>): WriterT<F, W, A> = AS().run {
    WriterT.liftF(async(fa), MM(), this)
  }

  override fun <A> asyncF(k: ProcF<WriterTPartialOf<F, W>, A>): WriterT<F, W, A> = AS().run {
    WriterT.liftF(asyncF { cb -> k(cb).value().void() }, MM(), this)
  }

  override fun <A> WriterTOf<F, W, A>.continueOn(ctx: CoroutineContext): WriterT<F, W, A> = AS().run {
    WriterT(value().continueOn(ctx))
  }

}
