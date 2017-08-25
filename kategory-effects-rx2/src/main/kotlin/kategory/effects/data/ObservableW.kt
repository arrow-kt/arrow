package kategory

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableSource

fun <A> Observable<A>.k(): ObservableW<A> = ObservableW { this }

@higherkind data class ObservableW<A>(val thunk: ((Either<Throwable, A>) -> Unit) -> Observable<A>) : ObservableWKind<A> {
    fun <B> map(f: (A) -> B): ObservableW<B> =
            ObservableW { ff: (Either<Throwable, B>) -> Unit ->
                thunk { either: Either<Throwable, A> ->
                    ff(either.map(f))
                }.map(f)
            }

    fun <B> flatMap(f: (A) -> ObservableW<B>): ObservableW<B> =
            ObservableW { ff: (Either<Throwable, B>) -> Unit ->
                thunk { either: Either<Throwable, A> ->
                    either.fold({ t: Throwable -> ObservableW<B> { Observable.error<B>(t) } }, { f(it) })
                }.flatMap { f(it).thunk(ff) }
            }

    fun <B> concatMap(f: (A) -> ObservableW<B>): ObservableW<B> =
            ObservableW { ff: (Either<Throwable, B>) -> Unit ->
                thunk { either: Either<Throwable, A> ->
                    either.fold({ t: Throwable -> ObservableW<B> { Observable.error<B>(t) } }, { f(it) })
                }.concatMap { f(it).thunk(ff) }
            }

    fun <B> switchMap(f: (A) -> ObservableW<B>): ObservableW<B> =
            ObservableW { ff: (Either<Throwable, B>) -> Unit ->
                thunk { either: Either<Throwable, A> ->
                    either.fold({ t: Throwable -> ObservableW<B> { Observable.error<B>(t) } }, { f(it) })
                }.switchMap { f(it).thunk(ff) }
            }

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

fun <A> ObservableWKind<A>.runObservable(ff: (Either<Throwable, A>) -> Unit): Observable<A> =
        this.ev().thunk(ff)

fun <A> ObservableW<A>.handleErrorWith(function: (Throwable) -> ObservableW<A>): ObservableW<A> =
        ObservableW { ff: (Either<Throwable, A>) -> Unit ->
            this.thunk { either: Either<Throwable, A> -> ff(either) }.onErrorResumeNext { t: Throwable -> function(t).thunk(ff) }
        }