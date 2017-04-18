package katz

interface IdMonad : Monad<Id.F> {

    override fun <A, B> map(fa: IdKind<A>, f: (A) -> B): Id<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): Id<A> = Id(a)

    override fun <A, B> flatMap(fa: IdKind<A>, f: (A) -> IdKind<B>): Id<B> =
            fa.ev().flatMap { f(it).ev() }
}
