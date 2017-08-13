package kategory

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object AsyncLaws {
    inline fun <reified F> laws(AC: AsyncContext<F> = asyncContext(), M: MonadError<F, Throwable> = monadError<F, Throwable>(), EQ: Eq<HK<F, Int>>, EQER: Eq<HK<F, Int>>): List<Law> =
            listOf(
                    Law("Async Laws: success equivalence", { asyncSuccess(AC, M, EQ) }),
                    Law("Async Laws: error equivalence", { asyncError(AC, M, EQER) }),
                    Law("Async bind: binding blocks", { asyncBind(AC, M, EQ) }),
                    Law("Async bind: binding failure", { asyncBindError(AC, M, EQER) }),
                    Law("Async bind: unsafe binding", { asyncBindUnsafe(AC, M, EQ) }),
                    Law("Async bind: unsafe binding failure", { asyncBindUnsafeError(AC, M, EQ) }),
                    Law("Async bind: binding in parallel", { asyncParallelBind(AC, M, EQ) })
            )

    inline fun <reified F> asyncSuccess(AC: AsyncContext<F> = asyncContext(), M: MonadError<F, Throwable> = monadError<F, Throwable>(), EQ: Eq<HK<F, Int>>): Unit =
            forAll(Gen.int(), { num: Int ->
                AC.runAsync { ff: (Either<Throwable, Int>) -> Unit -> ff(num.right()) }.equalUnderTheLaw(M.pure<Int>(num), EQ)
            })

    inline fun <reified F> asyncError(AC: AsyncContext<F> = asyncContext(), M: MonadError<F, Throwable> = monadError<F, Throwable>(), EQ: Eq<HK<F, Int>>): Unit =
            forAll(genThrowable(), { e: Throwable ->
                AC.runAsync { ff: (Either<Throwable, Int>) -> Unit -> ff(e.left()) }.equalUnderTheLaw(M.raiseError<Int>(e), EQ)
            })

    inline fun <reified F> asyncBind(AC: AsyncContext<F> = asyncContext(), M: MonadError<F, Throwable> = monadError<F, Throwable>(), EQ: Eq<HK<F, Int>>): Unit =
            forAll(genIntSmall(), genIntSmall(), genIntSmall(), { x: Int, y: Int, z: Int ->
                val bound = AC.bindingAsync(M) {
                    val a = bindAsync { x }
                    val b = bindAsync { a + y }
                    val c = bindAsync { b + z }
                    yields(c)
                }
                bound.equalUnderTheLaw(M.pure<Int>(x + y + z), EQ)
            })

    inline fun <reified F> asyncBindError(AC: AsyncContext<F> = asyncContext(), M: MonadError<F, Throwable> = monadError<F, Throwable>(), EQ: Eq<HK<F, Int>>): Unit =
            forAll(genIntSmall(), genIntSmall(), genIntSmall(), { x: Int, y: Int, z: Int ->
                val bound = AC.bindingAsync(M) {
                    val a = bindAsync { x }
                    val b = bindAsync { a + y }
                    val c = bindAsync { b + z }
                    yields(c)
                }
                bound.equalUnderTheLaw(M.pure<Int>(x + y + z), EQ)
            })

    inline fun <reified F> asyncBindUnsafe(AC: AsyncContext<F> = asyncContext(), M: MonadError<F, Throwable> = monadError<F, Throwable>(), EQ: Eq<HK<F, Int>>): Unit =
            forAll(genIntSmall(), genIntSmall(), genIntSmall(), { x: Int, y: Int, z: Int ->
                val bound = AC.bindingAsync(M) {
                    val a = bindAsyncUnsafe { x.right() }
                    val b = bindAsyncUnsafe { (a + y).right() }
                    val c = bindAsyncUnsafe { (b + z).right() }
                    yields(c)
                }
                bound.equalUnderTheLaw(M.pure<Int>(x + y + z), EQ)
            })

    inline fun <reified F> asyncBindUnsafeError(AC: AsyncContext<F> = asyncContext(), M: MonadError<F, Throwable> = monadError<F, Throwable>(), EQ: Eq<HK<F, Int>>): Unit =
            forAll(genThrowable(), { e: Throwable ->
                val bound: HK<F, Int> = AC.bindingAsync(M) {
                    bindAsyncUnsafe { e.left() }
                }
                bound.equalUnderTheLaw(M.raiseError<Int>(e), EQ)
            })

    inline fun <reified F> asyncParallelBind(AC: AsyncContext<F> = asyncContext(), M: MonadError<F, Throwable> = monadError<F, Throwable>(), EQ: Eq<HK<F, Int>>): Unit =
            forAll(genIntSmall(), genIntSmall(), genIntSmall(), { x: Int, y: Int, z: Int ->
                val bound = M.binding {
                    val value = !M.tupled(runAsync(AC) { x }, runAsync(AC) { y }, runAsync(AC) { z })
                    yields(value.a + value.b + value.c)
                }
                bound.equalUnderTheLaw(M.pure<Int>(x + y + z), EQ)
            })
}
