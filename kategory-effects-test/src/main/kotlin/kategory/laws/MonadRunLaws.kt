package kategory.effects

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kategory.*

object MonadRunLaws {
    inline fun <reified F> laws(M: MonadRun<F, Throwable> = monadRun<F, Throwable>(), AC: AsyncContext<F> = asyncContext(), EQ: Eq<HK<F, Int>>, EQ_EITHER: Eq<HK<F, Either<Throwable, Int>>>, EQERR: Eq<HK<F, Int>> = EQ): List<Law> =
            AsyncLaws.laws(AC, M, EQERR, EQ_EITHER, EQ) + listOf(
                    Law("MonadRun Laws: sync/async equivalence", { asyncSyncEquivalence(M, AC) }),
                    Law("MonadRun Laws: async safe/unsafe equivalence", { asyncUnsafeEquivalence(M, AC) }),
                    Law("MonadRun Laws: runAsync should not capture exceptions", { runAsyncSafety(M, AC, EQERR) }),
                    Law("MonadRun Laws: unsafeRunAsync should capture exceptions", { unsafeRunAsyncSafety(M, AC, EQERR) }),
                    Law("MonadRun Laws: unsafeRunAsync should capture exceptions", { unsafeRunSyncSafety(M, AC, EQERR) }))

    inline fun <reified F> asyncSyncEquivalence(M: MonadRun<F, Throwable> = monadRun<F, Throwable>(), AC: AsyncContext<F> = asyncContext()): Unit =
            forAll(Gen.int(), { num: Int ->
                var result: Int? = null
                val runAsync = AC.runAsync { ff: (Either<Throwable, Int>) -> Unit -> ff(num.right()) }
                M.unsafeRunAsync(runAsync) {
                    it.fold({}, { result = it })
                }
                result == M.unsafeRunSync(runAsync)
            })

    inline fun <reified F> asyncUnsafeEquivalence(M: MonadRun<F, Throwable> = monadRun<F, Throwable>(), AC: AsyncContext<F> = asyncContext()): Unit =
            forAll(Gen.int(), { num: Int ->
                var result: HK<F, Int>? = null
                val runAsync = AC.runAsync { ff: (Either<Throwable, Int>) -> Unit -> ff(num.right()) }
                M.runAsync(runAsync) {
                    M.pure(it.fold({ }, { result = M.pure(it) }))
                }
                M.unsafeRunSync(result!!) == M.unsafeRunSync(runAsync)
            })

    inline fun <reified F> runAsyncSafety(M: MonadRun<F, Throwable> = monadRun<F, Throwable>(), AC: AsyncContext<F> = asyncContext(), EQ_ERR: Eq<HK<F, Int>>): Unit =
            forAll(Gen.int(), genThrowable(), { num: Int, t: Throwable ->
                val runAsync = AC.runAsync { ff: (Either<Throwable, Int>) -> Unit -> ff(num.right()) }
                val safeRun = M.runAsync(runAsync) { throw t }.map { /* Should never happen */ 0 }

                safeRun.equalUnderTheLaw(M.raiseError(t), EQ_ERR)
            })

    inline fun <reified F> unsafeRunAsyncSafety(M: MonadRun<F, Throwable> = monadRun<F, Throwable>(), AC: AsyncContext<F> = asyncContext(), EQ_ERR: Eq<HK<F, Int>>): Unit =
            forAll(Gen.int(), genThrowable(), { num: Int, t: Throwable ->
                val runAsync = AC.runAsync { ff: (Either<Throwable, Int>) -> Unit -> ff(num.right()) }
                Try {
                    M.unsafeRunAsync(runAsync) { throw t }
                }.fold(
                        { M.raiseError<Int>(it).equalUnderTheLaw(M.raiseError(t), EQ_ERR) },
                        { false }
                )
            })

    inline fun <reified F> unsafeRunSyncSafety(M: MonadRun<F, Throwable> = monadRun<F, Throwable>(), AC: AsyncContext<F> = asyncContext(), EQ_ERR: Eq<HK<F, Int>>): Unit =
            forAll(Gen.int(), genThrowable(), { num: Int, t: Throwable ->
                val runAsync = AC.runAsync { ff: (Either<Throwable, Int>) -> Unit -> ff(t.left()) }
                Try {
                    M.unsafeRunSync(runAsync)
                }.fold(
                        { M.raiseError<Int>(it).equalUnderTheLaw(M.raiseError(t), EQ_ERR) },
                        { false }
                )
            })
}
