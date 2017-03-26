package katz

class Id<out A>(val value: A): HK<Id.F, A> {

    class F

    inline fun <B> map(f: (A) -> B): Id<B> = Id(f(value))

    inline fun <B> flatMap(f: (A) -> Id<B>): Id<B> = f(value)

    companion object : IdInstances

    interface IdInstances {
        fun <A> HK<Id.F, A>.ev(): Id<A> = this as Id<A>

        fun monad() = object : Monad<Id.F> {
            override fun <A, B> map(fa: HK<F, A>, f: (A) -> B): HK<F, B> =
                    fa.ev().map(f)

            override fun <A> pure(a: A): HK<F, A> = Id(a)

            override fun <A, B> flatMap(fa: HK<F, A>, f: (A) -> HK<F, B>): HK<F, B> =
                    fa.ev().flatMap { f(it).ev() }
        }

    }
}


