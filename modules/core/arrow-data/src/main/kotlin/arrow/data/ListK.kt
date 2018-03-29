package arrow.data

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Option
import arrow.core.Tuple2
import arrow.higherkind
import arrow.typeclasses.Applicative

@higherkind
data class ListK<out A> constructor(val list: List<A>) : ListKOf<A>, List<A> by list {

    fun <B> flatMap(f: (A) -> ListKOf<B>): ListK<B> = this.fix().list.flatMap { f(it).fix().list }.k()

    fun <B> map(f: (A) -> B): ListK<B> = this.fix().list.map(f).k()

    fun <B> foldLeft(b: B, f: (B, A) -> B): B = this.fix().fold(b, f)

    fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
        fun loop(fa_p: ListK<A>): Eval<B> = when {
            fa_p.list.isEmpty() -> lb
            else -> f(fa_p.fix().list.first(), Eval.defer { loop(fa_p.list.drop(1).k()) })
        }
        return Eval.defer { loop(this.fix()) }
    }

    fun <B> ap(ff: ListKOf<(A) -> B>): ListK<B> = ff.fix().flatMap { f -> map(f) }.fix()

    fun <G, B> traverse(GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, ListK<B>> = GA.run {
        foldRight(Eval.always { pure(emptyList<B>().k()) }) { a, eval ->
            f(a).map2Eval(eval) { (listOf(it.a) + it.b).k() }
        }.value()
    }

    fun <B, Z> map2(fb: ListKOf<B>, f: (Tuple2<A, B>) -> Z): ListK<Z> =
            this.fix().flatMap { a ->
                fb.fix().map { b ->
                    f(Tuple2(a, b))
                }
            }.fix()

    fun <B> mapFilter(f: (A) -> Option<B>): ListK<B> =
            flatMap({ a -> f(a).fold({ empty<B>() }, { pure(it) }) })

    companion object {

        fun <A> pure(a: A): ListK<A> = listOf(a).k()

        fun <A> empty(): ListK<A> = emptyList<A>().k()

        @Suppress("UNCHECKED_CAST")
        private tailrec fun <A, B> go(
                buf: ArrayList<B>,
                f: (A) -> Kind<ForListK, Either<A, B>>,
                v: ListK<Either<A, B>>) {
            if (!v.isEmpty()) {
                val head: Either<A, B> = v.first()
                when (head) {
                    is Either.Right<A, B> -> {
                        buf += head.b
                        go(buf, f, v.drop(1).k())
                    }
                    is Either.Left<A, B> -> go(buf, f, (f(head.a).fix() + v.drop(1)).k())
                }
            }
        }

        fun <A, B> tailRecM(a: A, f: (A) -> Kind<ForListK, Either<A, B>>): ListK<B> {
            val buf = ArrayList<B>()
            go(buf, f, f(a).fix())
            return ListK(buf)
        }
    }

}

fun <A> ListKOf<A>.combineK(y: ListKOf<A>): ListK<A> =
        (fix().list + y.fix().list).k()

fun <A> List<A>.k(): ListK<A> = ListK(this)
