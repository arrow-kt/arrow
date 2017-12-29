package arrow.test.laws

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import arrow.*
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.data.Try
import arrow.data.recover
import arrow.effects.*
import arrow.effects.data.internal.BindingCancellationException
import arrow.syntax.applicative.tupled
import arrow.test.concurrency.SideEffect
import arrow.test.generators.genIntSmall
import arrow.test.generators.genThrowable
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.newSingleThreadContext

object AsyncLaws {
    inline fun <reified F> laws(AC: AsyncContext<F> = asyncContext(), M: MonadError<F, Throwable> = monadError<F, Throwable>(), EQ: Eq<HK<F, Int>>, EQ_EITHER: Eq<HK<F, Either<Throwable, Int>>>, EQERR: Eq<HK<F, Int>> = EQ): List<Law> =
            MonadErrorLaws.laws(M, EQERR, EQ_EITHER, EQ) + listOf(
                    Law("Async Laws: success equivalence", { asyncSuccess(AC, M, EQ) }),
                    Law("Async Laws: error equivalence", { asyncError(AC, M, EQERR) }),
                    Law("Async bind: binding blocks", { asyncBind(AC, M, EQ) }),
                    Law("Async bind: binding failure", { asyncBindError(AC, M, EQERR) }),
                    Law("Async bind: unsafe binding", { asyncBindUnsafe(AC, M, EQ) }),
                    Law("Async bind: unsafe binding failure", { asyncBindUnsafeError(AC, M, EQERR) }),
                    Law("Async bind: binding in parallel", { asyncParallelBind(AC, M, EQ) }),
                    Law("Async bind: binding cancellation before flatMap", { asyncCancellationBefore(AC, M, EQ) }),
                    Law("Async bind: binding cancellation after flatMap", { asyncCancellationAfter(AC, M, EQ) }),
                    Law("Async bind: monad comprehensions cancellable binding in other threads equivalence", { monadErrorComprehensionsBindInContextEquivalent(M, EQ) }),
                    Law("Async bind: bindingInContext cancellation before flatMap", { inContextCancellationBefore(M, EQ) }),
                    Law("Async bind: bindingInContext cancellation after flatMap", { inContextCancellationAfter(M, EQ) }),
                    Law("Async bind: bindingInContext error equivalent to raiseError", { inContextError(M, EQERR) })
            )

    inline fun <reified F> asyncSuccess(AC: AsyncContext<F> = asyncContext(), M: MonadError<F, Throwable> = monadError<F, Throwable>(), EQ: Eq<HK<F, Int>>): Unit =
            forAll(Gen.int(), { num: Int ->
                AC.runAsync { ff: (Either<Throwable, Int>) -> Unit -> ff(Right(num)) }.equalUnderTheLaw(M.pure<Int>(num), EQ)
            })

    inline fun <reified F> asyncError(AC: AsyncContext<F> = asyncContext(), M: MonadError<F, Throwable> = monadError<F, Throwable>(), EQ: Eq<HK<F, Int>>): Unit =
            forAll(genThrowable(), { e: Throwable ->
                AC.runAsync { ff: (Either<Throwable, Int>) -> Unit -> ff(Left(e)) }.equalUnderTheLaw(M.raiseError<Int>(e), EQ)
            })

    inline fun <reified F> asyncBind(AC: AsyncContext<F> = asyncContext(), M: MonadError<F, Throwable> = monadError<F, Throwable>(), EQ: Eq<HK<F, Int>>): Unit =
            forAll(genIntSmall(), genIntSmall(), genIntSmall(), { x: Int, y: Int, z: Int ->
                val bound = M.bindingE {
                    val a = bindAsync(AC) { x }
                    val b = bindAsync(AC) { a + y }
                    val c = bindAsync(AC) { b + z }
                    yields(c)
                }
                bound.equalUnderTheLaw(M.pure<Int>(x + y + z), EQ)
            })

    inline fun <reified F> asyncBindError(AC: AsyncContext<F> = asyncContext(), M: MonadError<F, Throwable> = monadError<F, Throwable>(), EQ: Eq<HK<F, Int>>): Unit =
            forAll(genThrowable(), { e: Throwable ->
                val bound: HK<F, Int> = M.bindingE {
                    bindAsync(AC) { throw e }
                }
                bound.equalUnderTheLaw(M.raiseError<Int>(e), EQ)
            })

    inline fun <reified F> asyncBindUnsafe(AC: AsyncContext<F> = asyncContext(), M: MonadError<F, Throwable> = monadError<F, Throwable>(), EQ: Eq<HK<F, Int>>): Unit =
            forAll(genIntSmall(), genIntSmall(), genIntSmall(), { x: Int, y: Int, z: Int ->
                val bound = M.bindingE {
                    val a = bindAsyncUnsafe(AC) { Right(x) }
                    val b = bindAsyncUnsafe(AC) { Right(a + y) }
                    val c = bindAsyncUnsafe(AC) { Right(b + z) }
                    yields(c)
                }
                bound.equalUnderTheLaw(M.pure<Int>(x + y + z), EQ)
            })

    inline fun <reified F> asyncBindUnsafeError(AC: AsyncContext<F> = asyncContext(), M: MonadError<F, Throwable> = monadError<F, Throwable>(), EQ: Eq<HK<F, Int>>): Unit =
            forAll(genThrowable(), { e: Throwable ->
                val bound: HK<F, Int> = M.bindingE {
                    bindAsyncUnsafe(AC) { Left(e) }
                }
                bound.equalUnderTheLaw(M.raiseError<Int>(e), EQ)
            })

    inline fun <reified F> asyncParallelBind(AC: AsyncContext<F> = asyncContext(), M: MonadError<F, Throwable> = monadError<F, Throwable>(), EQ: Eq<HK<F, Int>>): Unit =
            forAll(genIntSmall(), genIntSmall(), genIntSmall(), { x: Int, y: Int, z: Int ->
                val bound = M.bindingE {
                    val value = bind { tupled(runAsync(AC) { x }, runAsync(AC) { y }, runAsync(AC) { z }) }
                    yields(value.a + value.b + value.c)
                }
                bound.equalUnderTheLaw(M.pure<Int>(x + y + z), EQ)
            })

    inline fun <reified F> asyncCancellationBefore(AC: AsyncContext<F> = asyncContext(), M: MonadError<F, Throwable> = monadError<F, Throwable>(), EQ: Eq<HK<F, Int>>): Unit =
            forFew(5, genIntSmall(), { num: Int ->
                val sideEffect = SideEffect()
                val (binding, dispose) = M.bindingECancellable {
                    val a = bindAsync(AC) { Thread.sleep(500); num }
                    sideEffect.increment()
                    val b = bindAsync(AC) { a + 1 }
                    val c = pure(b + 1).bind()
                    yields(c)
                }
                Try { Thread.sleep(250); dispose() }.recover { throw it }
                binding.equalUnderTheLaw(M.raiseError(BindingCancellationException()), EQ) && sideEffect.counter == 0
            })

    inline fun <reified F> asyncCancellationAfter(AC: AsyncContext<F> = asyncContext(), M: MonadError<F, Throwable> = monadError<F, Throwable>(), EQ: Eq<HK<F, Int>>): Unit =
            forFew(5, genIntSmall(), { num: Int ->
                val sideEffect = SideEffect()
                val (binding, dispose) = M.bindingECancellable {
                    val a = bindAsync(AC) { num }
                    sideEffect.increment()
                    val b = bindAsync(AC) { Thread.sleep(500); sideEffect.increment(); a + 1 }
                    yields(b)
                }
                Try { Thread.sleep(250); dispose() }.recover { throw it }
                binding.equalUnderTheLaw(M.raiseError(BindingCancellationException()), EQ)
                        && sideEffect.counter == 0
            })

    inline fun <reified F> monadErrorComprehensionsBindInContextEquivalent(M: MonadError<F, Throwable> = monadError<F, Throwable>(), EQ: Eq<HK<F, Int>>): Unit =
            forFew(5, genIntSmall(), { num: Int ->
                val (bindM, d1) = M.bindingECancellable {
                    val a = bindInM(newSingleThreadContext("$num")) { pure(num + 1) }
                    val b = bindInM(newSingleThreadContext("$a")) { pure(a + 1) }
                    yields(b)
                }
                val (bind, d2) = M.bindingECancellable {
                    val a = bindIn(newSingleThreadContext("$num")) { num + 1 }
                    val b = bindIn(newSingleThreadContext("$a")) { a + 1 }
                    yields(b)
                }
                bindM.equalUnderTheLaw(bind, EQ)
            })

    inline fun <reified F> inContextCancellationBefore(M: MonadError<F, Throwable> = monadError<F, Throwable>(), EQ: Eq<HK<F, Int>>): Unit =
            forFew(5, genIntSmall(), { num: Int ->
                val sideEffect = SideEffect()
                val (binding, dispose) = M.bindingECancellable {
                    val a = bindIn(CommonPool) { Thread.sleep(500); num }
                    sideEffect.increment()
                    val b = bindIn(CommonPool) { a + 1 }
                    val c = pure(b + 1).bind()
                    yields(c)
                }
                Try { Thread.sleep(250); dispose() }.recover { throw it }
                binding.equalUnderTheLaw(M.raiseError(BindingCancellationException()), EQ) && sideEffect.counter == 0
            })

    inline fun <reified F> inContextCancellationAfter(M: MonadError<F, Throwable> = monadError<F, Throwable>(), EQ: Eq<HK<F, Int>>): Unit =
            forFew(5, genIntSmall(), { num: Int ->
                val sideEffect = SideEffect()
                val (binding, dispose) = M.bindingECancellable {
                    val a = bindIn(CommonPool) { num }
                    sideEffect.increment()
                    val b = bindIn(CommonPool) { Thread.sleep(500); sideEffect.increment(); a + 1 }
                    yields(b)
                }
                Try { Thread.sleep(250); dispose() }.recover { throw it }
                binding.equalUnderTheLaw(M.raiseError(BindingCancellationException()), EQ)
                        && sideEffect.counter == 0
            })

    inline fun <reified F> inContextError(M: MonadError<F, Throwable> = monadError<F, Throwable>(), EQ: Eq<HK<F, Int>>): Unit =
            forFew(5, genThrowable(), { throwable: Throwable ->
                M.bindingE {
                    val a: Int = bindIn(newSingleThreadContext("1")) { throw throwable }
                    yields(a)
                }.equalUnderTheLaw(M.raiseError(throwable), EQ)
            })
}
