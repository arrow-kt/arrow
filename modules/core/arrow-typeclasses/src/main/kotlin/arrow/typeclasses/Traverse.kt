package arrow.typeclasses

import arrow.Kind
import arrow.core.Id
import arrow.core.value
import arrow.typeclasses.internal.IdMonad

/**
 * Traverse, also known as Traversable. Traversal over a structure with an effect.
 */
interface Traverse<F> : Functor<F>, Foldable<F> {

    /**
     * Given a function which returns a G effect, thread this effect through the running of this function on all the
     * values in F, returning an F<B> in a G context.
     */
    fun <G, A, B> Applicative<G>.traverse(fa: Kind<F, A>, f: (A) -> Kind<G, B>): Kind<G, Kind<F, B>>

    /**
     * Thread all the G effects through the F structure to invert the structure from F<G<A>> to G<F<A>>.
     */
    fun <G, A> Applicative<G>.sequence(fga: Kind<F, Kind<G, A>>): Kind<G, Kind<F, A>> = traverse(fga) { it }

    override fun <A, B> map(fa: Kind<F, A>, f: (A) -> B): Kind<F, B> =
            IdMonad.traverse(fa, { Id(f(it)) }).value()

    fun <G, A, B> FlatTraverse<F, G>.flatTraverse(fa: Kind<F, A>, f: (A) -> Kind<G, Kind<F, B>>): Kind<G, Kind<F, B>> =
            AG().run { map(traverse(fa, f)) { MF().run { it.flatten() } } }
}

interface FlatTraverse<F, G> {
    fun MF(): Monad<F>

    fun AG(): Applicative<G>
}
