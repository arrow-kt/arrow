package kategory.effects

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kategory.*
import kotlinx.coroutines.experimental.newSingleThreadContext
import java.util.concurrent.TimeUnit

object MonadSuspendLaws {
    inline fun <reified F> laws(M: MonadSuspend<F, Throwable> = monadSuspend<F, Throwable>(), AC: AsyncContext<F> = asyncContext(), EQ: Eq<HK<F, Int>>, EQ_EITHER: Eq<HK<F, Either<Throwable, Int>>>, EQERR: Eq<HK<F, Int>> = EQ): List<Law> =
            //AsyncLaws.laws(AC, M, EQERR, EQ_EITHER, EQ) +
                    listOf(
                            Law("MonadSuspend Laws: sync/async equivalence", { asyncSyncEquivalence(M, AC) }),
                            Law("MonadSuspend Laws: async safe/unsafe equivalence", { asyncUnsafeEquivalence(M, AC) }),
                            Law("MonadSuspend Laws: runAsync should capture exceptions", { runAsyncSafety(M, AC, EQERR) }),
                            Law("MonadSuspend Laws: runAsync should propagate caught exceptions", { runAsyncUnSafety(M, AC, EQERR) }),
                            Law("MonadSuspend Laws: unsafeRunAsync should capture exceptions", { unsafeRunAsyncSafety(M, AC, EQERR) }),
                            Law("MonadSuspend Laws: unsafeRunSync should throw", { unsafeRunSyncSafety(M, AC, EQERR) }),
                            Law("MonadSuspend bind: bindParallel runs blocks in parallel", { bindParallel(M, AC, EQ) }),
                            Law("MonadSuspend bind: bindParallel fails on raiseError", { bindParallelError(M, AC, EQERR) }),
                            Law("MonadSuspend bind: bindRace cancels the losing side", { bindRace(M, AC) }),
                            Law("MonadSuspend bind: bindRace works equally on either side", { bindRaceCommutativity(M, AC) }))

    val monadRunLawsCoroutineDispatcher = newCoroutineDispatcher("MonadRunLawsCoroutineDispatcher")

    fun <F> asyncFiber(M: MonadSuspend<F, Throwable>, AC: AsyncContext<F>, name: Int, delay: Int): Fiber<F, Int> =
            M.bindingFiber(AC, monadRunLawsCoroutineDispatcher) {
                val a = bindIn(newSingleThreadContext("MonadRunLaws $name")) {
                    Thread.sleep(delay.toLong())
                    name
                }
                yields(a)
            }

    inline fun <reified F> asyncSyncEquivalence(M: MonadSuspend<F, Throwable> = monadSuspend<F, Throwable>(), AC: AsyncContext<F> = asyncContext()): Unit =
            forAll(Gen.int(), { num: Int ->
                var result: Int? = null
                val runAsync = AC.runAsync { ff: (Either<Throwable, Int>) -> Unit -> ff(num.right()) }
                M.unsafeRunAsync(runAsync) {
                    it.fold({}, { result = it })
                }
                result == M.unsafeRunSync(runAsync)
            })

    inline fun <reified F> asyncUnsafeEquivalence(M: MonadSuspend<F, Throwable> = monadSuspend<F, Throwable>(), AC: AsyncContext<F> = asyncContext()): Unit =
            forAll(Gen.int(), { num: Int ->
                var result: HK<F, Int>? = null
                val runAsync = AC.runAsync { ff: (Either<Throwable, Int>) -> Unit -> ff(num.right()) }
                val resultNotStarted = M.runAsync(runAsync) {
                    M.pure(it.fold({ result = M.raiseError(it) }, { result = M.pure(it) }))
                }
                M.unsafeRunSync(resultNotStarted) // run resultNotStarted to capture result
                M.unsafeRunSync(result!!) == M.unsafeRunSync(runAsync)
            })

    inline fun <reified F> runAsyncSafety(M: MonadSuspend<F, Throwable> = monadSuspend<F, Throwable>(), AC: AsyncContext<F> = asyncContext(), EQ_ERR: Eq<HK<F, Int>>): Unit =
            forAll(Gen.int(), Gen.int(), genThrowable(), { num: Int, num2: Int, t: Throwable ->
                val runAsync = AC.runAsync { ff: (Either<Throwable, Int>) -> Unit -> ff(num.right()) }
                val safeRun = M.runAsync(runAsync) { throw t }.map { num2 }

                safeRun.equalUnderTheLaw(M.pure(num2), EQ_ERR)
            })

    inline fun <reified F> runAsyncUnSafety(M: MonadSuspend<F, Throwable> = monadSuspend<F, Throwable>(), AC: AsyncContext<F> = asyncContext(), EQ_ERR: Eq<HK<F, Int>>): Unit =
            forAll(Gen.int(), Gen.int(), genThrowable(), { num: Int, num2: Int, t: Throwable ->
                val runAsync = AC.runAsync { ff: (Either<Throwable, Int>) -> Unit -> ff(num.right()) }
                val safeRun = M.runAsync(runAsync) { M.raiseError(t) }.map { num2 }

                safeRun.equalUnderTheLaw(M.pure(num2), EQ_ERR)
            })

    inline fun <reified F> unsafeRunAsyncSafety(M: MonadSuspend<F, Throwable> = monadSuspend<F, Throwable>(), AC: AsyncContext<F> = asyncContext(), EQ_ERR: Eq<HK<F, Int>>): Unit =
            forAll(Gen.int(), genThrowable(), { num: Int, t: Throwable ->
                val runAsync = AC.runAsync { ff: (Either<Throwable, Int>) -> Unit -> ff(num.right()) }
                Try {
                    M.unsafeRunAsync(runAsync) { throw t }
                }.fold(
                        { throw it },
                        { true }
                )
            })

    inline fun <reified F> unsafeRunSyncSafety(M: MonadSuspend<F, Throwable> = monadSuspend<F, Throwable>(), AC: AsyncContext<F> = asyncContext(), EQ_ERR: Eq<HK<F, Int>>): Unit =
            forAll(Gen.int(), genThrowable(), { num: Int, t: Throwable ->
                val runAsync = AC.runAsync { ff: (Either<Throwable, Int>) -> Unit -> ff(t.left()) }
                Try {
                    M.unsafeRunSync(runAsync)
                }.fold(
                        { M.raiseError<Int>(it).equalUnderTheLaw(M.raiseError(t), EQ_ERR) },
                        { false }
                )
            })

    inline fun <reified F> bindParallel(M: MonadSuspend<F, Throwable> = monadSuspend<F, Throwable>(), AC: AsyncContext<F> = asyncContext(), EQ: Eq<HK<F, Int>>): Unit =
            forFew(5, genIntSmall(), genIntSmall(), { num1: Int, num2: Int ->
                val start = System.nanoTime()
                M.bindingFiber(AC, monadRunLawsCoroutineDispatcher) {
                    val result = bindParallel(asyncFiber(M, AC, num1, 500), asyncFiber(M, AC, num2, 100))
                    yields(result.a + result.b)
                }.binding.equalUnderTheLaw(M.pure(num1 + num2), EQ) &&
                        // Less time than the combination of both tasks
                        (System.nanoTime() - start) < TimeUnit.MILLISECONDS.toNanos(600)

            })

    inline fun <reified F> bindParallelError(M: MonadSuspend<F, Throwable> = monadSuspend<F, Throwable>(), AC: AsyncContext<F> = asyncContext(), EQ_ERR: Eq<HK<F, Int>>): Unit =
            forFew(5, genIntSmall(), genIntSmall(), genThrowable(), { num1: Int, num2: Int, t: Throwable ->
                val start = System.nanoTime()
                val binding = M.bindingFiber(AC, monadRunLawsCoroutineDispatcher) {
                    val result = bindParallel(asyncFiber(M, AC, num2, 500), bindingFiber(AC, monadRunLawsCoroutineDispatcher) { raiseError<Int>(t) })
                    yields(result.a + result.b)
                }.binding
                val endTime = System.nanoTime() - start
                binding.equalUnderTheLaw(M.raiseError(t), EQ_ERR) &&
                        // Note that the other monads aren't cancelled
                        endTime > TimeUnit.MILLISECONDS.toNanos(500) && endTime < TimeUnit.MILLISECONDS.toNanos(1000)
            })

    inline fun <reified F> bindRace(M: MonadSuspend<F, Throwable> = monadSuspend<F, Throwable>(), AC: AsyncContext<F> = asyncContext()): Unit =
            forFew(5, genIntSmall(), genIntSmall(), { num1: Int, num2: Int ->
                val start = System.nanoTime()
                val result: Either<Int, Int> = M.unsafeRunSync(M.bindingFiber(AC, monadRunLawsCoroutineDispatcher) {
                    val result = bindRace(asyncFiber(M, AC, num1, 5000), asyncFiber(M, AC, num2, 100))
                    yields(result)
                }.binding)
                val expected = num2.right()
                expected == result &&
                        // We should finish in less time than it takes for the longest task to complete
                        (System.nanoTime() - start) < TimeUnit.MILLISECONDS.toNanos(5000)

            })

    inline fun <reified F> bindRaceCommutativity(M: MonadSuspend<F, Throwable> = monadSuspend<F, Throwable>(), AC: AsyncContext<F> = asyncContext()): Unit =
            forFew(5, genIntSmall(), genIntSmall(), { num1: Int, num2: Int ->
                val start = System.nanoTime()
                val victoryFirst = M.unsafeRunSync(M.bindingFiber(AC, monadRunLawsCoroutineDispatcher) {
                    val result = bindRace(asyncFiber(M, AC, num1, 500), asyncFiber(M, AC, num2, 100))
                    yields(result)
                }.binding).fold({ Int.MIN_VALUE }, { it })
                val victorySecond = M.unsafeRunSync(M.bindingFiber(AC, monadRunLawsCoroutineDispatcher) {
                    val result = bindRace(asyncFiber(M, AC, num2, 100), asyncFiber(M, AC, num1, 500))
                    yields(result)
                }.binding).fold({ it }, { Int.MAX_VALUE })

                val valuesExpected = victoryFirst == num2 && victoryFirst == victorySecond
                valuesExpected &&
                        // We should finish in less time than it takes for the combination of the longest tasks to complete
                        (System.nanoTime() - start) < TimeUnit.MILLISECONDS.toNanos(1000)

            })
}
