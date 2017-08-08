package kategory

typealias IdKind<A> = HK<Id.F, A>

fun <A> IdKind<A>.ev(): Id<A> = this as Id<A>

fun <A> IdKind<A>.value(): A = this.ev().value

data class Id<out A>(val value: A) : IdKind<A> {

    class F private constructor()

    inline fun <B> map(f: (A) -> B): Id<B> = Id(f(value))

    inline fun <B> flatMap(f: (A) -> Id<B>): Id<B> = f(value)

    companion object : IdInstances, GlobalInstance<Bimonad<Id.F>>() {
        fun functor(): Functor<Id.F> = this

        fun applicative(): Applicative<Id.F> = this

        fun monad(): Monad<Id.F> = this

        fun bimonad(): Bimonad<Id.F> = this

        fun comonad(): Comonad<Id.F> = this

    }

}
