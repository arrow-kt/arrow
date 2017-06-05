package katz

class EitherMonad<L> : Monad<EitherF<L>> {

    override fun <A> pure(a: A): Either<L, A> = Either.Right(a)

    override fun <A, B> flatMap(fa: EitherKind<L, A>, f: (A) -> EitherKind<L, B>): Either<L, B> =
            fa.ev().flatMap { f(it).ev() }

    tailrec override fun <A, B> tailRecM(a: A, f: (A) -> HK<EitherF<L>, Either<A, B>>): Either<L, B> {
        val e = f(a).ev().ev()
        return when (e) {
            is Either.Left -> e
            is Either.Right -> when (e.b) {
                is Either.Left -> tailRecM(e.b.a, f)
                is Either.Right -> e.b
            }
        }
    }
}