package kategory

object ObservableWMonadInstanceImplicits {
    @JvmStatic
    fun instance(): ObservableWFlatMonadInstance = ObservableWFlatMonadInstanceImplicits.instance()
}

object ObservableWMonadErrorInstanceImplicits {
    @JvmStatic
    fun instance(): ObservableWFlatMonadErrorInstance = ObservableWFlatMonadErrorInstanceImplicits.instance()
}

interface ObservableWFlatMonadInstance :
        ObservableWApplicativeInstance,
        Monad<ObservableWHK> {
    override fun <A, B> ap(fa: ObservableWKind<A>, ff: ObservableWKind<(A) -> B>): ObservableW<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: ObservableWKind<A>, f: (A) -> ObservableWKind<B>): ObservableWKind<B> =
            fa.ev().flatMap { f(it).ev() }

    override fun <A, B> tailRecM(a: A, f: (A) -> ObservableWKind<Either<A, B>>): ObservableWKind<B> =
            f(a).ev().flatMap {
                it.fold({ tailRecM(a, f).ev() }, { pure(it).ev() })
            }
}

object ObservableWFlatMonadInstanceImplicits {
    @JvmStatic
    fun instance(): ObservableWFlatMonadInstance = object : ObservableWFlatMonadInstance {}
}

interface ObservableWFlatMonadErrorInstance :
        ObservableWFlatMonadInstance,
        MonadError<ObservableWHK, Throwable> {
    override fun <A> raiseError(e: Throwable): ObservableW<A> =
            ObservableW.raiseError(e)

    override fun <A> handleErrorWith(fa: ObservableWKind<A>, f: (Throwable) -> ObservableWKind<A>): ObservableW<A> =
            fa.ev().handleErrorWith { f(it).ev() }
}

object ObservableWFlatMonadErrorInstanceImplicits {
    @JvmStatic
    fun instance(): ObservableWFlatMonadErrorInstance = object : ObservableWFlatMonadErrorInstance {}
}

interface ObservableWConcatMonadInstance :
        ObservableWApplicativeInstance,
        Monad<ObservableWHK> {
    override fun <A, B> ap(fa: ObservableWKind<A>, ff: ObservableWKind<(A) -> B>): ObservableW<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: ObservableWKind<A>, f: (A) -> ObservableWKind<B>): ObservableW<B> =
            fa.ev().concatMap { f(it).ev() }

    override fun <A, B> tailRecM(a: A, f: (A) -> ObservableWKind<Either<A, B>>): ObservableW<B> =
            f(a).ev().concatMap {
                it.fold({ tailRecM(a, f).ev() }, { pure(it).ev() })
            }
}

object ObservableWConcatMonadInstanceImplicits {
    @JvmStatic
    fun instance(): ObservableWConcatMonadInstance = object : ObservableWConcatMonadInstance {}
}

interface ObservableWConcatMonadErrorInstance :
        ObservableWConcatMonadInstance,
        MonadError<ObservableWHK, Throwable> {
    override fun <A> raiseError(e: Throwable): ObservableW<A> =
            ObservableW.raiseError(e)

    override fun <A> handleErrorWith(fa: ObservableWKind<A>, f: (Throwable) -> ObservableWKind<A>): ObservableW<A> =
            fa.ev().handleErrorWith { f(it).ev() }
}

object ObservableWConcatMonadErrorInstanceImplicits {
    @JvmStatic
    fun instance(): ObservableWConcatMonadErrorInstance = object : ObservableWConcatMonadErrorInstance {}
}

interface ObservableWSwitchMonadInstance :
        ObservableWApplicativeInstance,
        Monad<ObservableWHK> {
    override fun <A, B> ap(fa: ObservableWKind<A>, ff: ObservableWKind<(A) -> B>): ObservableW<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: ObservableWKind<A>, f: (A) -> ObservableWKind<B>): ObservableW<B> =
            fa.ev().switchMap { f(it).ev() }

    override fun <A, B> tailRecM(a: A, f: (A) -> ObservableWKind<Either<A, B>>): ObservableW<B> =
            f(a).ev().switchMap {
                it.fold({ tailRecM(a, f).ev() }, { pure(it).ev() })
            }
}

object ObservableWSwitchMonadInstanceImplicits {
    @JvmStatic
    fun instance(): ObservableWSwitchMonadInstance = object : ObservableWSwitchMonadInstance {}
}

interface ObservableWSwitchMonadErrorInstance :
        ObservableWSwitchMonadInstance,
        MonadError<ObservableWHK, Throwable> {

    override fun <A> raiseError(e: Throwable): ObservableW<A> =
            ObservableW.raiseError(e)

    override fun <A> handleErrorWith(fa: ObservableWKind<A>, f: (Throwable) -> ObservableWKind<A>): ObservableW<A> =
            fa.ev().handleErrorWith { f(it).ev() }
}

object ObservableWSwitchMonadErrorInstanceImplicits {
    @JvmStatic
    fun instance(): ObservableWSwitchMonadErrorInstance = object : ObservableWSwitchMonadErrorInstance {}
}