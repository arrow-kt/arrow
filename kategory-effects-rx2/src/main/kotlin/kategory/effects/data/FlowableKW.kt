package kategory.effects

import kategory.*
import io.reactivex.*

fun <A> Flowable<A>.k(): FlowableKW<A> = FlowableKW(this)

fun <A> FlowableKWKind<A>.value(): Flowable<A> = this.ev().flowable

@higherkind
@deriving(Functor::class, Applicative::class, Monad::class)
data class FlowableKW<A>(val flowable: Flowable<A>) : FlowableKWKind<A> {

    fun <B> map(f: (A) -> B): FlowableKW<B> =
            flowable.map(f).k()

    fun <B> ap(fa: FlowableKWKind<(A) -> B>): FlowableKW<B> =
            flatMap { a -> fa.ev().map { ff -> ff(a) } }

    fun <B> flatMap(f: (A) -> FlowableKWKind<B>): FlowableKW<B> =
            flowable.flatMap { f(it).ev().flowable }.k()

    fun <B> concatMap(f: (A) -> FlowableKWKind<B>): FlowableKW<B> =
            flowable.concatMap { f(it).ev().flowable }.k()

    fun <B> switchMap(f: (A) -> FlowableKWKind<B>): FlowableKW<B> =
            flowable.switchMap { f(it).ev().flowable }.k()

    companion object {
        fun <A> pure(a: A): FlowableKW<A> =
                Flowable.just(a).k()

        fun <A> raiseError(t: Throwable): FlowableKW<A> =
                Flowable.error<A>(t).k()

        fun <A> runAsync(fa: Proc<A>, mode: BackpressureStrategy = BackpressureStrategy.BUFFER): FlowableKW<A> =
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

        fun <A, B> tailRecM(a: A, f: (A) -> FlowableKWKind<Either<A, B>>): FlowableKW<B> =
                f(a).ev().flatMap {
                    it.fold({ tailRecM(a, f).ev() }, { pure(it).ev() })
                }

        fun monadFlat(): FlowableKWMonadInstance = FlowableKWMonadInstanceImplicits.instance()

        fun monadConcat(): FlowableKWMonadInstance = object : FlowableKWMonadInstance {
            override fun <A, B> flatMap(fa: FlowableKWKind<A>, f: (A) -> FlowableKWKind<B>): FlowableKW<B> =
                    fa.ev().concatMap { f(it).ev() }

            override fun <A, B> tailRecM(a: A, f: (A) -> FlowableKWKind<Either<A, B>>): FlowableKW<B> =
                    f(a).ev().concatMap {
                        it.fold({ tailRecM(a, f).ev() }, { pure(it).ev() })
                    }
        }

        fun monadSwitch(): FlowableKWMonadInstance = object : FlowableKWMonadInstance {
            override fun <A, B> flatMap(fa: FlowableKWKind<A>, f: (A) -> FlowableKWKind<B>): FlowableKW<B> =
                    fa.ev().switchMap { f(it).ev() }

            override fun <A, B> tailRecM(a: A, f: (A) -> FlowableKWKind<Either<A, B>>): FlowableKW<B> =
                    f(a).ev().switchMap {
                        it.fold({ tailRecM(a, f).ev() }, { pure(it).ev() })
                    }
        }

        fun monadErrorFlat(): FlowableKWMonadErrorInstance = FlowableKWMonadErrorInstanceImplicits.instance()

        fun monadErrorConcat(): FlowableKWMonadErrorInstance = object : FlowableKWMonadErrorInstance {
            override fun <A, B> flatMap(fa: FlowableKWKind<A>, f: (A) -> FlowableKWKind<B>): FlowableKW<B> =
                    fa.ev().concatMap { f(it).ev() }

            override fun <A, B> tailRecM(a: A, f: (A) -> FlowableKWKind<Either<A, B>>): FlowableKW<B> =
                    f(a).ev().concatMap {
                        it.fold({ tailRecM(a, f).ev() }, { Companion.pure(it).ev() })
                    }
        }

        fun monadErrorSwitch(): FlowableKWMonadErrorInstance = object : FlowableKWMonadErrorInstance {
            override fun <A, B> flatMap(fa: FlowableKWKind<A>, f: (A) -> FlowableKWKind<B>): FlowableKW<B> =
                    fa.ev().switchMap { f(it).ev() }

            override fun <A, B> tailRecM(a: A, f: (A) -> FlowableKWKind<Either<A, B>>): FlowableKW<B> =
                    f(a).ev().switchMap {
                        it.fold({ tailRecM(a, f).ev() }, { Companion.pure(it).ev() })
                    }
        }

        fun asyncContextBuffer(): FlowableKWAsyncContextInstance = FlowableKWAsyncContextInstanceImplicits.instance()

        fun asyncContextDrop(): FlowableKWAsyncContextInstance = object : FlowableKWAsyncContextInstance {
            override fun BS(): BackpressureStrategy = BackpressureStrategy.DROP
        }

        fun asyncContextError(): FlowableKWAsyncContextInstance = object : FlowableKWAsyncContextInstance {
            override fun BS(): BackpressureStrategy = BackpressureStrategy.ERROR
        }

        fun asyncContextLatest(): FlowableKWAsyncContextInstance = object : FlowableKWAsyncContextInstance {
            override fun BS(): BackpressureStrategy = BackpressureStrategy.LATEST
        }

        fun asyncContextMissing(): FlowableKWAsyncContextInstance = object : FlowableKWAsyncContextInstance {
            override fun BS(): BackpressureStrategy = BackpressureStrategy.MISSING
        }
    }
}

fun <A> FlowableKWKind<A>.handleErrorWith(function: (Throwable) -> FlowableKW<A>): FlowableKW<A> =
        this.ev().flowable.onErrorResumeNext { t: Throwable -> function(t).flowable }.k()