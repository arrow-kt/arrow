package arrow.effects.instances

import arrow.Kind
import arrow.core.*
import arrow.data.*
import arrow.effects.Ref
import arrow.effects.typeclasses.*
import arrow.extension
import arrow.instances.WriterTMonadThrow
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
          WriterT(wa.just()).flatMap(use).value()
        }, release = { wa, exitCase ->
          val r = release(wa.b, exitCase).value()
          when (exitCase) {
            is ExitCase.Completed -> r.flatMap { (l, _) -> ref.set(l) }
            else -> r.void()
          }
        }).flatMap { (w, b) ->
          ref.get.map { ww -> Tuple2(w.combine(ww), b) }
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

  override fun <A> WriterTOf<F, W, A>.continueOn(ctx: CoroutineContext): WriterT<F, W, A> = AS().run {
    WriterT(value().continueOn(ctx))
  }

}

@extension
interface WriterTEffectInstance<F, W> : Effect<WriterTPartialOf<F, W>>, WriterTAsyncInstance<F, W> {

  fun EFF(): Effect<F>

  override fun MM(): Monoid<W>

  override fun AS(): Async<F> = EFF()

  override fun <A> WriterTOf<F, W, A>.runAsync(cb: (Either<Throwable, A>) -> WriterTOf<F, W, Unit>): WriterT<F, W, Unit> = EFF().run {
    WriterT.liftF(value().runAsync { r ->
      val f = cb.compose { a: Either<Throwable, Tuple2<W, A>> -> a.map(Tuple2<W, A>::b) }
      f(r).value().void()
    }, MM(), this)
  }

}

@extension
interface WriterTConcurrentEffectInstance<F, W> : ConcurrentEffect<WriterTPartialOf<F, W>>, WriterTEffectInstance<F, W> {

  fun CEFF(): ConcurrentEffect<F>

  override fun MM(): Monoid<W>

  override fun EFF(): Effect<F> = CEFF()

  override fun <A> WriterTOf<F, W, A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> WriterTOf<F, W, Unit>): WriterT<F, W, Disposable> = CEFF().run {
    WriterT.liftF(value().runAsyncCancellable { r: Either<Throwable, Tuple2<W, A>> ->
      val f = cb.compose { rr: Either<Throwable, Tuple2<W, A>> -> rr.map(Tuple2<W, A>::b) }
      f(r).value().void()
    }, MM(), this)
  }

}
