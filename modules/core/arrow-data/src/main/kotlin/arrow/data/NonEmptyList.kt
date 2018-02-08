package arrow.data

import arrow.*
import arrow.core.*
import arrow.typeclasses.Applicative
import arrow.typeclasses.foldable

typealias Nel<A> = NonEmptyList<A>

/**
 * A List that can not be empty
 */
@higherkind
class NonEmptyList<out A> private constructor(
        val head: A,
        val tail: List<A>,
        val all: List<A>) : NonEmptyListOf<A> {

    constructor(head: A, tail: List<A>) : this(head, tail, listOf(head) + tail)
    private constructor(list: List<A>) : this(list[0], list.drop(1), list)

    val size: Int = all.size

    fun contains(element: @UnsafeVariance A): Boolean = (head == element) || element in tail

    fun containsAll(elements: Collection<@UnsafeVariance A>): Boolean = elements.all(this::contains)

    fun isEmpty(): Boolean = false

    fun <B> map(f: (A) -> B): NonEmptyList<B> = NonEmptyList(f(head), tail.map(f))

    fun <B> flatMap(f: (A) -> NonEmptyListOf<B>): NonEmptyList<B> = f(head).extract() + tail.flatMap { f(it).extract().all }

    fun <B> ap(ff: NonEmptyListOf<(A) -> B>): NonEmptyList<B> = ff.extract().flatMap { f -> map(f) }.extract()

    operator fun plus(l: NonEmptyList<@UnsafeVariance A>): NonEmptyList<A> = NonEmptyList(all + l.all)

    operator fun plus(l: List<@UnsafeVariance A>): NonEmptyList<A> = NonEmptyList(all + l)

    operator fun plus(a: @UnsafeVariance A): NonEmptyList<A> = NonEmptyList(all + a)

    fun <B> foldLeft(b: B, f: (B, A) -> B): B = this.extract().tail.fold(f(b, this.extract().head), f)

    fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = foldable<ForListK>().foldRight(this.extract().all.k(), lb, f)

    fun <G, B> traverse(f: (A) -> Kind<G, B>, GA: Applicative<G>): Kind<G, NonEmptyList<B>> =
            GA.map2Eval(f(this.extract().head), Eval.always {
                arrow.typeclasses.traverse<ForListK>().traverse(this.extract().tail.k(), f, GA)
            }, {
                NonEmptyList(it.a, it.b.extract().list)
            }).value()

    fun <B> coflatMap(f: (NonEmptyListOf<A>) -> B): NonEmptyList<B> {
        val buf = mutableListOf<B>()
        tailrec fun consume(list: List<A>): List<B> =
                if (list.isEmpty()) {
                    buf
                } else {
                    val tail = list.subList(1, list.size)
                    buf += f(NonEmptyList(list[0], tail))
                    consume(tail)
                }
        return NonEmptyList(f(this), consume(this.extract().tail))
    }

    fun extract(): A = this.extract().head

    fun iterator(): Iterator<A> = all.iterator()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as NonEmptyList<*>

        if (all != other.all) return false

        return true
    }

    fun show(): String = all.joinToString()

    override fun hashCode(): Int = all.hashCode()

    override fun toString(): String = "NonEmptyList(all=$all)"

    companion object {
        fun <A> of(head: A, vararg t: A): NonEmptyList<A> = NonEmptyList(head, t.asList())
        fun <A> fromList(l: List<A>): Option<NonEmptyList<A>> = if (l.isEmpty()) None else Some(NonEmptyList(l))
        fun <A> fromListUnsafe(l: List<A>): NonEmptyList<A> = NonEmptyList(l)

        fun <A> pure(a: A): NonEmptyList<A> = a.nel()

        @Suppress("UNCHECKED_CAST")
        private tailrec fun <A, B> go(
                buf: ArrayList<B>,
                f: (A) -> Kind<ForNonEmptyList, Either<A, B>>,
                v: NonEmptyList<Either<A, B>>) {
            val head: Either<A, B> = v.head
            when (head) {
                is Either.Right<A, B> -> {
                    buf += head.b
                    val x = fromList(v.tail)
                    when (x) {
                        is Some<NonEmptyList<Either<A, B>>> -> go(buf, f, x.t)
                        is None -> Unit
                    }
                }
                is Either.Left<A, B> -> go(buf, f, f(head.a).extract() + v.tail)
            }
        }

        fun <A, B> tailRecM(a: A, f: (A) -> Kind<ForNonEmptyList, Either<A, B>>): NonEmptyList<B> {
            val buf = ArrayList<B>()
            go(buf, f, f(a).extract())
            return fromListUnsafe(buf)
        }

    }
}

fun <A> A.nel(): NonEmptyList<A> = NonEmptyList.of(this)

fun <A> NonEmptyList<A>.combineK(y: NonEmptyListOf<A>): NonEmptyList<A> = this.plus(y.extract())
