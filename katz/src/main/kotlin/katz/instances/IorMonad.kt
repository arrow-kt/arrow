package katz

class IorMonad<L>(val SL: Semigroup<L>) : Monad<HK<Ior.F, L>> {
    override fun <A, B> flatMap(fa: IorKind<L, A>, f: (A) -> IorKind<L, B>): Ior<L, B> =
            fa.ev().flatMap(SL, { f(it).ev() })

    override fun <A> pure(a: A): Ior<L, A> = Ior.Right(a)

}

fun <A, B> IorKind<A, B>.ev(): Ior<A, B> = this as Ior<A, B>
