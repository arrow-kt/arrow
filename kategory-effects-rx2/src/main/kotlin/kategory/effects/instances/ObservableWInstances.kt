package kategory

import io.reactivex.Observable

interface ObservableWInstances : Functor<ObservableWHK>,
        Applicative<ObservableWHK>,
        AsyncContext<ObservableWHK> {

    override fun <A> pure(a: A): HK<ObservableWHK, A> =
            ObservableW.pure(a)

    override fun <A, B> ap(fa: HK<ObservableWHK, A>, ff: HK<ObservableWHK, (A) -> B>): HK<ObservableWHK, B> =
            ff.ev().flatMap { fa.ev().map(it) }

    override fun <A> runAsync(fa: Proc<A>): HK<ObservableWHK, A> =
            ObservableW.runAsync(fa)
}

interface ObservableWFlatMapInstances :
        ObservableWInstances,
        Monad<ObservableWHK>,
        MonadError<ObservableWHK, Throwable> {
    override fun <A, B> ap(fa: HK<ObservableWHK, A>, ff: HK<ObservableWHK, (A) -> B>): HK<ObservableWHK, B> =
            ff.ev().flatMap { fa.ev().map(it) }

    override fun <A, B> flatMap(fa: HK<ObservableWHK, A>, f: (A) -> HK<ObservableWHK, B>): HK<ObservableWHK, B> =
            fa.ev().flatMap { f(it).ev() }

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<ObservableWHK, Either<A, B>>): HK<ObservableWHK, B> =
            f(a).ev().flatMap {
                it.fold({ tailRecM(a, f).ev() }, { Observable.just(it).k() })
            }

    override fun <A> raiseError(e: Throwable): HK<ObservableWHK, A> =
            ObservableW.raiseError(e)

    override fun <A> handleErrorWith(fa: HK<ObservableWHK, A>, f: (Throwable) -> HK<ObservableWHK, A>): HK<ObservableWHK, A> =
            fa.ev().handleErrorWith { f(it).ev() }
}

interface ObservableWConcatMapInstances :
        ObservableWInstances,
        Monad<ObservableWHK>,
        MonadError<ObservableWHK, Throwable> {
    override fun <A, B> ap(fa: HK<ObservableWHK, A>, ff: HK<ObservableWHK, (A) -> B>): HK<ObservableWHK, B> =
            ff.ev().flatMap { fa.ev().map(it) }

    override fun <A, B> flatMap(fa: HK<ObservableWHK, A>, f: (A) -> HK<ObservableWHK, B>): HK<ObservableWHK, B> =
            fa.ev().concatMap { f(it).ev() }

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<ObservableWHK, Either<A, B>>): HK<ObservableWHK, B> =
            f(a).ev().concatMap {
                it.fold({ tailRecM(a, f).ev() }, { Observable.just(it).k() })
            }

    override fun <A> raiseError(e: Throwable): HK<ObservableWHK, A> =
            ObservableW.raiseError(e)

    override fun <A> handleErrorWith(fa: HK<ObservableWHK, A>, f: (Throwable) -> HK<ObservableWHK, A>): HK<ObservableWHK, A> =
            fa.ev().handleErrorWith { f(it).ev() }
}

interface ObservableWSwitchMapInstances :
        ObservableWInstances,
        Monad<ObservableWHK>,
        MonadError<ObservableWHK, Throwable> {
    override fun <A, B> ap(fa: HK<ObservableWHK, A>, ff: HK<ObservableWHK, (A) -> B>): HK<ObservableWHK, B> =
            ff.ev().flatMap { fa.ev().map(it) }

    override fun <A, B> flatMap(fa: HK<ObservableWHK, A>, f: (A) -> HK<ObservableWHK, B>): HK<ObservableWHK, B> =
            fa.ev().switchMap { f(it).ev() }

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<ObservableWHK, Either<A, B>>): HK<ObservableWHK, B> =
            f(a).ev().switchMap {
                it.fold({ tailRecM(a, f).ev() }, { Observable.just(it).k() })
            }

    override fun <A> raiseError(e: Throwable): HK<ObservableWHK, A> =
            ObservableW.raiseError(e)

    override fun <A> handleErrorWith(fa: HK<ObservableWHK, A>, f: (Throwable) -> HK<ObservableWHK, A>): HK<ObservableWHK, A> =
            fa.ev().handleErrorWith { f(it).ev() }
}