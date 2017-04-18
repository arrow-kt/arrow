package katz

class OptionTMonad<F>(val MF : Monad<F>) : Monad<OptionTF<F>> {
    override fun <A> pure(a: A): OptionT<F, A> = OptionT(MF, MF.pure(Option(a)))

    override fun <A, B> flatMap(fa: OptionTKind<F, A>, f: (A) -> OptionTKind<F, B>): OptionT<F, B> =
            fa.ev().flatMap { f(it).ev() }

    override fun <A, B> map(fa: OptionTKind<F, A>, f: (A) -> B): OptionT<F, B> =
            fa.ev().map(f)
}

fun <F, A> OptionTKind<F, A>.ev(): OptionT<F, A> = this as OptionT<F, A>
