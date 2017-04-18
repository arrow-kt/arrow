package katz

class EitherMonad<L> : Monad<EitherF<L>> {

    override fun <A> pure(a: A): Either<L, A> = Either.Right(a)

    override fun <A, B> flatMap(fa: EitherKind<L, A>, f: (A) -> EitherKind<L, B>): Either<L, B> {
        return fa.ev().flatMap { f(it).ev() }
    }
}

fun <A, B> EitherKind<A, B>.ev(): Either<A, B> = this as Either<A, B>
