package arrow.data

import arrow.HK
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Option
import arrow.core.Tuple2
import arrow.higherkind
import arrow.typeclasses.Applicative

@higherkind
class ListA<A>(val value: List<A>): ArrayList<A>(value), ListAKind<A> {
    fun <B> flatMap(f: (A) -> ListAKind<B>): ListA<B> = this.ev().flatMap { f(it).ev() }

    fun <B> map(f: (A) -> B): ListA<B> = this.ev().map(f)

    fun <B> foldLeft(b: B, f: (B, A) -> B): B = this.ev().fold(b, f)

    fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
        fun loop(fa_p: ListA<A>): Eval<B> = when {
            fa_p.isEmpty() -> lb
            else -> f(fa_p.ev().first(), Eval.defer { loop(fa_p.drop(1).f()) })
        }
        return Eval.defer { loop(this.ev()) }
    }

    fun <B> ap(ff: ListAKind<(A) -> B>): ListA<B> = ff.ev().flatMap { f -> map(f) }.ev()

    fun <G, B> traverse(f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, ListA<B>> =
            foldRight(Eval.always { GA.pure(emptyList<B>().f()) }) { a, eval ->
                GA.map2Eval(f(a), eval) { (listOf(it.a) + it.b).f() }
            }.value()

    fun <B, Z> map2(fb: ListAKind<B>, f: (Tuple2<A, B>) -> Z): ListA<Z> =
            this.ev().flatMap { a ->
                fb.ev().map { b ->
                    f(Tuple2(a, b))
                }
            }.ev()

    fun <B> mapFilter(f: (A) -> Option<B>): ListA<B> =
            flatMap({ a -> f(a).fold({ empty<B>() }, { pure(it) }) })

    companion object {

        fun <A> pure(a: A): ListA<A> = listOf(a).f()

        fun <A> empty(): ListA<A> = emptyList<A>().f()

        @Suppress("UNCHECKED_CAST")
        private tailrec fun <A, B> go(
                buf: ArrayList<B>,
                f: (A) -> HK<ListAHK, Either<A, B>>,
                v: ListA<Either<A, B>>) {
            if (!v.isEmpty()) {
                val head: Either<A, B> = v.first()
                when (head) {
                    is Either.Right<A, B> -> {
                        buf += head.b
                        go(buf, f, v.drop(1).f())
                    }
                    is Either.Left<A, B> -> go(buf, f, (f(head.a).ev() + v.drop(1)).f())
                }
            }
        }

        fun <A, B> tailRecM(a: A, f: (A) -> HK<ListAHK, Either<A, B>>): ListA<B> {
            val buf = ListA<B>(emptyList())
            go(buf, f, f(a).ev())
            return buf
        }
    }

}

fun <A> ListA<A>.combineK(y: ListAKind<A>): ListA<A> = (this + y.ev()).f()

fun <A> List<A>.f(): ListA<A> = ListA(this).f()

