package arrow.typeclasses

import arrow.*
import arrow.core.*

/**
 * Traverse, also known as Traversable. Traversal over a structure with an effect.
 */
@typeclass
interface Traverse<F> : Functor<F>, Foldable<F>, TC {

    /**
     * Given a function which returns a G effect, thread this effect through the running of this function on all the
     * values in F, returning an F<B> in a G context.
     */
    fun <G, A, B> traverse(fa: Kind<F, A>, f: (A) -> Kind<G, B>, GA: Applicative<G>): Kind<G, Kind<F, B>>

    fun <G, A> sequence(GA: Applicative<G>, fga: Kind<F, Kind<G, A>>): Kind<G, Kind<F, A>> = traverse(fga, { it }, GA)

    override fun <A, B> map(fa: Kind<F, A>, f: (A) -> B): Kind<F, B> =
            traverse(fa, { Id(f(it)) }, applicative()).value()
}

inline fun <reified F, reified G, A, B> Traverse<F>.flatTraverse(fa: Kind<F, A>, noinline f: (A) -> Kind<G, Kind<F, B>>, GA: Applicative<G> =
applicative(), FM: Monad<F> = monad()): Kind<G, Kind<F, B>> = GA.map(traverse(fa, f, GA), { FM.flatten(it) })

/**
 * Thread all the G effects through the F structure to invert the structure from F<G<A>> to G<F<A>>.
 */
inline fun <F, reified G, A> Traverse<F>.sequence(fga: Kind<F, Kind<G, A>>, GA: Applicative<G> = applicative()): Kind<G, Kind<F, A>> = sequence(GA, fga)