package katz

class OptionTMonad<F>(val MF: Monad<F>) : Monad<OptionTF<F>> {
    override fun <A> pure(a: A): OptionT<F, A> = OptionT(MF, MF.pure(Option(a)))

    override fun <A, B> flatMap(fa: OptionTKind<F, A>, f: (A) -> OptionTKind<F, B>): OptionT<F, B> =
            fa.ev().flatMap { f(it).ev() }

    override fun <A, B> map(fa: OptionTKind<F, A>, f: (A) -> B): OptionT<F, B> =
            fa.ev().map(f)

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<OptionTF<F>, Either<A, B>>): OptionT<F, B> =
            OptionT(MF, MF.tailRecM(a, {
                MF.map(f(it).ev().value, {
                    it.fold({
                        Either.Right<Option<B>>(Option.None)
                    }, {
                        it.map { Option.Some(it) }
                    })
                })
            }))
}

fun <F, A> OptionTKind<F, A>.ev(): OptionT<F, A> = this as OptionT<F, A>
