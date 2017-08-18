package kategory

/**
 * Data structures that can be reduced to a summary value.
 *
 * Reducible is like a non-empty Foldable. In addition to the fold methods it provides reduce
 * methods which do not require an initial value.
 *
 * In addition to the methods needed by `Foldable`, `Reducible` is implemented in terms of two methods:
 *
 *  - reduceLeftTo(fa)(f)(g) eagerly reduces with an additional mapping function
 *  - reduceRightTo(fa)(f)(g) lazily reduces with an additional mapping function
 */
interface Reducible<in F> : Foldable<F>, Typeclass {

    /**
     * Left-associative reduction on F using the function f.
     *
     * Implementations should override this method when possible.
     */
    fun <A> reduceLeft(fa: HK<F, A>, f: (A, A) -> A): A =
            reduceLeftTo(fa, { a -> a }, f)

    /**
     * Right-associative reduction on F using the function f.
     */
    fun <A> reduceRight(fa: HK<F, A>, f: (A, Eval<A>) -> Eval<A>): Eval<A> =
            reduceRightTo(fa, { a -> a }, f)

    /**
     * Apply f to the "initial element" of fa and combine it with every other value using
     * the given function g.
     */
    fun <A, B> reduceLeftTo(fa: HK<F, A>, f: (A) -> B, g: (B, A) -> B): B

    fun <A, B> reduceLeftToOption(fa: HK<F, A>, f: (A) -> B, g: (B, A) -> B): Option<B> =
            Option.Some(reduceLeftTo(fa, f, g))

    /**
     * Apply f to the "initial element" of fa and lazily combine it with every other value using the
     * given function g.
     */
    fun <A, B> reduceRightTo(fa: HK<F, A>, f: (A) -> B, g: (A, Eval<B>) -> Eval<B>): Eval<B>

    fun <A, B> reduceRightToOption(fa: HK<F, A>, f: (A) -> B, g: (A, Eval<B>) -> Eval<B>): Eval<Option<B>> =
            reduceRightTo(fa, f, g).map({ Option.Some(it) })

    fun <A> toNonEmptyList(fa: HK<F, A>): NonEmptyList<A> =
            reduceRightTo(fa, { a -> NonEmptyList.of(a) }, { a, lnel ->
                lnel.map { nonEmptyList -> NonEmptyList(a, listOf(nonEmptyList.head) + nonEmptyList.tail) }
            }).value()

    override fun <A> isEmpty(fa: HK<F, A>): Boolean = false

    override fun <A> nonEmpty(fa: HK<F, A>): Boolean = true
}

/**
 * Reduce a F<A> value using the given Semigroup<A>.
 */
inline fun <F, reified A> Reducible<F>.reduce(fa: HK<F, A>, SA: Semigroup<A> = semigroup()): A =
        reduceLeft(fa, { a, b -> SA.combine(a, b) })

/**
 * Reduce a F<G<A>> value using SemigroupK<G>, a universal semigroup for G<_>.
 *
 * This method is a generalization of reduce.
 */
inline fun <F, reified G, A> Reducible<F>.reduceK(fga: HK<F, HK<G, A>>, SGKG: SemigroupK<G> = semigroupK()): HK<G, A> =
        reduce(fga, SGKG.algebra())

/**
 * Apply f to each element of fa and combine them using the given Semigroup<B>.
 */
inline fun <F, A, reified B> Reducible<F>.reduceMap(fa: HK<F, A>, noinline f: (A) -> B, SB: Semigroup<B> = semigroup()): B =
        reduceLeftTo(fa, f, { b, a -> SB.combine(b, f(a)) })

inline fun <reified F> reducible(): Reducible<F> = instance(InstanceParametrizedType(Reducible::class.java, listOf(F::class.java)))


/**
 * This class defines a Reducible<F> in terms of a Foldable<G> together with a split method, F<A> -> (A, G<A>).
 *
 * This class can be used on any type where the first value (A) and the "rest" of the values (G<A>) can be easily found.
 */
abstract class NonEmptyReducible<F, G> : Reducible<F> {

    abstract fun FG(): Foldable<G>

    abstract fun <A> split(fa: HK<F, A>): Tuple2<A, HK<G, A>>

    override fun <A, B> foldL(fa: HK<F, A>, b: B, f: (B, A) -> B): B {
        val (a, ga) = split(fa)
        return FG().foldL(ga, f(b, a), f)
    }

    override fun <A, B> foldR(fa: HK<F, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            Eval.Always({ split(fa) }).flatMap { (a, ga) -> f(a, FG().foldR(ga, lb, f)) }

    override fun <A, B> reduceLeftTo(fa: HK<F, A>, f: (A) -> B, g: (B, A) -> B): B {
        val (a, ga) = split(fa)
        return FG().foldL(ga, f(a), { b, a -> g(b, a) })
    }

    override fun <A, B> reduceRightTo(fa: HK<F, A>, f: (A) -> B, g: (A, Eval<B>) -> Eval<B>): Eval<B> =
            Eval.Always({ split(fa) }).flatMap { (a, ga) -> FG().reduceRightToOption(ga)(f)(g).flatMap {
                    case Some (b) => g(a, Now(b))
                    case None => Later (f(a))
                }
            }

    /*
    def reduceRightTo[A, B](fa: F[A])(f: A => B)(g: (A, Eval[B]) => Eval[B]): Eval[B] =
    Always(split(fa)).flatMap { case (a, ga) =>
        G.reduceRightToOption(ga)(f)(g).flatMap {
            case Some(b) => g(a, Now(b))
            case None => Later(f(a))
        }
    }

    override def size[A](fa: F[A]): Long = {
        val (_, tail) = split(fa)
        1 + G.size(tail)
    }

    override def get[A](fa: F[A])(idx: Long): Option[A] =
    if (idx == 0L) Some(split(fa)._1) else G.get(split(fa)._2)(idx - 1L)

    override def fold[A](fa: F[A])(implicit A: Monoid[A]): A = {
        val (a, ga) = split(fa)
        A.combine(a, G.fold(ga))
    }

    override def foldM[H[_], A, B](fa: F[A], z: B)(f: (B, A) => H[B])(implicit H: Monad[H]): H[B] = {
        val (a, ga) = split(fa)
        H.flatMap(f(z, a))(G.foldM(ga, _)(f))
    }

    override def find[A](fa: F[A])(f: A => Boolean): Option[A] = {
        val (a, ga) = split(fa)
        if (f(a)) Some(a) else G.find(ga)(f)
    }

    override def exists[A](fa: F[A])(p: A => Boolean): Boolean = {
        val (a, ga) = split(fa)
        p(a) || G.exists(ga)(p)
    }

    override def forall[A](fa: F[A])(p: A => Boolean): Boolean = {
        val (a, ga) = split(fa)
        p(a) && G.forall(ga)(p)
    }

    override def toList[A](fa: F[A]): List[A] = {
        val (a, ga) = split(fa)
        a :: G.toList(ga)
    }

    override def toNonEmptyList[A](fa: F[A]): NonEmptyList[A] = {
        val (a, ga) = split(fa)
        NonEmptyList(a, G.toList(ga))
    }

    override def filter_[A](fa: F[A])(p: A => Boolean): List[A] = {
        val (a, ga) = split(fa)
        val filteredTail = G.filter_(ga)(p)
        if (p(a)) a :: filteredTail else filteredTail
    }

    override def takeWhile_[A](fa: F[A])(p: A => Boolean): List[A] = {
        val (a, ga) = split(fa)
        if (p(a)) a :: G.takeWhile_(ga)(p) else Nil
    }

    override def dropWhile_[A](fa: F[A])(p: A => Boolean): List[A] = {
        val (a, ga) = split(fa)
        if (p(a)) G.dropWhile_(ga)(p) else a :: G.toList(ga)
    }*/
}
