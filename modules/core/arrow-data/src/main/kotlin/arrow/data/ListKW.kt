package arrow.data

import arrow.*
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Option
import arrow.core.Tuple2
import arrow.typeclasses.Applicative

@higherkind
data class ListK<out A> constructor(val list: List<A>) : ListKOf<A>, List<A> by list {

    fun <B> flatMap(f: (A) -> ListKOf<B>): ListK<B> = this.extract().list.flatMap { f(it).extract().list }.k()

    fun <B> map(f: (A) -> B): ListK<B> = this.extract().list.map(f).k()

    fun <B> foldLeft(b: B, f: (B, A) -> B): B = this.extract().fold(b, f)

    fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
        fun loop(fa_p: ListK<A>): Eval<B> = when {
            fa_p.list.isEmpty() -> lb
            else -> f(fa_p.extract().list.first(), Eval.defer { loop(fa_p.list.drop(1).k()) })
        }
        return Eval.defer { loop(this.extract()) }
    }

    fun <B> ap(ff: ListKOf<(A) -> B>): ListK<B> = ff.extract().flatMap { f -> map(f) }.extract()

    fun <G, B> traverse(f: (A) -> Kind<G, B>, GA: Applicative<G>): Kind<G, ListK<B>> =
            foldRight(Eval.always { GA.pure(emptyList<B>().k()) }) { a, eval ->
                GA.map2Eval(f(a), eval) { (listOf(it.a) + it.b).k() }
            }.value()

    fun <B, Z> map2(fb: ListKOf<B>, f: (Tuple2<A, B>) -> Z): ListK<Z> =
            this.extract().flatMap { a ->
                fb.extract().map { b ->
                    f(Tuple2(a, b))
                }
            }.extract()

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
                    is Either.Left<A, B> -> go(buf, f, (f(head.a).extract() + v.drop(1)).k())
                }
            }
        }

        fun <A, B> tailRecM(a: A, f: (A) -> Kind<ForListK, Either<A, B>>): ListK<B> {
            val buf = ArrayList<B>()
            go(buf, f, f(a).extract())
            return ListK(buf)
        }
    }

}

fun <A> ListK<A>.combineK(y: ListKOf<A>): ListK<A> = (this.list + y.extract().list).k()

fun <A> List<A>.k(): ListK<A> = ListK(this)
