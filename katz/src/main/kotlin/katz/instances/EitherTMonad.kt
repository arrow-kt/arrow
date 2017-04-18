package katz

class EitherTMonad<F, L>(val MF : Monad<F>) : Monad<EitherTF<F, L>> {
  
    override fun <A> pure(a: A): EitherT<F, L, A> = EitherT(MF, MF.pure(Either.Right(a)))

    override fun <A, B> flatMap(fa: EitherTKind<F, L, A>, f: (A) -> EitherTKind<F, L, B>): EitherT<F, L, B> =
            fa.ev().flatMap { f(it).ev() }
}

fun <F, A, B> EitherTKind<F, A, B>.ev(): EitherT<F, A, B> = this as EitherT<F, A, B>
