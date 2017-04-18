package katz

interface OptionMonad : Monad<Option.F> {

    override fun <A, B> map(fa: OptionKind<A>, f: (A) -> B): Option<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): Option<A> = Option.Some(a)

    override fun <A, B> flatMap(fa: OptionKind<A>, f: (A) -> OptionKind<B>): Option<B> =
            fa.ev().flatMap { f(it).ev() }

    tailrec override fun <A, B> tailRecM(a: A, f: (A) -> HK<Option.F, Either<A, B>>): Option<B> {
        val option = f(a).ev()
        return when(option) {
            is Option.Some -> {
                when (option.value) {
                    is Either.Left -> tailRecM(option.value.a, f)
                    is Either.Right -> Option.Some(option.value.b)
                }
            }
            is Option.None -> Option.None
        }
    }
}

fun <A> OptionKind<A>.ev(): Option<A> = this as Option<A>
