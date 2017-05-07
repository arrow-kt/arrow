package katz

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class GlobalInstancesTest : UnitSpec() {

    inline fun <reified F> testTypeclassHierarchyInference(a: Any) {
        functor<F>() shouldBe a
        applicative<F>() shouldBe a
        monad<F>() shouldBe a
    }

    init {

        "Id inference" {
            testTypeclassHierarchyInference<Id.F>(Id)
        }

        "NonEmptyList monad inference" {
            testTypeclassHierarchyInference<NonEmptyList.F>(NonEmptyList)
        }

        "Option monad inference" {
            testTypeclassHierarchyInference<Option.F>(Option)
        }

        "EitherTMonad#tailRecM should execute and terminate without blowing up the stack" {
            putThingy<EitherT.F, Id.F> { EitherTMonad<Id.F, Nothing>(monad()) }
            val ev = monad1<EitherT.F, Id.F, Int>().ev()

            forAll(Gen.oneOf(listOf(10000))) { limit: Int ->
                val value: EitherT<Id.F, Int, Int> = ev.tailRecM(0) { current ->
                    if (current == limit)
                        EitherT.left<Id.F, Int, Either<Int, Int>>(current)
                    else
                        EitherT.pure<Id.F, Int, Either<Int, Int>>(Either.Left(current + 1))
                }
                val expected = EitherT.left<Id.F, Int, Int>(limit)

                expected == value
            }
        }
    }
}

inline fun <reified F, reified G> putThingy(noinline function: () -> Monad<*>) =
        GlobalInstancesN.put(
                InstanceParametrizedType(Monad::class.java, listOf(F::class.java, G::class.java)),
                function
        )

inline fun <reified F, reified G, H> monad1(): Monad<HK2<F, G, H>> =
        instance(createInstanceParametrizedType2<F, G>())

inline fun <reified F, reified G, reified H> monad2(): Monad<F> =
        instance(createInstanceParametrizedType3<F, G, H>())

inline fun <reified F, reified G> createInstanceParametrizedType2() =
        InstanceParametrizedType(Monad::class.java, listOf(F::class.java, G::class.java))

inline fun <reified F, reified G, reified H> createInstanceParametrizedType3() =
        InstanceParametrizedType(Monad::class.java, listOf(F::class.java, G::class.java, H::class.java))