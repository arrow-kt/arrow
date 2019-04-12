package arrow.test.laws

import arrow.Kind
import arrow.test.generators.applicative
import arrow.test.generators.functionAToB
import arrow.test.generators.intSmall
import arrow.typeclasses.Applicative
import arrow.typeclasses.Apply
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object ApplyLaws {

    fun <F, A> laws(AP: Applicative<F>, gen: Gen<A>, EQ: Eq<Kind<F, A>>): List<Law> =
            FunctorLaws.laws(AP, Eq.any()) + listOf(
                    Law("Apply Laws: ap identity") { AP.apIdentity(AP, gen, EQ) },
                    Law("Apply Laws: ap homomorphism") { AP.homomorphism(AP, gen, EQ) },
                    Law("Apply Laws: interchange") { AP.interchange(AP, gen, EQ) },
                    Law("Apply Laws: map derived") { AP.mapDerived(AP, gen, EQ) }
            )

    fun <F, A> Apply<F>.apIdentity(AP: Applicative<F>, gen: Gen<A>, EQ: Eq<Kind<F, A>>): Unit =
            forAll(gen.applicative(AP)) { fa ->
                fa.ap(AP.just({ n: A -> n })).equalUnderTheLaw(fa, EQ)
            }

    fun <F, A> Apply<F>.homomorphism(AP: Applicative<F>, gen: Gen<A>, EQ: Eq<Kind<F, A>>): Unit =
            forAll(Gen.functionAToB<A, A>(gen), gen) { ab: (A) -> A, a: A ->
                AP.just(a).ap(AP.just(ab)).equalUnderTheLaw(AP.just(ab(a)), EQ)
            }


    fun <F, A> Apply<F>.interchange(AP: Applicative<F>, gen: Gen<A>, EQ: Eq<Kind<F, A>>): Unit =
            forAll(Gen.functionAToB<A, A>(gen).applicative(AP), gen) { fa: Kind<F, (A) -> A>, a: A ->
                AP.just(a).ap(fa).equalUnderTheLaw(fa.ap(AP.just({ x: (A) -> A -> x(a) })), EQ)
            }

    fun <F, A> Apply<F>.mapDerived(AP: Applicative<F>, gen: Gen<A>, EQ: Eq<Kind<F, A>>): Unit =
            forAll(gen.applicative(AP), Gen.functionAToB<A, A>(gen)) { fa: Kind<F, A>, f: (A) -> A ->
                fa.map(f).equalUnderTheLaw(fa.ap(AP.just(f)), EQ)
            }

}

object ApplyCartesianLaws {

    fun <F> laws(AP: Applicative<F>, EQ: Eq<Kind<F, Int>>): List<Law> =
            FunctorLaws.laws(AP, Eq.any()) + listOf(
                    Law("Apply Laws: cartesian builder map") { AP.cartesianBuilderMap(AP, EQ) },
                    Law("Apply Laws: cartesian builder tupled") { AP.cartesianBuilderTupled(AP, EQ) }
            )

    fun <F> Apply<F>.cartesianBuilderMap(AP: Applicative<F>, EQ: Eq<Kind<F, Int>>): Unit =
            forAll(Gen.intSmall(), Gen.intSmall(), Gen.intSmall(), Gen.intSmall(), Gen.intSmall(), Gen.intSmall()) { a: Int, b: Int, c: Int, d: Int, e: Int, f: Int ->
                map(AP.just(a), AP.just(b), AP.just(c), AP.just(d), AP.just(e), AP.just(f)) { (x, y, z, u, v, w) -> x + y + z - u - v - w }.equalUnderTheLaw(AP.just(a + b + c - d - e - f), EQ)
            }

    fun <F> Apply<F>.cartesianBuilderTupled(AP: Applicative<F>, EQ: Eq<Kind<F, Int>>): Unit =
            forAll(Gen.intSmall(), Gen.intSmall(), Gen.intSmall(), Gen.intSmall(), Gen.intSmall(), Gen.intSmall()) { a: Int, b: Int, c: Int, d: Int, e: Int, f: Int ->
                tupled(AP.just(a), AP.just(b), AP.just(c), AP.just(d), AP.just(e), AP.just(f)).map { (x, y, z, u, v, w) -> x + y + z - u - v - w }.equalUnderTheLaw(AP.just(a + b + c - d - e - f), EQ)
            }
}
