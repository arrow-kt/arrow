package katz

import java.util.Vector

typealias NonEmptyVectorKind<A> = HK<NonEmptyVector.F, A>

/**
 * A Vector that can not be empty
 */
class NonEmptyVector<A> private constructor(
        val head: A,
        val tail: Vector<A>,
        val toVector: Vector<A>) : NonEmptyVectorKind<A> {

    class F private constructor()

    constructor(head: A, tail: Vector<A>) : this(head, tail, Vector<A>().apply {
        add(head)
        addAll(tail)
    })

    private constructor(list: Vector<A>) : this(list[0], Vector<A>(list.drop(1)), list)

    val size: Int = toVector.size

    fun contains(element: @UnsafeVariance A): Boolean =
            toVector.contains(element)

    fun containsAll(elements: Collection<@UnsafeVariance A>): Boolean =
            elements.all(toVector::contains)

    fun isEmpty(): Boolean = false

    fun <B> map(f: (A) -> B): NonEmptyVector<B> =
            NonEmptyVector(toVector.mapTo(Vector<B>(toVector.capacity()), f))

    fun <B> flatMap(f: (A) -> NonEmptyVector<B>): NonEmptyVector<B> =
            NonEmptyVector(toVector.flatMapTo(Vector<B>(toVector.capacity()), { f.invoke(it).toVector }))

    operator fun plus(l: NonEmptyVector<@UnsafeVariance A>): NonEmptyVector<A> = NonEmptyVector(toVector.apply { addAll(l.toVector) })

    operator fun plus(l: Vector<@UnsafeVariance A>): NonEmptyVector<A> = NonEmptyVector(toVector.apply { addAll(l) })

    operator fun plus(a: @UnsafeVariance A): NonEmptyVector<A> = NonEmptyVector(toVector.apply { add(a) })

    fun iterator(): Iterator<A> = toVector.iterator()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as NonEmptyVector<*>
        if (toVector != other.toVector) return false

        return true
    }

    override fun hashCode(): Int =
            toVector.hashCode()

    override fun toString(): String =
            "NonEmptyVector(toVector=$toVector)"

    companion object : NonEmptyVectorBimonad, GlobalInstance<Bimonad<NonEmptyVector.F>>() {
        @JvmStatic fun <A> of(head: A, vararg t: A): NonEmptyVector<A> = NonEmptyVector(head, Vector<A>(t.asList()))
        @JvmStatic fun <A> fromVector(v: Vector<A>): Option<NonEmptyVector<A>> = if (v.isEmpty()) Option.None else Option.Some(NonEmptyVector(v))
        @JvmStatic fun <A> fromVectorUnsafe(v: Vector<A>): NonEmptyVector<A> = NonEmptyVector(v)
    }

}