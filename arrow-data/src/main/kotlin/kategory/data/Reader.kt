package arrow

/**
 * Alias that represents a computation that has a dependency on [D].
 */
typealias ReaderFun<D, A> = (D) -> A

/**
 * Alias ReaderHK for [ReaderTHK]
 *
 * @see ReaderTHK
 */
typealias ReaderHK = ReaderTHK

/**
 * Alias ReaderKind for [ReaderTKind]
 *
 * @see ReaderTKind
 */
typealias ReaderKind<D, A> = ReaderTKind<IdHK, D, A>

/**
 * Alias to partially apply type parameter [D] to [Reader].
 *
 * @see ReaderTKindPartial
 */
typealias ReaderKindPartial<D> = ReaderTKindPartial<IdHK, D>

/**
 * [Reader] represents a computation that has a dependency on [D].
 * `Reader<D, A>` is an alias for `ReaderT<IdHK, D, A>` and `Kleisli<IdHK, D, A>`.
 *
 * @param D the dependency or environment we depend on.
 * @param A resulting type of the computation.
 * @see ReaderT
 */
typealias Reader<D, A> = ReaderT<IdHK, D, A>

/**
 * Constructor for [Reader].
 *
 * @param run the dependency dependent computation.
 */
@Suppress("FunctionName")
fun <D, A> Reader(run: ReaderFun<D, A>): Reader<D, A> = ReaderT(run.andThen { Id(it) })

/**
 * Syntax for constructing a [Reader]
 *
 * @receiver [ReaderFun] a function that represents computation dependent on type [D].
 */
fun <D, A> (ReaderFun<D, A>).reader(): Reader<D, A> = Reader(this)

/**
 * Alias for [Kleisli.run]
 *
 * @param d dependency to runId the computation.
 */
fun <D, A> Reader<D, A>.runId(d: D): A = this.run(d).value()

/**
 * Map the result of the computation [A] to [B] given a function [f].
 *
 * @param f the function to apply.
 */
fun <D, A, B> Reader<D, A>.map(f: (A) -> B): Reader<D, B> = map(f, Id.functor())

/**
 * FlatMap the result of the computation [A] to another [Reader] for the same dependency [D] and flatten the structure.
 *
 * @param f the function to apply.
 */
fun <D, A, B> Reader<D, A>.flatMap(f: (A) -> Reader<D, B>): Reader<D, B> = flatMap(f, Id.monad())

/**
 * Apply a function `(A) -> B` that operates within the context of [Reader].
 *
 * @param ff function that maps [A] to [B] within the [Reader] context.
 */
fun <D, A, B> Reader<D, A>.ap(ff: ReaderKind<D, (A) -> B>): Reader<D, B> = ap(ff, Id.applicative())

/**
 * Zip with another [Reader].
 *
 * @param o other [Reader] to zip with.
 */
fun <D, A, B> Reader<D, A>.zip(o: Reader<D, B>): Reader<D, Tuple2<A, B>> = zip(o, Id.monad())

/**
 * Compose with another [Reader] that has a dependency on the output of the computation.
 *
 * @param o other [Reader] to compose with.
 */
fun <D, A, C> Reader<D, A>.andThen(o: Reader<A, C>): Reader<D, C> = andThen(o, Id.monad())

/**
 * Map the result of the computation [A] to [B] given a function [f].
 * Alias for [map]
 *
 * @param f the function to apply.
 * @see map
 */
fun <D, A, B> Reader<D, A>.andThen(f: (A) -> B): Reader<D, B> = map(f)

/**
 * Set the result to [B] after running the computation.
 */
fun <D, A, B> Reader<D, A>.andThen(b: B): Reader<D, B> = map { _ -> b }

@Suppress("FunctionName")
fun Reader(): ReaderApi = ReaderApi

object ReaderApi {

    /**
     * Alias for[ReaderT.Companion.applicative]
     */
    fun <D> applicative(): Applicative<ReaderKindPartial<D>> = ReaderT.applicative(Id.monad(), dummy = Unit)

    /**
     * Alias for [ReaderT.Companion.functor]
     */
    fun <D> functor(): Functor<ReaderKindPartial<D>> = ReaderT.functor(Id.functor(), dummy = Unit)

    /**
     * Alias for [ReaderT.Companion.monad]
     */
    fun <D> monad(): Monad<ReaderKindPartial<D>> = ReaderT.monad(Id.monad(), dummy = Unit)

    fun <D> monadReader(): MonadReader<ReaderKindPartial<D>, D> = ReaderT.monadReader(Id.monad(), dummy = Unit)

    fun <D, A> pure(x: A): Reader<D, A> = ReaderT.pure(x, Id.monad())

    fun <D> ask(): Reader<D, D> = ReaderT.ask(Id.monad())

}
