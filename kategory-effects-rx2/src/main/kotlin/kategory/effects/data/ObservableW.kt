package kategory

import io.reactivex.Observable
import io.reactivex.ObservableEmitter

fun <A> Observable<A>.k(): ObservableW<A> = ObservableW(this)

fun <A> ObservableWKind<A>.value(): Observable<A> =
        this.ev().observable

@higherkind
@deriving(Functor::class, Applicative::class, AsyncContext::class)
data class ObservableW<A>(val observable: Observable<A>) : ObservableWKind<A> {
    fun <B> map(f: (A) -> B): ObservableW<B> =
            observable.map(f).k()

    fun <B> ap(fa: ObservableWKind<(A) -> B>): ObservableW<B> =
            flatMap { a -> fa.ev().map { ff -> ff(a) } }

    fun <B> flatMap(f: (A) -> ObservableW<B>): ObservableW<B> =
            observable.flatMap { f(it).observable }.k()

    fun <B> concatMap(f: (A) -> ObservableW<B>): ObservableW<B> =
            observable.concatMap { f(it).observable }.k()

    fun <B> switchMap(f: (A) -> ObservableW<B>): ObservableW<B> =
            observable.switchMap { f(it).observable }.k()

    companion object {
        fun <A> pure(a: A): ObservableW<A> =
                Observable.just(a).k()

        fun <A> raiseError(t: Throwable): ObservableW<A> =
                Observable.error<A>(t).k()

        fun <A> runAsync(fa: Proc<A>): ObservableW<A> =
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

        fun monadFlat(): ObservableWFlatMonadInstance = ObservableWFlatMonadInstanceImplicits.instance()

        fun monadConcat(): ObservableWConcatMonadInstance = ObservableWConcatMonadInstanceImplicits.instance()

        fun monadSwitch(): ObservableWSwitchMonadInstance = ObservableWSwitchMonadInstanceImplicits.instance()

        fun monadErrorFlat(): ObservableWFlatMonadErrorInstance = ObservableWFlatMonadErrorInstanceImplicits.instance()

        fun monadErrorConcat(): ObservableWConcatMonadErrorInstance = ObservableWConcatMonadErrorInstanceImplicits.instance()

        fun monadErrorSwitch(): ObservableWSwitchMonadErrorInstance = ObservableWSwitchMonadErrorInstanceImplicits.instance()
    }
}

fun <A> ObservableW<A>.handleErrorWith(function: (Throwable) -> ObservableW<A>): ObservableW<A> =
        this.observable.onErrorResumeNext { t: Throwable -> function(t).observable }.k()