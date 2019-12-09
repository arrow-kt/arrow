package arrow.fx.mtl

import arrow.Kind
import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.compose
import arrow.fx.Ref
import arrow.mtl.WriterT
import arrow.mtl.WriterTOf
import arrow.mtl.WriterTPartialOf
import arrow.mtl.extensions.WriterTMonadThrow
import arrow.mtl.value
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
import arrow.typeclasses.MonadError
import arrow.typeclasses.Monoid
import arrow.undocumented
import kotlin.coroutines.CoroutineContext

@extension
@undocumented
interface WriterTBracket<F, W> : Bracket<WriterTPartialOf<F, W>, Throwable>, WriterTMonadThrow<F, W> {

  fun MD(): MonadDefer<F>

  override fun MM(): Monoid<W>

  override fun ME(): MonadError<F, Throwable> = MD()

  override fun <A, B> WriterTOf<F, W, A>.bracketCase(
    release: (A, ExitCase<Throwable>) -> WriterTOf<F, W, Unit>,
    use: (A) -> WriterTOf<F, W, B>
  ): WriterT<F, W, B> = MM().run {
    MD().run {
      WriterT(Ref(this, empty()).flatMap { ref ->
        value().bracketCase(use = { wa ->
          WriterT(wa.just()).flatMap(use).value()
        }, release = { wa, exitCase ->
          val r = release(wa.b, exitCase).value()
          when (exitCase) {
            is ExitCase.Completed -> r.flatMap { (l, _) -> ref.set(l) }
            else -> r.unit()
          }
        }).flatMap { (w, b) ->
          ref.get().map { ww -> Tuple2(w.combine(ww), b) }
        }
      })
    }
  }
}

@extension
@undocumented
interface WriterTMonadDefer<F, W> : MonadDefer<WriterTPartialOf<F, W>>, WriterTBracket<F, W> {

  override fun MD(): MonadDefer<F>

  override fun MM(): Monoid<W>

  override fun <A> defer(fa: () -> Kind<WriterTPartialOf<F, W>, A>): Kind<WriterTPartialOf<F, W>, A> =
    WriterT(MD().defer { fa().value() })
}

@extension
@undocumented
interface WriterTAsync<F, W> : Async<WriterTPartialOf<F, W>>, WriterTMonadDefer<F, W> {

  fun AS(): Async<F>

  override fun MM(): Monoid<W>

  override fun MD(): MonadDefer<F> = AS()

  override fun <A> async(fa: Proc<A>): WriterT<F, W, A> = AS().run {
    WriterT.liftF(async(fa), MM(), this)
  }

  override fun <A> asyncF(k: ProcF<WriterTPartialOf<F, W>, A>): Kind<WriterTPartialOf<F, W>, A> = AS().run {
    WriterT.liftF(asyncF { cb -> k(cb).value().unit() }, MM(), this)
  }

  override fun <A> WriterTOf<F, W, A>.continueOn(ctx: CoroutineContext): WriterT<F, W, A> = AS().run {
    WriterT(value().continueOn(ctx))
  }
}

@extension
@undocumented
interface WriterTEffect<F, W> : Effect<WriterTPartialOf<F, W>>, WriterTAsync<F, W> {

  fun EFF(): Effect<F>

  override fun MM(): Monoid<W>

  override fun AS(): Async<F> = EFF()

  override fun <A> WriterTOf<F, W, A>.runAsync(cb: (Either<Throwable, A>) -> WriterTOf<F, W, Unit>): WriterT<F, W, Unit> = EFF().run {
    WriterT.liftF(value().runAsync { r ->
      val f = cb.compose { a: Either<Throwable, Tuple2<W, A>> -> a.map(Tuple2<W, A>::b) }
      f(r).value().unit()
    }, MM(), this)
  }
}

@extension
@undocumented
interface WriterTConcurrentEffect<F, W> : ConcurrentEffect<WriterTPartialOf<F, W>>, WriterTEffect<F, W> {

  fun CEFF(): ConcurrentEffect<F>

  override fun MM(): Monoid<W>

  override fun EFF(): Effect<F> = CEFF()

  override fun <A> WriterTOf<F, W, A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> WriterTOf<F, W, Unit>): WriterT<F, W, Disposable> = CEFF().run {
    WriterT.liftF(value().runAsyncCancellable { r: Either<Throwable, Tuple2<W, A>> ->
      val f = cb.compose { rr: Either<Throwable, Tuple2<W, A>> -> rr.map(Tuple2<W, A>::b) }
      f(r).value().unit()
    }, MM(), this)
  }
}
