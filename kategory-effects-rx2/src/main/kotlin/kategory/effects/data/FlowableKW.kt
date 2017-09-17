package kategory

import io.reactivex.*

fun <A> Flowable<A>.k(): FlowableKW<A> = FlowableKW(this)

fun <A> FlowableKWKind<A>.value(): Flowable<A> = this.ev().flowable

@higherkind
@deriving(Functor::class, Applicative::class)
data class FlowableKW<A>(val flowable: Flowable<A>) : FlowableKWKind<A> {

    fun <B> map(f: (A) -> B): FlowableKW<B> =
            flowable.map(f).k()

    fun <B> ap(fa: FlowableKWKind<(A) -> B>): FlowableKW<B> =
            flatMap { a -> fa.ev().map { ff -> ff(a) } }

    fun <B> flatMap(f: (A) -> FlowableKW<B>): FlowableKW<B> =
            flowable.flatMap { f(it).flowable }.k()

    fun <B> concatMap(f: (A) -> FlowableKW<B>): FlowableKW<B> =
            flowable.concatMap { f(it).flowable }.k()

    fun <B> switchMap(f: (A) -> FlowableKW<B>): FlowableKW<B> =
            flowable.switchMap { f(it).flowable }.k()

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

        fun monadFlat(): FlowableKWFlatMonadInstance = FlowableKWFlatMonadInstanceImplicits.instance()

        fun monadConcat(): FlowableKWConcatMonadInstance = FlowableKWConcatMonadInstanceImplicits.instance()

        fun monadSwitch(): FlowableKWSwitchMonadInstance = FlowableKWSwitchMonadInstanceImplicits.instance()

        fun monadErrorFlat(): FlowableKWFlatMonadErrorInstance = FlowableKWFlatMonadErrorInstanceImplicits.instance()

        fun monadErrorConcat(): FlowableKWConcatMonadErrorInstance = FlowableKWConcatMonadErrorInstanceImplicits.instance()

        fun monadErrorSwitch(): FlowableKWSwitchMonadErrorInstance = FlowableKWSwitchMonadErrorInstanceImplicits.instance()

        fun asyncContext(): FlowableKWAsyncContextInstance = asyncContextBuffer()

        fun asyncContextBuffer(): FlowableKWAsyncContextInstance = object : FlowableKWAsyncContextInstance {
            override fun BS(): BackpressureStrategy = BackpressureStrategy.BUFFER
        }

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

fun <A> FlowableKW<A>.handleErrorWith(function: (Throwable) -> FlowableKW<A>): FlowableKW<A> =
        this.flowable.onErrorResumeNext { t: Throwable -> function(t).flowable }.k()