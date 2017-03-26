package katz

class Id<out A>(val value: A): HK<Id.F, A> {

    class F

    inline fun <B> map(f: (A) -> B): Id<B> = Id(f(value))

    inline fun <B> flatMap(f: (A) -> Id<B>): Id<B> = f(value)

    companion object : IdInstances
}

/**
 * Companion interface for [Id] type
 * outside due to https://youtrack.jetbrains.com/issue/KT-10532
 */
private interface IdInstances {
    fun <A> HK<Id.F, A>.ev(): Id<A> = this as Id<A>

    fun monad() = object : Monad<Id.F> {
        override fun <A, B> map(fa: HK<Id.F, A>, f: (A) -> B): HK<Id.F, B> =
                fa.ev().map(f)

        override fun <A> pure(a: A): HK<Id.F, A> = Id(a)

        override fun <A, B> flatMap(fa: HK<Id.F, A>, f: (A) -> HK<Id.F, B>): HK<Id.F, B> =
                fa.ev().flatMap { f(it).ev() }
    }

}
