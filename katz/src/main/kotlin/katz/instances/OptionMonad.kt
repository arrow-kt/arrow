package katz

interface OptionMonad : Monad<Option.F> {

    override fun <A, B> map(fa: OptionKind<A>, f: (A) -> B): Option<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): Option<A> = Option.Some(a)

    override fun <A, B> flatMap(fa: OptionKind<A>, f: (A) -> OptionKind<B>): Option<B> =
            fa.ev().flatMap { f(it).ev() }
}

fun <A> OptionKind<A>.ev(): Option<A> = this as Option<A>

