package arrow.effects

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Left
import arrow.core.Right
import arrow.effects.typeclasses.Proc
import arrow.higherkind
import arrow.typeclasses.Applicative
import io.reactivex.Observable
import io.reactivex.ObservableEmitter

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

    fun <G, B> traverse(f: (A) -> Kind<G, B>, GA: Applicative<G>): Kind<G, ObservableK<B>> =
            foldRight(Eval.always { GA.pure(Observable.empty<B>().k()) }) { a, eval ->
                GA.map2Eval(f(a), eval) { Observable.concat(Observable.just<B>(it.a), it.b.observable).k() }
            }.value()

    fun runAsync(cb: (Either<Throwable, A>) -> ObservableKOf<Unit>): ObservableK<Unit> =
            observable.flatMap { cb(Right(it)).value() }.onErrorResumeNext(io.reactivex.functions.Function { cb(Left(it)).value() }).k()

    companion object {
        fun <A> pure(a: A): ObservableK<A> =
                Observable.just(a).k()

        fun <A> raiseError(t: Throwable): ObservableK<A> =
                Observable.error<A>(t).k()

        operator fun <A> invoke(fa: () -> A): ObservableK<A> =
                suspend { pure(fa()) }

        fun <A> suspend(fa: () -> ObservableKOf<A>): ObservableK<A> =
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
            override fun <A, B> flatMap(fa: ObservableKOf<A>, f: (A) -> ObservableKOf<B>): ObservableK<B> =
                    fa.fix().concatMap { f(it).fix() }
        }

        fun monadSwitch(): ObservableKMonadInstance = object : ObservableKMonadErrorInstance {
            override fun <A, B> flatMap(fa: ObservableKOf<A>, f: (A) -> ObservableKOf<B>): ObservableK<B> =
                    fa.fix().switchMap { f(it).fix() }
        }

        fun monadErrorFlat(): ObservableKMonadErrorInstance = monadError()

        fun monadErrorConcat(): ObservableKMonadErrorInstance = object : ObservableKMonadErrorInstance {
            override fun <A, B> flatMap(fa: ObservableKOf<A>, f: (A) -> ObservableKOf<B>): ObservableK<B> =
                    fa.fix().concatMap { f(it).fix() }
        }

        fun monadErrorSwitch(): ObservableKMonadErrorInstance = object : ObservableKMonadErrorInstance {
            override fun <A, B> flatMap(fa: ObservableKOf<A>, f: (A) -> ObservableKOf<B>): ObservableK<B> =
                    fa.fix().switchMap { f(it).fix() }
        }
    }
}

fun <A> ObservableKOf<A>.handleErrorWith(function: (Throwable) -> ObservableK<A>): ObservableK<A> =
        this.fix().observable.onErrorResumeNext { t: Throwable -> function(t).observable }.k()