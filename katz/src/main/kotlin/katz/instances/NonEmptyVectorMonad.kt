package katz

import java.util.Vector

interface NonEmptyVectorMonad : Monad<NonEmptyVector.F> {
    override fun <A> pure(a: A): HK<NonEmptyVector.F, A> = NonEmptyVector.of(a)

    override fun <A, B> flatMap(fa: HK<NonEmptyVector.F, A>, f: (A) -> HK<NonEmptyVector.F, B>): HK<NonEmptyVector.F, B> =
            fa.ev().flatMap { f(it).ev() }

    @Suppress("UNCHECKED_CAST")
    private tailrec fun <A, B> go(buf: Vector<B>, f: (A) -> HK<NonEmptyVector.F, Either<A, B>>, v: NonEmptyVector<Either<A, B>>): Unit =
            when (v.head) {
                is Either.Right<*> -> {
                    buf += v.head.b as B
                    val x = NonEmptyVector.fromVector(v.tail)
                    when (x) {
                        is Option.Some<NonEmptyVector<Either<A, B>>> -> go(buf, f, x.value)
                        is Option.None -> Unit
                    }
                }
                is Either.Left<*> -> go(buf, f, f(v.head.a as A).ev() + v.tail)
            }

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<NonEmptyVector.F, Either<A, B>>): NonEmptyVector<B> {
        val buf = Vector<B>()
        go(buf, f, f(a).ev())
        return NonEmptyVector.fromVectorUnsafe(buf)
    }
}

fun <A> NonEmptyVectorKind<A>.ev(): NonEmptyVector<A> = this as NonEmptyVector<A>