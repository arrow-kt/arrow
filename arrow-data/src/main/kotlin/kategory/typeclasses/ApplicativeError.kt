package arrow

interface ApplicativeError<F, E> : Applicative<F>, Typeclass {

    fun <A> raiseError(e: E): HK<F, A>

    fun <A> handleErrorWith(fa: HK<F, A>, f: (E) -> HK<F, A>): HK<F, A>

    fun <A> handleError(fa: HK<F, A>, f: (E) -> A): HK<F, A> = handleErrorWith(fa) { pure(f(it)) }

    fun <A> attempt(fa: HK<F, A>): HK<F, Either<E, A>> =
            handleErrorWith(map(fa) { Right(it) }) {
                pure(Left(it))
            }

    fun <A> fromEither(fab: Either<E, A>): HK<F, A> = fab.fold({ raiseError<A>(it) }, { pure(it) })

    fun <A> catch(f: () -> A, recover: (Throwable) -> E): HK<F, A> =
            try {
                pure(f())
            } catch (t: Throwable) {
                raiseError<A>(recover(t))
            }
}

inline fun <reified F, reified E, A> A.raiseError(FT: ApplicativeError<F, E> = applicativeError(), e: E): HK<F, A> = FT.raiseError<A>(e)

inline fun <reified F, reified E, A> HK<F, A>.handlerErrorWith(FT: ApplicativeError<F, E> = applicativeError(), noinline f: (E) -> HK<F, A>): HK<F, A> =
        FT.handleErrorWith(this, f)

inline fun <reified F, reified E, A> HK<F, A>.attempt(FT: ApplicativeError<F, E> = applicativeError()): HK<F, Either<E, A>> = FT.attempt(this)

inline fun <reified F, reified E, A> Either<E, A>.toError(FT: ApplicativeError<F, E> = applicativeError()): HK<F, A> = FT.fromEither(this)

inline fun <reified F, A> (() -> A).catch(FT: ApplicativeError<F, Throwable> = applicativeError()): HK<F, A> = FT.catch(this)

fun <F, A> ApplicativeError<F, Throwable>.catch(f: () -> A): HK<F, A> =
        try {
            pure(f())
        }
        catch (e: Throwable) {
            raiseError(e)
        }

inline fun <reified F, reified E> applicativeError(): ApplicativeError<F, E> =
        instance(InstanceParametrizedType(ApplicativeError::class.java, listOf(typeLiteral<F>(), typeLiteral<E>())))
