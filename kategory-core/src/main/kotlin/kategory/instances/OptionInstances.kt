package kategory

import kategory.Option.None
import kategory.Option.Some

@instance(Option::class)
interface OptionSemigroupInstance<A> : Semigroup<Option<A>> {

    fun SG(): Semigroup<A>

    override fun combine(a: Option<A>, b: Option<A>): Option<A> =
            when (a) {
                is Some<A> -> when (b) {
                    is Some<A> -> Some(SG().combine(a.value, b.value))
                    is None -> b
                }
                is None -> a
            }
}

@instance(Option::class)
interface OptionMonoidInstance<A> : OptionSemigroupInstance<A>, Monoid<Option<A>> {
    override fun empty(): Option<A> = Option.None
}

@instance(Option::class)
interface OptionMonadErrorInstance : OptionMonadInstance, MonadError<OptionHK, Unit> {
    override fun <A> raiseError(e: Unit): Option<A> = Option.None

    override fun <A> handleErrorWith(fa: OptionKind<A>, f: (Unit) -> OptionKind<A>): Option<A> = fa.ev().orElse({ f(Unit).ev() })
}
