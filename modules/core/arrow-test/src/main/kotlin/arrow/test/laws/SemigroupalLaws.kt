package arrow.test.laws

import arrow.Kind
import arrow.core.Tuple2
import arrow.typeclasses.Eq
import arrow.typeclasses.Semigroupal

object SemigroupalLaws {

    fun <F, A, B, C> laws(
            SGAL: Semigroupal<F>,
            af: Kind<F, A>,
            bf: Kind<F, B>,
            cf: Kind<F, C>,
            bijection: (Kind<F, Tuple2<Tuple2<A, B>, C>>) -> (Kind<F, Tuple2<A, Tuple2<B, C>>>),
            associativeEq: Eq<Kind<F, Tuple2<A, Tuple2<B, C>>>>,
            eq: Eq<Kind<F, Tuple2<A,B>>>
    ): List<Law> =
            listOf(
                    Law("Semigroupal: Bijective associativity") { SGAL.semigroupalAssociative(af, bf, cf, bijection, associativeEq) },
                    Law("Semigroupal: Left identity") { SGAL.semigroupalLeftIdentity(bf, eq) },
                    Law("Semigroupal: Right identity") { SGAL.semigroupalRightIdentity(af, eq) }
            )

    private fun <F, A, B, C> Semigroupal<F>.semigroupalAssociative(af: Kind<F, A>, bf: Kind<F, B>, cf: Kind<F, C>, bijection: (Kind<F, Tuple2<Tuple2<A, B>, C>>) -> (Kind<F, Tuple2<A, Tuple2<B, C>>>), EQ: Eq<Kind<F, Tuple2<A, Tuple2<B, C>>>>): Unit =
            af.product(bf.product(cf)).shouldBeEq(bijection(af.product(bf).product(cf)), EQ)

    private fun <F, B, A> Semigroupal<F>.semigroupalLeftIdentity(bf: Kind<F, B>, EQ: Eq<Kind<F, Tuple2<A,B>>>): Unit =
            identity<A>().product(bf).shouldBeEq(identity(), EQ)


    private fun <F, A, B> Semigroupal<F>.semigroupalRightIdentity(af: Kind<F, A>, EQ: Eq<Kind<F, Tuple2<A,B>>>): Unit =
            af.product(identity<B>()).shouldBeEq(identity(), EQ)

}
