package kategory

fun <A> IdKind<A>.value(): A = this.ev().value

@higherkind data class Id<out A>(val value: A) : IdKind<A> {

    inline fun <B> map(f: (A) -> B): Id<B> = Id(f(value))

    inline fun <B> flatMap(f: (A) -> Id<B>): Id<B> = f(value)

    companion object : IdInstances, GlobalInstance<Bimonad<IdHK>>() {
        fun functor(): Functor<IdHK> = this

        fun applicative(): Applicative<IdHK> = this

        fun monad(): Monad<IdHK> = this

        fun bimonad(): Bimonad<IdHK> = this

        fun comonad(): Comonad<IdHK> = this

    }

}