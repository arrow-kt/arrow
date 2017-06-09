package katz

typealias NonEmptyVectorKind<A> = HK<NonEmptyVector.F, A>

/**
 * A Vector that can not be empty
 */
class NonEmptyVector<A> private constructor(
        val head: A,
        val tail: Array<A>,
        val array: Array<A>) : NonEmptyVectorKind<A> {

    class F private constructor()

    constructor(head: A, tail: Array<A>) : this(head, tail, tail.copyOfRange(0, 0).plus(head).plus(tail))
    private constructor(array: Array<A>) : this(array[0], array.copyOfRange(0, array.size), array)

    val size: Int = array.size

    fun contains(element: @UnsafeVariance A): Boolean =
            array.contains(element)

    fun containsAll(elements: Collection<@UnsafeVariance A>): Boolean =
            elements.all(array::contains)

    fun isEmpty(): Boolean = false

    fun <B> map(f: (A) -> B): NonEmptyVector<B> = TODO()

    fun <B> flatMap(f: (A) -> NonEmptyVector<B>): NonEmptyVector<B> = TODO()

    operator fun plus(l: NonEmptyVector<@UnsafeVariance A>): NonEmptyVector<A> = NonEmptyVector(array + l.array)

    operator fun plus(l: Array<@UnsafeVariance A>): NonEmptyVector<A> = NonEmptyVector(array + l)

    operator fun plus(a: @UnsafeVariance A): NonEmptyVector<A> = NonEmptyVector(array + a)

    fun iterator(): Iterator<A> = array.iterator()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as NonEmptyVector<*>
        if (array != other.array) return false

        return true
    }

    override fun hashCode(): Int =
            array.hashCode()

    override fun toString(): String =
            "NonEmptyVector(array=$array)"

    companion object : NonEmptyVectorBimonad, GlobalInstance<Bimonad<NonEmptyVector.F>>() {
        @JvmStatic inline fun <reified A> of(head: A, vararg t: A): NonEmptyVector<A> = NonEmptyVector(head, arrayOf(*t))
        @JvmStatic fun <A> fromArray(v: Array<A>): Option<NonEmptyVector<A>> = if (v.isEmpty()) Option.None else Option.Some(NonEmptyVector(v))
        @JvmStatic fun <A> fromArrayUnsafe(v: Array<A>): NonEmptyVector<A> = NonEmptyVector(v)
    }

}