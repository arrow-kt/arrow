package arrow.data

import arrow.*
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Tuple2
import arrow.typeclasses.Applicative

fun <A> SequenceKWKind<A>.toList(): List<A> = this.reify().sequence.toList()

@higherkind
data class SequenceKW<out A> constructor(val sequence: Sequence<A>) : SequenceKWKind<A>, Sequence<A> by sequence {

    fun <B> flatMap(f: (A) -> SequenceKWKind<B>): SequenceKW<B> = this.reify().sequence.flatMap { f(it).reify().sequence }.k()

    fun <B> ap(ff: SequenceKWKind<(A) -> B>): SequenceKW<B> = ff.reify().flatMap { f -> map(f) }.reify()

    fun <B> map(f: (A) -> B): SequenceKW<B> = this.reify().sequence.map(f).k()

    fun <B> foldLeft(b: B, f: (B, A) -> B): B = this.reify().fold(b, f)

    fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
        fun loop(fa_p: SequenceKW<A>): Eval<B> = when {
            fa_p.sequence.none() -> lb
            else -> f(fa_p.first(), Eval.defer { loop(fa_p.drop(1).k()) })
        }
        return Eval.defer { loop(this.reify()) }
    }

    fun <G, B> traverse(f: (A) -> Kind<G, B>, GA: Applicative<G>): Kind<G, SequenceKW<B>> =
            foldRight(Eval.always { GA.pure(emptySequence<B>().k()) }) { a, eval ->
                GA.map2Eval(f(a), eval) { (sequenceOf(it.a) + it.b).k() }
            }.value()

    fun <B, Z> map2(fb: SequenceKWKind<B>, f: (Tuple2<A, B>) -> Z): SequenceKW<Z> =
            this.reify().flatMap { a ->
                fb.reify().map { b ->
                    f(Tuple2(a, b))
                }
            }.reify()

    companion object {

        fun <A> pure(a: A): SequenceKW<A> = sequenceOf(a).k()

        fun <A> empty(): SequenceKW<A> = emptySequence<A>().k()

        fun <A, B> tailRecM(a: A, f: (A) -> Kind<ForSequenceKW, Either<A, B>>): SequenceKW<B> {
            tailrec fun <A, B> go(
                    buf: MutableList<B>,
                    f: (A) -> Kind<ForSequenceKW, Either<A, B>>,
                    v: SequenceKW<Either<A, B>>) {
                if (!(v.toList().isEmpty())) {
                    val head: Either<A, B> = v.first()
                    when (head) {
                        is Either.Right<A, B> -> {
                            buf += head.b
                            go(buf, f, v.drop(1).k())
                        }
                        is Either.Left<A, B> -> {
                            if (v.count() == 1)
                                go(buf, f, (f(head.a).reify()).k())
                            else
                                go(buf, f, (f(head.a).reify() + v.drop(1)).k())
                        }
                    }
                }
            }

            val buf = mutableListOf<B>()
            go(buf, f, f(a).reify())
            return SequenceKW(buf.asSequence())
        }

    }
}

fun <A> SequenceKW<A>.combineK(y: SequenceKWKind<A>): SequenceKW<A> = (this.sequence + y.reify().sequence).k()

fun <A> Sequence<A>.k(): SequenceKW<A> = SequenceKW(this)
