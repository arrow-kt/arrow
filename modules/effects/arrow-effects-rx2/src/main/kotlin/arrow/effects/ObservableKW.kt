package arrow.effects

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Left
import arrow.core.Right
import arrow.deriving
import arrow.higherkind
import arrow.typeclasses.*
import io.reactivex.Observable
import io.reactivex.ObservableEmitter

fun <A> Observable<A>.k(): ObservableKW<A> = ObservableKW(this)

fun <A> ObservableKWKind<A>.value(): Observable<A> =
        this.reify().observable

@higherkind
@deriving(
        Functor::class,
        Applicative::class,
        Monad::class,
        Foldable::class,
        Traverse::class
)
data class ObservableKW<A>(val observable: Observable<A>) : ObservableKWKind<A>, ObservableKWKindedJ<A> {
    fun <B> map(f: (A) -> B): ObservableKW<B> =
            observable.map(f).k()

    fun <B> ap(fa: ObservableKWKind<(A) -> B>): ObservableKW<B> =
            flatMap { a -> fa.reify().map { ff -> ff(a) } }

    fun <B> flatMap(f: (A) -> ObservableKWKind<B>): ObservableKW<B> =
            observable.flatMap { f(it).reify().observable }.k()

    fun <B> concatMap(f: (A) -> ObservableKWKind<B>): ObservableKW<B> =
            observable.concatMap { f(it).reify().observable }.k()

    fun <B> switchMap(f: (A) -> ObservableKWKind<B>): ObservableKW<B> =
            observable.switchMap { f(it).reify().observable }.k()

    fun <B> foldLeft(b: B, f: (B, A) -> B): B = observable.reduce(b, f).blockingGet()

    fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
        fun loop(fa_p: ObservableKW<A>): Eval<B> = when {
            fa_p.observable.isEmpty.blockingGet() -> lb
            else -> f(fa_p.observable.blockingFirst(), Eval.defer { loop(fa_p.observable.skip(1).k()) })
        }

        return Eval.defer { loop(this) }
    }

    fun <G, B> traverse(f: (A) -> Kind<G, B>, GA: Applicative<G>): Kind<G, ObservableKW<B>> =
            foldRight(Eval.always { GA.pure(Observable.empty<B>().k()) }) { a, eval ->
                GA.map2Eval(f(a), eval) { Observable.concat(Observable.just<B>(it.a), it.b.observable).k() }
            }.value()

    fun runAsync(cb: (Either<Throwable, A>) -> ObservableKWKind<Unit>): ObservableKW<Unit> =
            observable.flatMap { cb(Right(it)).value() }.onErrorResumeNext(io.reactivex.functions.Function { cb(Left(it)).value() }).k()

    companion object {
        fun <A> pure(a: A): ObservableKW<A> =
                Observable.just(a).k()

        fun <A> raiseError(t: Throwable): ObservableKW<A> =
                Observable.error<A>(t).k()

        operator fun <A> invoke(fa: () -> A): ObservableKW<A> =
                suspend { pure(fa()) }

        fun <A> suspend(fa: () -> ObservableKWKind<A>): ObservableKW<A> =
                Observable.defer { fa().value() }.k()

        fun <A> runAsync(fa: Proc<A>): ObservableKW<A> =
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

        tailrec fun <A, B> tailRecM(a: A, f: (A) -> ObservableKWKind<Either<A, B>>): ObservableKW<B> {
            val either = f(a).reify().value().blockingFirst()
            return when (either) {
                is Either.Left -> tailRecM(either.a, f)
                is Either.Right -> Observable.just(either.b).k()
            }
        }

        fun monadFlat(): ObservableKWMonadInstance = ObservableKWMonadInstanceImplicits.instance()

        fun monadConcat(): ObservableKWMonadInstance = object : ObservableKWMonadInstance {
            override fun <A, B> flatMap(fa: ObservableKWKind<A>, f: (A) -> ObservableKWKind<B>): ObservableKW<B> =
                    fa.reify().concatMap { f(it).reify() }
        }

        fun monadSwitch(): ObservableKWMonadInstance = object : ObservableKWMonadErrorInstance {
            override fun <A, B> flatMap(fa: ObservableKWKind<A>, f: (A) -> ObservableKWKind<B>): ObservableKW<B> =
                    fa.reify().switchMap { f(it).reify() }
        }

        fun monadErrorFlat(): ObservableKWMonadErrorInstance = ObservableKWMonadErrorInstanceImplicits.instance()

        fun monadErrorConcat(): ObservableKWMonadErrorInstance = object : ObservableKWMonadErrorInstance {
            override fun <A, B> flatMap(fa: ObservableKWKind<A>, f: (A) -> ObservableKWKind<B>): ObservableKW<B> =
                    fa.reify().concatMap { f(it).reify() }
        }

        fun monadErrorSwitch(): ObservableKWMonadErrorInstance = object : ObservableKWMonadErrorInstance {
            override fun <A, B> flatMap(fa: ObservableKWKind<A>, f: (A) -> ObservableKWKind<B>): ObservableKW<B> =
                    fa.reify().switchMap { f(it).reify() }
        }
    }
}

fun <A> ObservableKWKind<A>.handleErrorWith(function: (Throwable) -> ObservableKW<A>): ObservableKW<A> =
        this.reify().observable.onErrorResumeNext { t: Throwable -> function(t).observable }.k()