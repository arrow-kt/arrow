package arrow.test.laws

import arrow.Kind
import arrow.core.Tuple2
import arrow.typeclasses.Semigroupal
import io.kotlintest.shouldBe

object SemigroupalLaws {

    fun <F, A, B, C> laws(SGAL: Semigroupal<F>, af: Kind<F, A>, bf: Kind<F, B>, cf: Kind<F, C>, bijection: (Kind<F, Tuple2<Tuple2<A,B>,C>>) -> (Kind<F, Tuple2<A, Tuple2<B, C>>>)): List<Law> =
            listOf(Law("Semigroupal: Bijective associativity") { SGAL.semigroupAssociative(af, bf, cf, bijection) })

    private fun <F, A, B, C> Semigroupal<F>.semigroupAssociative(af: Kind<F, A>, bf: Kind<F, B>, cf: Kind<F, C>, bijection: (Kind<F, Tuple2<Tuple2<A,B>,C>>) -> (Kind<F, Tuple2<A, Tuple2<B, C>>>)): Unit =
            bijection(af.product(bf).product(cf)).shouldBe(af.product(bf.product(cf)))
}
