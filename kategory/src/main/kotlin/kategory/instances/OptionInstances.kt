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

class OptionMonadError : MonadError<OptionHK, Unit> , OptionHKMonadInstance {

    override fun <A> raiseError(e: Unit): Option<A> = Option.None

    override fun <A> handleErrorWith(fa: OptionKind<A>, f: (Unit) -> OptionKind<A>): Option<A> = fa.ev().orElse({ f(Unit).ev() })

}

/**
 * Dummy SemigroupK instance to be able to test laws for SemigroupK.
 */
class OptionSemigroupK : SemigroupK<OptionHK> {
    override fun <A> combineK(x: HK<OptionHK, A>, y: HK<OptionHK, A>): Option<A> = x.ev().flatMap { y.ev() }
}

/**
 * Dummy MonoidK instance to be able to test laws for MonoidK.
 */
class OptionMonoidK : MonoidK<OptionHK>, GlobalInstance<ApplicativeError<OptionHK, Unit>>() {
    override fun <A> combineK(x: HK<OptionHK, A>, y: HK<OptionHK, A>): Option<A> = x.ev().flatMap { y.ev() }

    override fun <A> empty(): HK<OptionHK, A> = Option.None
}

