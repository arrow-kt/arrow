package arrow.effects

import arrow.core.*
import arrow.effects.typeclasses.Fiber
import kotlin.coroutines.CoroutineContext
import arrow.effects.CoroutineContextRx2Scheduler.asScheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

fun <A, B> ObservableK.Companion.racePair(ctx: CoroutineContext, fa: ObservableKOf<A>, fb: ObservableKOf<B>): ObservableK<Either<Tuple2<A, Fiber<ForObservableK, B>>, Tuple2<Fiber<ForObservableK, A>, B>>> {
  val subject = BehaviorSubject.create<Either<Tuple2<A, Fiber<ForObservableK, B>>, Tuple2<Fiber<ForObservableK, A>, B>>>()
  val active = AtomicBoolean(true)
  val state = AtomicReference(Tuple2(emptyList<BehaviorSubject<A>>(), emptyList<BehaviorSubject<B>>()))
  val disposable = CompositeDisposable()

  disposable.add(fa.value()
    .subscribeOn(ctx.asScheduler())
    .subscribe({ a ->
      val oldState = state.getAndUpdate { pair ->
        if (pair.a.isEmpty()) pair.copy(b = pair.b + BehaviorSubject.create<B>())
        else pair.copy(a = pair.a.drop(1))
      }
      val promiseA = oldState.a.firstOrNull()
      if (promiseA != null) {
        promiseA.onNext(a)
        promiseA.onComplete()
      } else {
        subject.onNext(Left(Tuple2(a, oldState.b.first().toFiber(disposable))))
      }
    }, { e ->
      if (active.getAndSet(false)) { //If first error than cancel stream
        disposable.dispose()
        subject.onError(e)
      } else {
        //If stream is not active anymore but fa still errors propagate as promised.
        state.get().a.firstOrNull()?.onError(e)
      }
    }, subject::onComplete))

  disposable.add(fb.value()
    .subscribeOn(ctx.asScheduler())
    .subscribe({ b ->
      val oldState = state.updateAndGet { pair ->
        if (pair.b.isEmpty()) pair.copy(a = pair.a + BehaviorSubject.create<A>())
        else pair.copy(b = pair.b.drop(1))
      }
      val promiseB = oldState.b.firstOrNull()
      if (promiseB != null) {
        promiseB.onNext(b)
        promiseB.onComplete()
      } else {
        subject.onNext(Right(Tuple2(oldState.a.first().toFiber(disposable), b)))
      }
    }, { e ->
      if (active.getAndSet(false)) { //If first error than cancel stream
        disposable.dispose()
        subject.onError(e)
      } else {
        //If stream is not active anymore but fa still errors propagate as promised.
        state.get().b.firstOrNull()?.onError(e)
      }
    }, subject::onComplete))

  return subject.k()
}

internal fun <A> BehaviorSubject<A>.toFiber(d: Disposable): Fiber<ForObservableK, A> =
  Fiber(k(), ObservableK { onError(ConnectionCancellationException()); d.dispose() })