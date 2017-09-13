package kategory

import kategory.Option.None
import kategory.Option.Some

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

object OptionSemigroupInstanceImplicits {
    @JvmStatic fun <A> instance(SG: Semigroup<A>): OptionSemigroupInstance<A> = object : OptionSemigroupInstance<A> {
        override fun SG(): Semigroup<A> = SG
    }
}

interface OptionMonoidInstance<A> : OptionSemigroupInstance<A>, Monoid<Option<A>> {
    override fun empty(): Option<A> = Option.None
}

object OptionMonoidInstanceImplicits {
    @JvmStatic fun <A> instance(SG: Semigroup<A>): OptionMonoidInstance<A> = object : OptionMonoidInstance<A> {
        override fun SG(): Semigroup<A> = SG
    }
}

interface OptionMonadErrorInstance : OptionMonadInstance, MonadError<OptionHK, Unit> {
    override fun <A> raiseError(e: Unit): Option<A> = Option.None

    override fun <A> handleErrorWith(fa: OptionKind<A>, f: (Unit) -> OptionKind<A>): Option<A> = fa.ev().orElse({ f(Unit).ev() })
}

object OptionMonadErrorInstanceImplicits {
    @JvmStatic fun instance(): OptionMonadErrorInstance = object : OptionMonadErrorInstance {}
}

