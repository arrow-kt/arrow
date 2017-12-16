package kategory.effects

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kategory.*
import kotlinx.coroutines.experimental.newSingleThreadContext
import java.util.concurrent.TimeUnit

object MonadRunLaws {
    inline fun <reified F> laws(M: MonadRun<F, Throwable> = monadRun<F, Throwable>(), AC: AsyncContext<F> = asyncContext(), EQ: Eq<HK<F, Int>>, EQ_EITHER: Eq<HK<F, Either<Throwable, Int>>>, EQERR: Eq<HK<F, Int>> = EQ): List<Law> =
            AsyncLaws.laws(AC, M, EQERR, EQ_EITHER, EQ) + listOf(
                    Law("MonadRun Laws: sync/async equivalence", { asyncSyncEquivalence(M, AC) }),
                    Law("MonadRun Laws: async safe/unsafe equivalence", { asyncUnsafeEquivalence(M, AC) }),
                    Law("MonadRun Laws: runAsync should capture exceptions", { runAsyncSafety(M, AC, EQERR) }),
                    Law("MonadRun Laws: runAsync should propagate caught exceptions", { runAsyncUnSafety(M, AC, EQERR) }),
                    Law("MonadRun Laws: unsafeRunAsync should capture exceptions", { unsafeRunAsyncSafety(M, AC, EQERR) }),
                    Law("MonadRun Laws: unsafeRunSync should throw", { unsafeRunSyncSafety(M, AC, EQERR) }),
                    Law("MonadRun bind: bindParallel runs blocks in parallel", { bindParallel(M, AC, EQ) }),
                    Law("MonadRun bind: bindParallel fails on raiseError", { bindParallelError(M, AC, EQERR) }))

    val monadRunLawsCoroutineDispatcher = newCoroutineDispatcher("MonadRunLawsCoroutineDispatcher")

    fun <F> asyncFiber(M: MonadRun<F, Throwable>, AC: AsyncContext<F>, name: Int, delay: Int): Fiber<F, Int> =
            M.bindingFiber(AC) {
                val a = bindInM(newSingleThreadContext("MonadRunLaws $name")) {
                    runAsync<Int> { cb ->
                        Thread.sleep(delay.toLong())
                        cb(name.right())
                    }
                }
                yields(a)
            }

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
                val resultNotStarted = M.runAsync(runAsync) {
                    M.pure(it.fold({ result = M.raiseError(it) }, { result = M.pure(it) }))
                }
                M.unsafeRunSync(resultNotStarted) // run resultNotStarted to capture result
                M.unsafeRunSync(result!!) == M.unsafeRunSync(runAsync)
            })

    inline fun <reified F> runAsyncSafety(M: MonadRun<F, Throwable> = monadRun<F, Throwable>(), AC: AsyncContext<F> = asyncContext(), EQ_ERR: Eq<HK<F, Int>>): Unit =
            forAll(Gen.int(), Gen.int(), genThrowable(), { num: Int, num2: Int, t: Throwable ->
                val runAsync = AC.runAsync { ff: (Either<Throwable, Int>) -> Unit -> ff(num.right()) }
                val safeRun = M.runAsync(runAsync) { throw t }.map { num2 }

                safeRun.equalUnderTheLaw(M.pure(num2), EQ_ERR)
            })

    inline fun <reified F> runAsyncUnSafety(M: MonadRun<F, Throwable> = monadRun<F, Throwable>(), AC: AsyncContext<F> = asyncContext(), EQ_ERR: Eq<HK<F, Int>>): Unit =
            forAll(Gen.int(), Gen.int(), genThrowable(), { num: Int, num2: Int, t: Throwable ->
                val runAsync = AC.runAsync { ff: (Either<Throwable, Int>) -> Unit -> ff(num.right()) }
                val safeRun = M.runAsync(runAsync) { M.raiseError(t) }.map { num2 }

                safeRun.equalUnderTheLaw(M.pure(num2), EQ_ERR)
            })

    inline fun <reified F> unsafeRunAsyncSafety(M: MonadRun<F, Throwable> = monadRun<F, Throwable>(), AC: AsyncContext<F> = asyncContext(), EQ_ERR: Eq<HK<F, Int>>): Unit =
            forAll(Gen.int(), genThrowable(), { num: Int, t: Throwable ->
                val runAsync = AC.runAsync { ff: (Either<Throwable, Int>) -> Unit -> ff(num.right()) }
                Try {
                    M.unsafeRunAsync(runAsync) { throw t }
                }.fold(
                        { throw it },
                        { true }
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

    inline fun <reified F> bindParallel(M: MonadRun<F, Throwable> = monadRun<F, Throwable>(), AC: AsyncContext<F> = asyncContext(), EQ: Eq<HK<F, Int>>): Unit =
            forFew(5, genIntSmall(), genIntSmall(), { num1: Int, num2: Int ->
                val start = System.nanoTime()
                M.bindingFiber(AC) {
                    val result = bindParallel(monadRunLawsCoroutineDispatcher, asyncFiber(M, AC, num1, 500).binding, asyncFiber(M, AC, num2, 100).binding)
                    yields(result.a + result.b)
                }.binding.equalUnderTheLaw(M.pure(num1 + num2), EQ) &&
                        (System.nanoTime() - start) < TimeUnit.MILLISECONDS.toNanos(600)

            })

    inline fun <reified F> bindParallelError(M: MonadRun<F, Throwable> = monadRun<F, Throwable>(), AC: AsyncContext<F> = asyncContext(), EQ_ERR: Eq<HK<F, Int>>): Unit =
            forFew(5, genIntSmall(), genIntSmall(), genThrowable(), { num1: Int, num2: Int, t: Throwable ->
                val start = System.nanoTime()
                M.bindingFiber(AC) {
                    val result = bindParallel(monadRunLawsCoroutineDispatcher, asyncFiber(M, AC, num2, 500).binding, raiseError<Int>(t))
                    yields(result.a + result.b)
                }.binding.equalUnderTheLaw(M.raiseError(t), EQ_ERR) &&
                        // Note that the other monads aren't cancelled
                        (System.nanoTime() - start) > TimeUnit.MILLISECONDS.toNanos(500)
            })
}
