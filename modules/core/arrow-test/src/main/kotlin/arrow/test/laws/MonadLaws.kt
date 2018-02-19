package arrow.test.laws

import arrow.Kind
import arrow.core.Left
import arrow.core.Right
import arrow.data.Kleisli
import arrow.free.Free
import arrow.free.bindingStackSafe
import arrow.free.run
import arrow.test.generators.genApplicative
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genIntSmall
import arrow.typeclasses.Eq
import arrow.typeclasses.Monad
import arrow.typeclasses.binding
import arrow.typeclasses.monad
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kotlinx.coroutines.experimental.newSingleThreadContext

object MonadLaws {

    inline fun <reified F> laws(M: Monad<F> = monad<F>(), EQ: Eq<Kind<F, Int>>): List<Law> =
            ApplicativeLaws.laws(M, EQ) + listOf(
                    Law("Monad Laws: left identity", { leftIdentity(M, EQ) }),
                    Law("Monad Laws: right identity", { rightIdentity(M, EQ) }),
                    Law("Monad Laws: kleisli left identity", { kleisliLeftIdentity(M, EQ) }),
                    Law("Monad Laws: kleisli right identity", { kleisliRightIdentity(M, EQ) }),
                    Law("Monad Laws: map / flatMap coherence", { mapFlatMapCoherence(M, EQ) }),
                    Law("Monad Laws: monad comprehensions", { monadComprehensions(M, EQ) }),
                    Law("Monad Laws: monad comprehensions binding in other threads", { monadComprehensionsBindInContext(M, EQ) }),
                    Law("Monad Laws: stack-safe//unsafe monad comprehensions equivalence", { equivalentComprehensions(M, EQ) }),
                    Law("Monad Laws: stack safe", { stackSafety(5000, M, EQ) }),
                    Law("Monad Laws: stack safe comprehensions", { stackSafetyComprehensions(5000, M, EQ) })
            )

    inline fun <reified F> leftIdentity(M: Monad<F> = monad<F>(), EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genFunctionAToB<Int, Kind<F, Int>>(genApplicative(Gen.int(), M)), Gen.int(), { f: (Int) -> Kind<F, Int>, a: Int ->
                M.flatMap(M.pure(a), f).equalUnderTheLaw(f(a), EQ)
            })

    inline fun <reified F> rightIdentity(M: Monad<F> = monad<F>(), EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genApplicative(Gen.int(), M), { fa: Kind<F, Int> ->
                M.flatMap(fa, { M.pure(it) }).equalUnderTheLaw(fa, EQ)
            })

    inline fun <reified F> kleisliLeftIdentity(M: Monad<F> = monad<F>(), EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genFunctionAToB<Int, Kind<F, Int>>(genApplicative(Gen.int(), M)), Gen.int(), { f: (Int) -> Kind<F, Int>, a: Int ->
                (Kleisli({ n: Int -> M.pure(n) }).andThen(Kleisli(f), M).run(a).equalUnderTheLaw(f(a), EQ))
            })

    inline fun <reified F> kleisliRightIdentity(M: Monad<F> = monad<F>(), EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genFunctionAToB<Int, Kind<F, Int>>(genApplicative(Gen.int(), M)), Gen.int(), { f: (Int) -> Kind<F, Int>, a: Int ->
                (Kleisli(f).andThen(Kleisli({ n: Int -> M.pure(n) }), M).run(a).equalUnderTheLaw(f(a), EQ))
            })

    inline fun <reified F> mapFlatMapCoherence(M: Monad<F> = monad<F>(), EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genFunctionAToB<Int, Int>(Gen.int()), genApplicative(Gen.int(), M), { f: (Int) -> Int, fa: Kind<F, Int> ->
                M.flatMap(fa, { M.pure(f(it)) }).equalUnderTheLaw(M.map(fa, f), EQ)
            })

    inline fun <reified F> stackSafety(iterations: Int = 5000, M: Monad<F> = monad<F>(), EQ: Eq<Kind<F, Int>>): Unit =
            forFew(1, Gen.oneOf(listOf(iterations))) { iter ->
                val res = M.tailRecM(0, { i -> M.pure(if (i < iter) Left(i + 1) else Right(i)) })
                res.equalUnderTheLaw(M.pure(iter), EQ)
            }

    inline fun <reified F> stackSafetyComprehensions(iterations: Int = 5000, M: Monad<F> = monad<F>(), EQ: Eq<Kind<F, Int>>): Unit =
            forFew(1, Gen.oneOf(listOf(iterations))) { iter ->
                val res = stackSafeTestProgram(M, 0, iter)
                res.run(M).equalUnderTheLaw(M.pure(iter), EQ)
            }

    inline fun <reified F> equivalentComprehensions(M: Monad<F> = monad<F>(), EQ: Eq<Kind<F, Int>>): Unit =
            forAll(Gen.int(), { num: Int ->
                val aa = M.binding {
                    val a = pure(num).bind()
                    val b = pure(a + 1).bind()
                    val c = pure(b + 1).bind()
                    c
                }
                val bb = M.bindingStackSafe {
                    val a = pure(num).bind()
                    val b = pure(a + 1).bind()
                    val c = pure(b + 1).bind()
                    c
                }.run(M)
                aa.equalUnderTheLaw(bb, EQ) &&
                        aa.equalUnderTheLaw(M.pure(num + 2), EQ)
            })

    inline fun <reified F> monadComprehensions(M: Monad<F> = monad<F>(), EQ: Eq<Kind<F, Int>>): Unit =
            forAll(Gen.int(), { num: Int ->
                M.binding {
                    val a = pure(num).bind()
                    val b = pure(a + 1).bind()
                    val c = pure(b + 1).bind()
                    c
                }.equalUnderTheLaw(M.pure(num + 2), EQ)
            })

    inline fun <reified F> monadComprehensionsBindInContext(M: Monad<F> = monad<F>(), EQ: Eq<Kind<F, Int>>): Unit =
            forFew(5, genIntSmall(), { num: Int ->
                M.binding {
                    val a = bindIn(newSingleThreadContext("$num")) { num + 1 }
                    val b = bindIn(newSingleThreadContext("$a")) { a + 1 }
                    b
                }.equalUnderTheLaw(M.pure(num + 2), EQ)
            })

    fun <F> stackSafeTestProgram(M: Monad<F>, n: Int, stopAt: Int): Free<F, Int> = M.bindingStackSafe {
        val v = pure(n + 1).bind()
        val r = if (v < stopAt) stackSafeTestProgram(M, v, stopAt).bind() else pure(v).bind()
        r
    }
}
