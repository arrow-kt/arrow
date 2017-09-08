package kategory

interface OptionMonoid<A> : Monoid<Option<A>> {

    fun SG(): Semigroup<A>

    override fun empty(): Option<A> = Option.None

    override fun combine(a: Option<A>, b: Option<A>): Option<A> =
            when (a) {
                is Option.Some<A> -> when (b) {
                    is Option.Some<A> -> Option.Some(SG().combine(a.value, b.value))
                    is Option.None -> b
                }
                is Option.None -> a
            }

}

data class OptionMonadError<E>(val error: E) : MonadError<OptionHK, E> , OptionMonadInstance {

    override fun <A> raiseError(e: E): Option<A> = Option.None

    override fun <A> handleErrorWith(fa: OptionKind<A>, f: (E) -> OptionKind<A>): Option<A> = fa.ev().orElse({ f(error).ev() })

}

