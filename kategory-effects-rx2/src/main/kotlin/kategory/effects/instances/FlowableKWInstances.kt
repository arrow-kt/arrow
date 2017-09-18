package kategory

import io.reactivex.BackpressureStrategy

@instance(FlowableKW::class)
interface FlowableKWMonadInstance :
        FlowableKWApplicativeInstance,
        Monad<FlowableKWHK> {
    override fun <A, B> ap(fa: FlowableKWKind<A>, ff: FlowableKWKind<(A) -> B>): FlowableKW<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: FlowableKWKind<A>, f: (A) -> FlowableKWKind<B>): FlowableKWKind<B> =
            fa.ev().flatMap { f(it).ev() }

    override fun <A, B> tailRecM(a: A, f: (A) -> FlowableKWKind<Either<A, B>>): FlowableKWKind<B> =
            f(a).ev().flatMap {
                it.fold({ tailRecM(a, f).ev() }, { pure(it).ev() })
            }
}

@instance(FlowableKW::class)
interface FlowableKWMonadErrorInstance :
        FlowableKWMonadInstance,
        MonadError<FlowableKWHK, Throwable> {
    override fun <A> raiseError(e: Throwable): FlowableKW<A> =
            FlowableKW.raiseError(e)

    override fun <A> handleErrorWith(fa: FlowableKWKind<A>, f: (Throwable) -> FlowableKWKind<A>): FlowableKW<A> =
            fa.ev().handleErrorWith { f(it).ev() }
}

@instance(FlowableKW::class)
interface FlowableKWAsyncContextInstance : AsyncContext<FlowableKWHK> {
    override fun <A> runAsync(fa: Proc<A>): FlowableKW<A> = FlowableKW.runAsync(fa, BS())
    fun BS(): BackpressureStrategy = BackpressureStrategy.BUFFER
}
