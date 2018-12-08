package arrow.instances

import arrow.Kind
import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.compose
import arrow.data.*
import arrow.effects.Ref
import arrow.effects.typeclasses.*
import arrow.typeclasses.MonadError
import arrow.typeclasses.Monoid
import kotlin.coroutines.CoroutineContext

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

fun <F, W> WriterT.Companion.bracket(MD: MonadDefer<F>, MM: Monoid<W>): Bracket<WriterTPartialOf<F, W>, Throwable> = object : WriterTBrackInstance<F, W> {
  override fun MD(): MonadDefer<F> = MD
  override fun MM(): Monoid<W> = MM
}

interface WriterTMonadDeferInstance<F, W> : MonadDefer<WriterTPartialOf<F, W>>, WriterTBrackInstance<F, W> {

  override fun MD(): MonadDefer<F>

  override fun MM(): Monoid<W>

  override fun <A> defer(fa: () -> Kind<WriterTPartialOf<F, W>, A>): Kind<WriterTPartialOf<F, W>, A> =
    WriterT(MD().defer { fa().value() })

}

fun <F, W> WriterT.Companion.monadDefer(MD: MonadDefer<F>, MM: Monoid<W>): MonadDefer<WriterTPartialOf<F, W>> = object : WriterTMonadDeferInstance<F, W> {
  override fun MD(): MonadDefer<F> = MD

  override fun MM(): Monoid<W> = MM
}

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

fun <F, W> WriterT.Companion.async(AS: Async<F>, MM: Monoid<W>): Async<WriterTPartialOf<F, W>> = object : WriterTAsyncInstance<F, W> {
  override fun AS(): Async<F> = AS
  override fun MM(): Monoid<W> = MM
}

interface WriterTEffectInstance<F, W> : Effect<WriterTPartialOf<F, W>>, WriterTAsyncInstance<F, W> {

  fun EFF(): Effect<F>

  override fun MM(): Monoid<W>

  override fun AS(): Async<F> = EFF()

  override fun <A> WriterTOf<F, W, A>.runAsync(cb: (Either<Throwable, A>) -> WriterTOf<F, W, Unit>): WriterT<F, W, Unit> = EFF().run {
    WriterT.liftF(value().runAsync { either ->
      val xx = cb.compose { a: Either<Throwable, Tuple2<W, A>> -> a.map { (_, a) -> a } }
      xx(either).value().void()
    }, MM(), this)
  }

}

fun <F, W> WriterT.Companion.effect(EFF: Effect<F>, MM: Monoid<W>): Effect<WriterTPartialOf<F, W>> = object : WriterTEffectInstance<F, W> {
  override fun EFF(): Effect<F> = EFF
  override fun MM(): Monoid<W> = MM
}

interface WriterTConcurrentEffectInstance<F, W> : ConcurrentEffect<WriterTPartialOf<F, W>>, WriterTEffectInstance<F, W> {

  fun CEFF(): ConcurrentEffect<F>

  override fun MM(): Monoid<W>

  override fun EFF(): Effect<F> = CEFF()

  override fun <A> WriterTOf<F, W, A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> WriterTOf<F, W, Unit>): WriterT<F, W, Disposable> = CEFF().run {
    WriterT.liftF(value().runAsyncCancellable { r: Either<Throwable, Tuple2<W, A>> ->
      val x = cb.compose { rr: Either<Throwable, Tuple2<W, A>> -> rr.map { (_, a) -> a } }
      x(r).value().void()
    }, MM(), this)
  }

}

fun <F, W> WriterT.Companion.concurrentEffect(CEFF: ConcurrentEffect<F>, MM: Monoid<W>): ConcurrentEffect<WriterTPartialOf<F, W>> = object : WriterTConcurrentEffectInstance<F, W> {
  override fun CEFF(): ConcurrentEffect<F> = CEFF
  override fun MM(): Monoid<W> = MM
}
