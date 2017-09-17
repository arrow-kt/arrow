package kategory

object ObservableKWMonadInstanceImplicits {
    @JvmStatic
    fun instance(): ObservableKWFlatMonadInstance = ObservableKWFlatMonadInstanceImplicits.instance()
}

object ObservableKWMonadErrorInstanceImplicits {
    @JvmStatic
    fun instance(): ObservableKWFlatMonadErrorInstance = ObservableKWFlatMonadErrorInstanceImplicits.instance()
}

interface ObservableKWFlatMonadInstance :
        ObservableKWApplicativeInstance,
        Monad<ObservableKWHK> {
    override fun <A, B> ap(fa: ObservableKWKind<A>, ff: ObservableKWKind<(A) -> B>): ObservableKW<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: ObservableKWKind<A>, f: (A) -> ObservableKWKind<B>): ObservableKW<B> =
            fa.ev().flatMap { f(it).ev() }

    override fun <A, B> tailRecM(a: A, f: (A) -> ObservableKWKind<Either<A, B>>): ObservableKW<B> =
            f(a).ev().flatMap {
                it.fold({ tailRecM(a, f).ev() }, { pure(it).ev() })
            }
}

object ObservableKWFlatMonadInstanceImplicits {
    @JvmStatic
    fun instance(): ObservableKWFlatMonadInstance = object : ObservableKWFlatMonadInstance {}
}

interface ObservableKWFlatMonadErrorInstance :
        ObservableKWFlatMonadInstance,
        MonadError<ObservableKWHK, Throwable> {
    override fun <A> raiseError(e: Throwable): ObservableKW<A> =
            ObservableKW.raiseError(e)

    override fun <A> handleErrorWith(fa: ObservableKWKind<A>, f: (Throwable) -> ObservableKWKind<A>): ObservableKW<A> =
            fa.ev().handleErrorWith { f(it).ev() }
}

object ObservableKWFlatMonadErrorInstanceImplicits {
    @JvmStatic
    fun instance(): ObservableKWFlatMonadErrorInstance = object : ObservableKWFlatMonadErrorInstance {}
}

interface ObservableKWConcatMonadInstance :
        ObservableKWApplicativeInstance,
        Monad<ObservableKWHK> {
    override fun <A, B> ap(fa: ObservableKWKind<A>, ff: ObservableKWKind<(A) -> B>): ObservableKW<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: ObservableKWKind<A>, f: (A) -> ObservableKWKind<B>): ObservableKW<B> =
            fa.ev().concatMap { f(it).ev() }

    override fun <A, B> tailRecM(a: A, f: (A) -> ObservableKWKind<Either<A, B>>): ObservableKW<B> =
            f(a).ev().concatMap {
                it.fold({ tailRecM(a, f).ev() }, { pure(it).ev() })
            }
}

object ObservableKWConcatMonadInstanceImplicits {
    @JvmStatic
    fun instance(): ObservableKWConcatMonadInstance = object : ObservableKWConcatMonadInstance {}
}

interface ObservableKWConcatMonadErrorInstance :
        ObservableKWConcatMonadInstance,
        MonadError<ObservableKWHK, Throwable> {
    override fun <A> raiseError(e: Throwable): ObservableKW<A> =
            ObservableKW.raiseError(e)

    override fun <A> handleErrorWith(fa: ObservableKWKind<A>, f: (Throwable) -> ObservableKWKind<A>): ObservableKW<A> =
            fa.ev().handleErrorWith { f(it).ev() }
}

object ObservableKWConcatMonadErrorInstanceImplicits {
    @JvmStatic
    fun instance(): ObservableKWConcatMonadErrorInstance = object : ObservableKWConcatMonadErrorInstance {}
}

interface ObservableKWSwitchMonadInstance :
        ObservableKWApplicativeInstance,
        Monad<ObservableKWHK> {
    override fun <A, B> ap(fa: ObservableKWKind<A>, ff: ObservableKWKind<(A) -> B>): ObservableKW<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: ObservableKWKind<A>, f: (A) -> ObservableKWKind<B>): ObservableKW<B> =
            fa.ev().switchMap { f(it).ev() }

    override fun <A, B> tailRecM(a: A, f: (A) -> ObservableKWKind<Either<A, B>>): ObservableKW<B> =
            f(a).ev().switchMap {
                it.fold({ tailRecM(a, f).ev() }, { pure(it).ev() })
            }
}

object ObservableKWSwitchMonadInstanceImplicits {
    @JvmStatic
    fun instance(): ObservableKWSwitchMonadInstance = object : ObservableKWSwitchMonadInstance {}
}

interface ObservableKWSwitchMonadErrorInstance :
        ObservableKWSwitchMonadInstance,
        MonadError<ObservableKWHK, Throwable> {

    override fun <A> raiseError(e: Throwable): ObservableKW<A> =
            ObservableKW.raiseError(e)

    override fun <A> handleErrorWith(fa: ObservableKWKind<A>, f: (Throwable) -> ObservableKWKind<A>): ObservableKW<A> =
            fa.ev().handleErrorWith { f(it).ev() }
}

object ObservableKWSwitchMonadErrorInstanceImplicits {
    @JvmStatic
    fun instance(): ObservableKWSwitchMonadErrorInstance = object : ObservableKWSwitchMonadErrorInstance {}
}