package arrow.data

import arrow.Kind
import arrow.core.*
import arrow.higherkind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Traverse

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

    fun <B> flatMap(f: (A) -> NonEmptyListOf<B>): NonEmptyList<B> = f(head).fix() + tail.flatMap { f(it).fix().all }

    fun <B> ap(ff: NonEmptyListOf<(A) -> B>): NonEmptyList<B> = ff.fix().flatMap { f -> map(f) }.fix()

    operator fun plus(l: NonEmptyList<@UnsafeVariance A>): NonEmptyList<A> = NonEmptyList(all + l.all)

    operator fun plus(l: List<@UnsafeVariance A>): NonEmptyList<A> = NonEmptyList(all + l)

    operator fun plus(a: @UnsafeVariance A): NonEmptyList<A> = NonEmptyList(all + a)

    fun <B> foldLeft(b: B, f: (B, A) -> B): B = this.fix().tail.fold(f(b, this.fix().head), f)

    fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            ListKTraverse.foldRight(this.fix().all.k(), lb, f)

    fun <G, B> traverse(AG: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, NonEmptyList<B>> = with (AG) {
            map2Eval(f(fix().head), Eval.always {
                ListKTraverse.run { traverse(fix().tail.k(), f) }
            }, {
                NonEmptyList(it.a, it.b.fix().list)
            }).value()
    }

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
        return NonEmptyList(f(this), consume(this.fix().tail))
    }

    fun extract(): A = this.fix().head

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
                is Either.Left<A, B> -> go(buf, f, f(head.a).fix() + v.tail)
            }
        }

        fun <A, B> tailRecM(a: A, f: (A) -> Kind<ForNonEmptyList, Either<A, B>>): NonEmptyList<B> {
            val buf = ArrayList<B>()
            go(buf, f, f(a).fix())
            return fromListUnsafe(buf)
        }

    }
}

fun <A> A.nel(): NonEmptyList<A> = NonEmptyList.of(this)

fun <A> NonEmptyList<A>.combineK(y: NonEmptyListOf<A>): NonEmptyList<A> = this.plus(y.fix())

private val ListKTraverse: Traverse<ForListK> = object : Traverse<ForListK> {
    override fun <A, B> map(fa: ListKOf<A>, f: kotlin.Function1<A, B>): ListK<B> =
            fa.fix().map(f)

    override fun <G, A, B> Applicative<G>.traverse(fa: ListKOf<A>, f: kotlin.Function1<A, Kind<G, B>>): Kind<G, ListK<B>> =
            fa.fix().traverse(f, this)

    override fun <A, B> foldLeft(fa: ListKOf<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.fix().foldLeft(b, f)

    override fun <A, B> foldRight(fa: ListKOf<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.fix().foldRight(lb, f)

    override fun <A> isEmpty(fa: ListKOf<A>): kotlin.Boolean =
            fa.fix().isEmpty()
}
