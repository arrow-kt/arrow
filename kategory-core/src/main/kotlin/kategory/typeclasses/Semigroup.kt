package kategory

interface Semigroup<A> : Typeclass {
    /**
     * Combine two [A] values.
     */
    fun combine(a: A, b: A): A

    fun maybeCombine(a: A, b: A?): A = Option.fromNullable(b).fold({ a }, { this.combine(a, it) })

    /**
     * Combine an array of [A] values.
     */
    fun combineAll(vararg elems: A): A = combineAll(elems.asList())

    /**
     * Combine a collection of [A] values.
     */
    fun combineAll(elems: Collection<A>): A = elems.reduce { a, b -> combine(a, b) }
}

inline fun <reified A> A.combine(FT: Semigroup<A> = semigroup(), b: A): A = FT.combine(this, b)

inline fun <reified A> Collection<A>.combineAll(FT: Semigroup<A> = semigroup()): A = FT.combineAll(this)

inline fun <reified A> semigroup(): Semigroup<A> = instance(InstanceParametrizedType(Semigroup::class.java, listOf(typeLiteral<A>())))
