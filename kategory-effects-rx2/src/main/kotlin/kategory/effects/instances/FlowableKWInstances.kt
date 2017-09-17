package kategory

import io.reactivex.BackpressureStrategy

object FlowableKWMonadInstanceImplicits {
    @JvmStatic
    fun instance(): FlowableKWFlatMonadInstance = FlowableKWFlatMonadInstanceImplicits.instance()
}

object FlowableKWMonadErrorInstanceImplicits {
    @JvmStatic
    fun instance(): FlowableKWFlatMonadErrorInstance = FlowableKWFlatMonadErrorInstanceImplicits.instance()
}

interface FlowableKWFlatMonadInstance :
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

object FlowableKWFlatMonadInstanceImplicits {
    @JvmStatic
    fun instance(): FlowableKWFlatMonadInstance = object : FlowableKWFlatMonadInstance {}
}

interface FlowableKWFlatMonadErrorInstance :
        FlowableKWFlatMonadInstance,
        MonadError<FlowableKWHK, Throwable> {
    override fun <A> raiseError(e: Throwable): FlowableKW<A> =
            FlowableKW.raiseError(e)

    override fun <A> handleErrorWith(fa: FlowableKWKind<A>, f: (Throwable) -> FlowableKWKind<A>): FlowableKW<A> =
            fa.ev().handleErrorWith { f(it).ev() }
}

object FlowableKWFlatMonadErrorInstanceImplicits {
    @JvmStatic
    fun instance(): FlowableKWFlatMonadErrorInstance = object : FlowableKWFlatMonadErrorInstance {}
}

interface FlowableKWConcatMonadInstance :
        FlowableKWApplicativeInstance,
        Monad<FlowableKWHK> {
    override fun <A, B> ap(fa: FlowableKWKind<A>, ff: FlowableKWKind<(A) -> B>): FlowableKW<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: FlowableKWKind<A>, f: (A) -> FlowableKWKind<B>): FlowableKW<B> =
            fa.ev().concatMap { f(it).ev() }

    override fun <A, B> tailRecM(a: A, f: (A) -> FlowableKWKind<Either<A, B>>): FlowableKW<B> =
            f(a).ev().concatMap {
                it.fold({ tailRecM(a, f).ev() }, { pure(it).ev() })
            }
}

object FlowableKWConcatMonadInstanceImplicits {
    @JvmStatic
    fun instance(): FlowableKWConcatMonadInstance = object : FlowableKWConcatMonadInstance {}
}

interface FlowableKWConcatMonadErrorInstance :
        FlowableKWConcatMonadInstance,
        MonadError<FlowableKWHK, Throwable> {
    override fun <A> raiseError(e: Throwable): FlowableKW<A> =
            FlowableKW.raiseError(e)

    override fun <A> handleErrorWith(fa: FlowableKWKind<A>, f: (Throwable) -> FlowableKWKind<A>): FlowableKW<A> =
            fa.ev().handleErrorWith { f(it).ev() }
}

object FlowableKWConcatMonadErrorInstanceImplicits {
    @JvmStatic
    fun instance(): FlowableKWConcatMonadErrorInstance = object : FlowableKWConcatMonadErrorInstance {}
}

interface FlowableKWSwitchMonadInstance :
        FlowableKWApplicativeInstance,
        Monad<FlowableKWHK> {
    override fun <A, B> ap(fa: FlowableKWKind<A>, ff: FlowableKWKind<(A) -> B>): FlowableKW<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: FlowableKWKind<A>, f: (A) -> FlowableKWKind<B>): FlowableKW<B> =
            fa.ev().switchMap { f(it).ev() }

    override fun <A, B> tailRecM(a: A, f: (A) -> FlowableKWKind<Either<A, B>>): FlowableKW<B> =
            f(a).ev().switchMap {
                it.fold({ tailRecM(a, f).ev() }, { pure(it).ev() })
            }
}

object FlowableKWSwitchMonadInstanceImplicits {
    @JvmStatic
    fun instance(): FlowableKWSwitchMonadInstance = object : FlowableKWSwitchMonadInstance {}
}

interface FlowableKWSwitchMonadErrorInstance :
        FlowableKWSwitchMonadInstance,
        MonadError<FlowableKWHK, Throwable> {

    override fun <A> raiseError(e: Throwable): FlowableKW<A> =
            FlowableKW.raiseError(e)

    override fun <A> handleErrorWith(fa: FlowableKWKind<A>, f: (Throwable) -> FlowableKWKind<A>): FlowableKW<A> =
            fa.ev().handleErrorWith { f(it).ev() }
}

object FlowableKWSwitchMonadErrorInstanceImplicits {
    @JvmStatic
    fun instance(): FlowableKWSwitchMonadErrorInstance = object : FlowableKWSwitchMonadErrorInstance {}
}

interface FlowableKWAsyncContextInstance : AsyncContext<FlowableKWHK> {
    override fun <A> runAsync(fa: Proc<A>): FlowableKW<A> = FlowableKW.runAsync(fa, BS())
    fun BS(): BackpressureStrategy = BackpressureStrategy.BUFFER
}

object FlowableKWAsyncContextInstanceImplicits : FlowableKWAsyncContextInstance {
    @JvmStatic
    fun instance(): FlowableKWAsyncContextInstance = object : FlowableKWAsyncContextInstance {}
}