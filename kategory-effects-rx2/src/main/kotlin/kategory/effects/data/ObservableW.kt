package kategory

import io.reactivex.Observable
import io.reactivex.ObservableEmitter

fun <A> Observable<A>.k(): ObservableW<A> = ObservableW(this)

fun <A> ObservableWKind<A>.value(): Observable<A> =
        this.ev().observable

@higherkind data class ObservableW<A>(val observable: Observable<A>) : ObservableWKind<A> {
    fun <B> map(f: (A) -> B): ObservableW<B> =
            ObservableW(observable.map(f))

    fun <B> flatMap(f: (A) -> ObservableW<B>): ObservableW<B> =
            ObservableW(observable.flatMap { f(it).observable })

    fun <B> concatMap(f: (A) -> ObservableW<B>): ObservableW<B> =
            ObservableW(observable.concatMap { f(it).observable })

    fun <B> switchMap(f: (A) -> ObservableW<B>): ObservableW<B> =
            ObservableW(observable.switchMap { f(it).observable })

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

        inline fun instances(): ObservableWInstances =
                object : ObservableWInstances {
                }

        inline fun instancesFlatMap(): ObservableWFlatMapInstances = object : ObservableWFlatMapInstances {}

        inline fun instancesConcatMap(): ObservableWConcatMapInstances = object : ObservableWConcatMapInstances {}

        inline fun instancesSwitchMap(): ObservableWSwitchMapInstances = object : ObservableWSwitchMapInstances {}

        fun functor(): Functor<ObservableWHK> = instances()

        fun applicative(): Applicative<ObservableWHK> = instances()

        fun monadFlat(): Monad<ObservableWHK> = instancesFlatMap()

        fun monadConcat(): Monad<ObservableWHK> = instancesConcatMap()

        fun monadSwitch(): Monad<ObservableWHK> = instancesSwitchMap()

        fun monadErrorFlat(): MonadError<ObservableWHK, Throwable> = instancesFlatMap()

        fun monadErrorConcat(): MonadError<ObservableWHK, Throwable> = instancesConcatMap()

        fun monadErrorSwitch(): MonadError<ObservableWHK, Throwable> = instancesSwitchMap()

        fun asyncContext(): AsyncContext<ObservableWHK> = instances()
    }
}

fun <A> ObservableW<A>.handleErrorWith(function: (Throwable) -> ObservableW<A>): ObservableW<A> =
        ObservableW(this.observable.onErrorResumeNext { t: Throwable -> function(t).observable })