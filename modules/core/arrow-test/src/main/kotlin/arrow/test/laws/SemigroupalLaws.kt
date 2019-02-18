package arrow.test.laws

import arrow.Kind
import arrow.core.Tuple2
import arrow.typeclasses.Eq
import arrow.typeclasses.Semigroupal
import io.kotlintest.shouldBe

object SemigroupalLaws {

    fun <F, A, B, C> laws(SGAL: Semigroupal<F>, af: Kind<F, A>, bf: Kind<F, B>, cf: Kind<F, C>, bijection: (Kind<F, Tuple2<Tuple2<A, B>, C>>) -> (Kind<F, Tuple2<A, Tuple2<B, C>>>), EQ: Eq<Kind<F, Tuple2<A, Tuple2<B, C>>>>): List<Law> =
            listOf(
                    Law("Semigroupal: Bijective associativity") { SGAL.semigroupalAssociative(af, bf, cf, bijection, EQ) },
                    Law("Semigroupal: Left identity") { SGAL.semigroupalLeftIdentity(bf) },
                    Law("Semigroupal: Right identity") { SGAL.semigroupalRightIdentity(af) }
            )

    private fun <F, A, B, C> Semigroupal<F>.semigroupalAssociative(af: Kind<F, A>, bf: Kind<F, B>, cf: Kind<F, C>, bijection: (Kind<F, Tuple2<Tuple2<A, B>, C>>) -> (Kind<F, Tuple2<A, Tuple2<B, C>>>), EQ: Eq<Kind<F, Tuple2<A, Tuple2<B, C>>>>): Unit =
            af.product(bf.product(cf)).shouldBeEq(bijection(af.product(bf).product(cf)), EQ)

    private fun <F, B> Semigroupal<F>.semigroupalLeftIdentity(bf: Kind<F, B>): Unit =
            identity<B>().product(bf).shouldBe(identity<B>())


    private fun <F, A> Semigroupal<F>.semigroupalRightIdentity(af: Kind<F, A>): Unit =
            af.product(identity<A>()).shouldBe(identity<A>())

}
