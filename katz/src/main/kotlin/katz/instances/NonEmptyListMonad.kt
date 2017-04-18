package katz

interface NonEmptyListMonad : Monad<NonEmptyList.F> {

    override fun <A, B> map(fa: NonEmptyListKind<A>, f: (A) -> B): NonEmptyList<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): NonEmptyList<A> = NonEmptyList.of(a)

    override fun <A, B> flatMap(fa: NonEmptyListKind<A>, f: (A) -> NonEmptyListKind<B>): NonEmptyList<B> =
            fa.ev().flatMap { f(it).ev() }

}

fun <A> NonEmptyListKind<A>.ev(): NonEmptyList<A> = this as NonEmptyList<A>
