package arrow.test.laws

import arrow.*
import arrow.syntax.applicative.map
import arrow.syntax.applicative.tupled
import arrow.test.generators.genApplicative
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genIntSmall
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.applicative
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object ApplicativeLaws {

    inline fun <reified F> laws(A: Applicative<F> = applicative<F>(), EQ: Eq<Kind<F, Int>>): List<Law> =
            FunctorLaws.laws(A, EQ) + listOf(
                    Law("Applicative Laws: ap identity", { apIdentity(A, EQ) }),
                    Law("Applicative Laws: homomorphism", { homomorphism(A, EQ) }),
                    Law("Applicative Laws: interchange", { interchange(A, EQ) }),
                    Law("Applicative Laws: map derived", { mapDerived(A, EQ) }),
                    Law("Applicative Laws: cartesian builder map", { cartesianBuilderMap(A, EQ) }),
                    Law("Applicative Laws: cartesian builder tupled", { cartesianBuilderTupled(A, EQ) })
            )

    inline fun <reified F> apIdentity(A: Applicative<F> = applicative<F>(), EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genApplicative(Gen.int(), A), { fa: Kind<F, Int> ->
                A.ap(fa, A.pure({ n: Int -> n })).equalUnderTheLaw(fa, EQ)
            })

    inline fun <reified F> homomorphism(A: Applicative<F> = applicative<F>(), EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genFunctionAToB<Int, Int>(Gen.int()), Gen.int(), { ab: (Int) -> Int, a: Int ->
                A.ap(A.pure(a), A.pure(ab)).equalUnderTheLaw(A.pure(ab(a)), EQ)
            })

    inline fun <reified F> interchange(A: Applicative<F> = applicative<F>(), EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genApplicative(genFunctionAToB<Int, Int>(Gen.int()), A), Gen.int(), { fa: Kind<F, (Int) -> Int>, a: Int ->
                A.ap(A.pure(a), fa).equalUnderTheLaw(A.ap(fa, A.pure({ x: (Int) -> Int -> x(a) })), EQ)
            })

    inline fun <reified F> mapDerived(A: Applicative<F> = applicative<F>(), EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genApplicative(Gen.int(), A), genFunctionAToB<Int, Int>(Gen.int()), { fa: Kind<F, Int>, f: (Int) -> Int ->
                A.map(fa, f).equalUnderTheLaw(A.ap(fa, A.pure(f)), EQ)
            })

    inline fun <reified F> cartesianBuilderMap(A: Applicative<F> = applicative<F>(), EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genIntSmall(), genIntSmall(), genIntSmall(), genIntSmall(), genIntSmall(), genIntSmall(), { a: Int, b: Int, c: Int, d: Int, e: Int, f: Int ->
                A.map(A.pure(a), A.pure(b), A.pure(c), A.pure(d), A.pure(e), A.pure(f), { (x, y, z, u, v, w) -> x + y + z - u - v - w }).equalUnderTheLaw(A.pure(a + b + c - d - e - f), EQ)
            })

    inline fun <reified F> cartesianBuilderTupled(A: Applicative<F> = applicative<F>(), EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genIntSmall(), genIntSmall(), genIntSmall(), genIntSmall(), genIntSmall(), genIntSmall(), { a: Int, b: Int, c: Int, d: Int, e: Int, f: Int ->
                A.map(A.tupled(A.pure(a), A.pure(b), A.pure(c), A.pure(d), A.pure(e), A.pure(f)), { (x, y, z, u, v, w) -> x + y + z - u - v - w }).equalUnderTheLaw(A.pure(a + b + c - d - e - f), EQ)
            })
}
