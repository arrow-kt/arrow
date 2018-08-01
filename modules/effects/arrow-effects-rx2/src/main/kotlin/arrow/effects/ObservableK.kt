package arrow.effects

import arrow.Kind
import arrow.core.*
import arrow.effects.CoroutineContextRx2Scheduler.asScheduler
import arrow.effects.typeclasses.Proc
import arrow.higherkind
import arrow.typeclasses.Applicative
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import kotlin.coroutines.experimental.CoroutineContext

fun <A> Observable<A>.k(): ObservableK<A> = ObservableK(this)

fun <A> ObservableKOf<A>.value(): Observable<A> =
  this.fix().observable

@higherkind
data class ObservableK<A>(val observable: Observable<A>) : ObservableKOf<A>, ObservableKKindedJ<A> {
  fun <B> map(f: (A) -> B): ObservableK<B> =
    observable.map(f).k()

  fun <B> ap(fa: ObservableKOf<(A) -> B>): ObservableK<B> =
    flatMap { a -> fa.fix().map { ff -> ff(a) } }

  fun <B> flatMap(f: (A) -> ObservableKOf<B>): ObservableK<B> =
    observable.flatMap { f(it).fix().observable }.k()

  fun <B> concatMap(f: (A) -> ObservableKOf<B>): ObservableK<B> =
    observable.concatMap { f(it).fix().observable }.k()

  fun <B> switchMap(f: (A) -> ObservableKOf<B>): ObservableK<B> =
    observable.switchMap { f(it).fix().observable }.k()

  fun <B> foldLeft(b: B, f: (B, A) -> B): B = observable.reduce(b, f).blockingGet()

  fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
    fun loop(fa_p: ObservableK<A>): Eval<B> = when {
      fa_p.observable.isEmpty.blockingGet() -> lb
      else -> f(fa_p.observable.blockingFirst(), Eval.defer { loop(fa_p.observable.skip(1).k()) })
    }

    return Eval.defer { loop(this) }
  }

  fun <G, B> traverse(GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, ObservableK<B>> =
    foldRight(Eval.always { GA.just(Observable.empty<B>().k()) }) { a, eval ->
      GA.run { f(a).map2Eval(eval) { Observable.concat(Observable.just<B>(it.a), it.b.observable).k() } }
    }.value()

  fun handleErrorWith(function: (Throwable) -> ObservableK<A>): ObservableK<A> =
    this.fix().observable.onErrorResumeNext { t: Throwable -> function(t).observable }.k()

  fun continueOn(ctx: CoroutineContext): ObservableK<A> =
    observable.observeOn(ctx.asScheduler()).k()

  fun runAsync(cb: (Either<Throwable, A>) -> ObservableKOf<Unit>): ObservableK<Unit> =
    observable.flatMap { cb(Right(it)).value() }.onErrorResumeNext(io.reactivex.functions.Function { cb(Left(it)).value() }).k()

  companion object {
    fun <A> just(a: A): ObservableK<A> =
      Observable.just(a).k()

    fun <A> raiseError(t: Throwable): ObservableK<A> =
      Observable.error<A>(t).k()

    operator fun <A> invoke(fa: () -> A): ObservableK<A> =
      defer { just(fa()) }

    fun <A> defer(fa: () -> ObservableKOf<A>): ObservableK<A> =
      Observable.defer { fa().value() }.k()

    fun <A> runAsync(fa: Proc<A>): ObservableK<A> =
      Observable.create { emitter: ObservableEmitter<A> ->
        fa { either: Either<Throwable, A> ->
          either.fold({
            emitter.onError(it)
          }, {
            emitter.onNext(it)
            emitter.onComplete()
          })
        }
      }.k()

    tailrec fun <A, B> tailRecM(a: A, f: (A) -> ObservableKOf<Either<A, B>>): ObservableK<B> {
      val either = f(a).fix().value().blockingFirst()
      return when (either) {
        is Either.Left -> tailRecM(either.a, f)
        is Either.Right -> Observable.just(either.b).k()
      }
    }

    fun monadFlat(): ObservableKMonadInstance = monad()

    fun monadConcat(): ObservableKMonadInstance = object : ObservableKMonadInstance {
      override fun <A, B> Kind<ForObservableK, A>.flatMap(f: (A) -> Kind<ForObservableK, B>): ObservableK<B> =
        fix().concatMap { f(it).fix() }
    }

    fun monadSwitch(): ObservableKMonadInstance = object : ObservableKMonadErrorInstance {
      override fun <A, B> Kind<ForObservableK, A>.flatMap(f: (A) -> Kind<ForObservableK, B>): ObservableK<B> =
        fix().switchMap { f(it).fix() }
    }

    fun monadErrorFlat(): ObservableKMonadErrorInstance = monadError()

    fun monadErrorConcat(): ObservableKMonadErrorInstance = object : ObservableKMonadErrorInstance {
      override fun <A, B> Kind<ForObservableK, A>.flatMap(f: (A) -> Kind<ForObservableK, B>): ObservableK<B> =
        fix().concatMap { f(it).fix() }
    }

    fun monadErrorSwitch(): ObservableKMonadErrorInstance = object : ObservableKMonadErrorInstance {
      override fun <A, B> Kind<ForObservableK, A>.flatMap(f: (A) -> Kind<ForObservableK, B>): ObservableK<B> =
        fix().switchMap { f(it).fix() }
    }
  }
}

inline fun <A, G> ObservableKOf<Kind<G, A>>.sequence(GA: Applicative<G>): Kind<G, ObservableK<A>> =
  fix().traverse(GA, ::identity)
