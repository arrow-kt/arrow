package arrow.effects

import arrow.Kind
import arrow.core.*
import arrow.effects.CoroutineContextRx2Scheduler.asScheduler
import arrow.effects.typeclasses.Proc
import arrow.higherkind
import arrow.typeclasses.Applicative
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import kotlin.coroutines.CoroutineContext

fun <A> Flowable<A>.k(): FlowableK<A> = FlowableK(this)

fun <A> FlowableKOf<A>.value(): Flowable<A> = this.fix().flowable

@higherkind
data class FlowableK<A>(val flowable: Flowable<A>) : FlowableKOf<A>, FlowableKKindedJ<A> {

  fun <B> map(f: (A) -> B): FlowableK<B> =
    flowable.map(f).k()

  fun <B> ap(fa: FlowableKOf<(A) -> B>): FlowableK<B> =
    flatMap { a -> fa.fix().map { ff -> ff(a) } }

  fun <B> flatMap(f: (A) -> FlowableKOf<B>): FlowableK<B> =
    flowable.flatMap { f(it).fix().flowable }.k()

  fun <B> concatMap(f: (A) -> FlowableKOf<B>): FlowableK<B> =
    flowable.concatMap { f(it).fix().flowable }.k()

  fun <B> switchMap(f: (A) -> FlowableKOf<B>): FlowableK<B> =
    flowable.switchMap { f(it).fix().flowable }.k()

  fun <B> foldLeft(b: B, f: (B, A) -> B): B = flowable.reduce(b, f).blockingGet()

  fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
    fun loop(fa_p: FlowableK<A>): Eval<B> = when {
      fa_p.flowable.isEmpty.blockingGet() -> lb
      else -> f(fa_p.flowable.blockingFirst(), Eval.defer { loop(fa_p.flowable.skip(1).k()) })
    }

    return Eval.defer { loop(this) }
  }

  fun <G, B> traverse(GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, FlowableK<B>> =
    foldRight(Eval.always { GA.just(Flowable.empty<B>().k()) }) { a, eval ->
      GA.run { f(a).map2Eval(eval) { Flowable.concat(Flowable.just<B>(it.a), it.b.flowable).k() } }
    }.value()

  fun handleErrorWith(function: (Throwable) -> FlowableK<A>): FlowableK<A> =
    flowable.onErrorResumeNext { t: Throwable -> function(t).flowable }.k()

  fun continueOn(ctx: CoroutineContext): FlowableK<A> =
    flowable.observeOn(ctx.asScheduler()).k()

  fun runAsync(cb: (Either<Throwable, A>) -> FlowableKOf<Unit>): FlowableK<Unit> =
    flowable.flatMap { cb(Right(it)).value() }.onErrorResumeNext(io.reactivex.functions.Function { cb(Left(it)).value() }).k()

  companion object {
    fun <A> just(a: A): FlowableK<A> =
      Flowable.just(a).k()

    fun <A> raiseError(t: Throwable): FlowableK<A> =
      Flowable.error<A>(t).k()

    operator fun <A> invoke(fa: () -> A): FlowableK<A> =
      defer { just(fa()) }

    fun <A> defer(fa: () -> FlowableKOf<A>): FlowableK<A> =
      Flowable.defer { fa().value() }.k()

    fun <A> async(fa: Proc<A>, mode: BackpressureStrategy = BackpressureStrategy.BUFFER): FlowableK<A> =
      Flowable.create({ emitter: FlowableEmitter<A> ->
        fa { either: Either<Throwable, A> ->
          either.fold({
            emitter.onError(it)
          }, {
            emitter.onNext(it)
            emitter.onComplete()
          })

        }
      }, mode).k()

    tailrec fun <A, B> tailRecM(a: A, f: (A) -> FlowableKOf<Either<A, B>>): FlowableK<B> {
      val either = f(a).fix().value().blockingFirst()
      return when (either) {
        is Either.Left -> tailRecM(either.a, f)
        is Either.Right -> Flowable.just(either.b).k()
      }
    }

    fun monadFlat(): FlowableKMonadInstance = monad()

    fun monadConcat(): FlowableKMonadInstance = object : FlowableKMonadInstance {
      override fun <A, B> Kind<ForFlowableK, A>.flatMap(f: (A) -> Kind<ForFlowableK, B>): FlowableK<B> =
        fix().concatMap { f(it).fix() }
    }

    fun monadSwitch(): FlowableKMonadInstance = object : FlowableKMonadInstance {
      override fun <A, B> Kind<ForFlowableK, A>.flatMap(f: (A) -> Kind<ForFlowableK, B>): FlowableK<B> =
        fix().switchMap { f(it).fix() }
    }

    fun monadErrorFlat(): FlowableKMonadErrorInstance = monadError()

    fun monadErrorConcat(): FlowableKMonadErrorInstance = object : FlowableKMonadErrorInstance {
      override fun <A, B> Kind<ForFlowableK, A>.flatMap(f: (A) -> Kind<ForFlowableK, B>): FlowableK<B> =
        fix().concatMap { f(it).fix() }
    }

    fun monadErrorSwitch(): FlowableKMonadErrorInstance = object : FlowableKMonadErrorInstance {
      override fun <A, B> Kind<ForFlowableK, A>.flatMap(f: (A) -> Kind<ForFlowableK, B>): FlowableK<B> =
        fix().switchMap { f(it).fix() }
    }

    fun monadSuspendBuffer(): FlowableKMonadDeferInstance = monadDefer()

    fun monadSuspendDrop(): FlowableKMonadDeferInstance = object : FlowableKMonadDeferInstance {
      override fun BS(): BackpressureStrategy = BackpressureStrategy.DROP
    }

    fun monadSuspendError(): FlowableKMonadDeferInstance = object : FlowableKMonadDeferInstance {
      override fun BS(): BackpressureStrategy = BackpressureStrategy.ERROR
    }

    fun monadSuspendLatest(): FlowableKMonadDeferInstance = object : FlowableKMonadDeferInstance {
      override fun BS(): BackpressureStrategy = BackpressureStrategy.LATEST
    }

    fun monadSuspendMissing(): FlowableKMonadDeferInstance = object : FlowableKMonadDeferInstance {
      override fun BS(): BackpressureStrategy = BackpressureStrategy.MISSING
    }

    fun asyncBuffer(): FlowableKAsyncInstance = async()

    fun asyncDrop(): FlowableKAsyncInstance = object : FlowableKAsyncInstance {
      override fun BS(): BackpressureStrategy = BackpressureStrategy.DROP
    }

    fun asyncError(): FlowableKAsyncInstance = object : FlowableKAsyncInstance {
      override fun BS(): BackpressureStrategy = BackpressureStrategy.ERROR
    }

    fun asyncLatest(): FlowableKAsyncInstance = object : FlowableKAsyncInstance {
      override fun BS(): BackpressureStrategy = BackpressureStrategy.LATEST
    }

    fun asyncMissing(): FlowableKAsyncInstance = object : FlowableKAsyncInstance {
      override fun BS(): BackpressureStrategy = BackpressureStrategy.MISSING
    }

    fun effectBuffer(): FlowableKEffectInstance = effect()

    fun effectDrop(): FlowableKEffectInstance = object : FlowableKEffectInstance {
      override fun BS(): BackpressureStrategy = BackpressureStrategy.DROP
    }

    fun effectError(): FlowableKEffectInstance = object : FlowableKEffectInstance {
      override fun BS(): BackpressureStrategy = BackpressureStrategy.ERROR
    }

    fun effectLatest(): FlowableKEffectInstance = object : FlowableKEffectInstance {
      override fun BS(): BackpressureStrategy = BackpressureStrategy.LATEST
    }

    fun effectMissing(): FlowableKEffectInstance = object : FlowableKEffectInstance {
      override fun BS(): BackpressureStrategy = BackpressureStrategy.MISSING
    }
  }
}

inline fun <A, G> FlowableKOf<Kind<G, A>>.sequence(GA: Applicative<G>): Kind<G, FlowableK<A>> =
  fix().traverse(GA, ::identity)
