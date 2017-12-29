package arrow

import arrow.core.Id
import arrow.core.value

/**
 * Traverse, also known as Traversable. Traversal over a structure with an effect.
 */
interface Traverse<F> : Functor<F>, Foldable<F>, Typeclass {

    /**
     * Given a function which returns a G effect, thread this effect through the running of this function on all the
     * values in F, returning an F<B> in a G context.
     */
    fun <G, A, B> traverse(fa: HK<F, A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, HK<F, B>>

    fun <G, A> sequence(GA: Applicative<G>, fga: HK<F, HK<G, A>>): HK<G, HK<F, A>> = traverse(fga, { it }, GA)

    override fun <A, B> map(fa: HK<F, A>, f: (A) -> B): HK<F, B> =
            traverse(fa, { Id(f(it)) }, arrow.applicative()).value()
}

inline fun <reified F, reified G, A, B> Traverse<F>.flatTraverse(fa: HK<F, A>, noinline f: (A) -> HK<G, HK<F, B>>, GA: Applicative<G> =
applicative(), FM: Monad<F> = monad()): HK<G, HK<F, B>> = GA.map(traverse(fa, f, GA), { FM.flatten(it) })

/**
 * Thread all the G effects through the F structure to invert the structure from F<G<A>> to G<F<A>>.
 */
inline fun <F, reified G, A> Traverse<F>.sequence(fga: HK<F, HK<G, A>>, GA: Applicative<G> = applicative()): HK<G, HK<F, A>> = sequence(GA, fga)

inline fun <reified F> traverse(): Traverse<F> = instance(InstanceParametrizedType(Traverse::class.java, listOf(typeLiteral<F>())))
