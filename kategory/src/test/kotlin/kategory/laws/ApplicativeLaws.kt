package kategory

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object ApplicativeLaws {

    inline fun <reified F> laws(A: Applicative<F> = applicative<F>()): List<Law> =
            FunctorLaws.laws(A) + listOf(
                    Law("Applicative Laws: ap identity", { apIdentity(A) }),
                    Law("Applicative Laws: homomorphism", { homomorphism(A) }),
                    Law("Applicative Laws: interchange", { interchange(A) }),
                    Law("Applicative Laws: map derived", { mapDerived(A) })
            )

    inline fun <reified F> apIdentity(A: Applicative<F> = applicative<F>()): Unit =
            forAll(genApplicative(Gen.int(), A), { fa: HK<F, Int> ->
                A.ap(fa, A.pure({ n: Int -> n })) == fa
            })

    inline fun <reified F> homomorphism(A: Applicative<F> = applicative<F>()): Unit =
            forAll(genFunctionAToB<Int, Int>(Gen.int()), Gen.int(), { ab: (Int) -> Int, a: Int ->
                A.ap(A.pure(a), A.pure(ab)) == A.pure(ab(a))
            })

    inline fun <reified F> interchange(A: Applicative<F> = applicative<F>()): Unit =
            forAll(genApplicative(genFunctionAToB<Int, Int>(Gen.int()), A), Gen.int(), { fa: HK<F, (Int) -> Int>, a: Int ->
                A.ap(A.pure(a), fa) == A.ap(fa, A.pure({ x: (Int) -> Int -> x(a) }))
            })

    inline fun <reified F> mapDerived(A: Applicative<F> = applicative<F>()): Unit =
            forAll(genApplicative(Gen.int(), A), genFunctionAToB<Int, Int>(Gen.int()), { fa: HK<F, Int>, f: (Int) -> Int ->
                A.map(fa, f) == A.ap(fa, A.pure(f))
            })

}
