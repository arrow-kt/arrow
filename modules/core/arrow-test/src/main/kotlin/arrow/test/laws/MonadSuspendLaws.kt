package arrow.test.laws

import arrow.Kind
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.data.Try
import arrow.data.recover
import arrow.effects.MonadSuspend
import arrow.effects.bindingCancellable
import arrow.effects.data.internal.BindingCancellationException
import arrow.effects.monadSuspend
import arrow.syntax.applicative.tupled
import arrow.test.concurrency.SideEffect
import arrow.test.generators.genIntSmall
import arrow.test.generators.genThrowable
import arrow.typeclasses.Eq
import io.kotlintest.properties.forAll
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.newSingleThreadContext

object MonadSuspendLaws {
    inline fun <reified F> laws(SC: MonadSuspend<F> = monadSuspend(), EQ: Eq<Kind<F, Int>>, EQ_EITHER: Eq<Kind<F, Either<Throwable, Int>>>, EQERR: Eq<Kind<F, Int>> = EQ): List<Law> =
            MonadErrorLaws.laws(SC, EQERR, EQ_EITHER, EQ) + listOf(
                    Law("Sync bind: binding blocks", { asyncBind(SC, EQ) }),
                    Law("Sync bind: binding failure", { asyncBindError(SC, EQERR) }),
                    Law("Sync bind: unsafe binding", { asyncBindUnsafe(SC, EQ) }),
                    Law("Sync bind: unsafe binding failure", { asyncBindUnsafeError(SC, EQERR) }),
                    Law("Sync bind: binding in parallel", { asyncParallelBind(SC, EQ) }),
                    Law("Sync bind: binding cancellation before flatMap", { asyncCancellationBefore(SC, EQ) }),
                    Law("Sync bind: binding cancellation after flatMap", { asyncCancellationAfter(SC, EQ) }),
                    Law("Sync bind: bindingInContext cancellation before flatMap", { inContextCancellationBefore(SC, EQ) }),
                    Law("Sync bind: bindingInContext cancellation after flatMap", { inContextCancellationAfter(SC, EQ) }),
                    Law("Sync bind: bindingInContext throw equivalent to raiseError", { inContextErrorThrow(SC, EQERR) }),
                    Law("Sync bind: monad comprehensions binding in other threads equivalence", { monadComprehensionsBindInContextEquivalent(SC, EQ) })
            )

    inline fun <reified F> asyncBind(SC: MonadSuspend<F> = monadSuspend(), EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genIntSmall(), genIntSmall(), genIntSmall(), { x: Int, y: Int, z: Int ->
                val (bound, dispose) = SC.bindingCancellable {
                    val a = bindDefer { x }
                    val b = bindDefer { a + y }
                    val c = bindDefer { b + z }
                    c
                }
                bound.equalUnderTheLaw(SC.pure<Int>(x + y + z), EQ)
            })

    inline fun <reified F> asyncBindError(SC: MonadSuspend<F> = monadSuspend(), EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genThrowable(), { e: Throwable ->
                val (bound: Kind<F, Int>, cancel) = SC.bindingCancellable<F, Int> {
                    bindDefer { throw e }
                }
                bound.equalUnderTheLaw(SC.raiseError<Int>(e), EQ)
            })

    inline fun <reified F> asyncBindUnsafe(SC: MonadSuspend<F> = monadSuspend(), EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genIntSmall(), genIntSmall(), genIntSmall(), { x: Int, y: Int, z: Int ->
                val (bound, dispose) = SC.bindingCancellable {
                    val a = bindDeferUnsafe { Right(x) }
                    val b = bindDeferUnsafe { Right(a + y) }
                    val c = bindDeferUnsafe { Right(b + z) }
                    c
                }
                bound.equalUnderTheLaw(SC.pure<Int>(x + y + z), EQ)
            })

    inline fun <reified F> asyncBindUnsafeError(SC: MonadSuspend<F> = monadSuspend(), EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genThrowable(), { e: Throwable ->
                val (bound: Kind<F, Int>, dispose) = SC.bindingCancellable<F, Int> {
                    bindDeferUnsafe { Left(e) }
                }
                bound.equalUnderTheLaw(SC.raiseError<Int>(e), EQ)
            })

    inline fun <reified F> asyncParallelBind(SC: MonadSuspend<F> = monadSuspend(), EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genIntSmall(), genIntSmall(), genIntSmall(), { x: Int, y: Int, z: Int ->
                val (bound, dispose) = SC.bindingCancellable {
                    val value = bind { tupled(SC { x }, SC { y }, SC { z }) }
                    value.a + value.b + value.c
                }
                bound.equalUnderTheLaw(SC.pure<Int>(x + y + z), EQ)
            })

    inline fun <reified F> asyncCancellationBefore(SC: MonadSuspend<F> = monadSuspend(), EQ: Eq<Kind<F, Int>>): Unit =
            forFew(5, genIntSmall(), { num: Int ->
                val sideEffect = SideEffect()
                val (binding, dispose) = SC.bindingCancellable {
                    val a = bindDefer { Thread.sleep(500); num }
                    sideEffect.increment()
                    val b = bindDefer { a + 1 }
                    val c = pure(b + 1).bind()
                    c
                }
                Try { Thread.sleep(250); dispose() }.recover { throw it }
                binding.equalUnderTheLaw(SC.raiseError(BindingCancellationException()), EQ) && sideEffect.counter == 0
            })

    inline fun <reified F> asyncCancellationAfter(SC: MonadSuspend<F> = monadSuspend(), EQ: Eq<Kind<F, Int>>): Unit =
            forFew(5, genIntSmall(), { num: Int ->
                val sideEffect = SideEffect()
                val (binding, dispose) = SC.bindingCancellable {
                    val a = bindDefer { num }
                    sideEffect.increment()
                    val b = bindDefer { Thread.sleep(500); sideEffect.increment(); a + 1 }
                    b
                }
                Try { Thread.sleep(250); dispose() }.recover { throw it }
                binding.equalUnderTheLaw(SC.raiseError(BindingCancellationException()), EQ)
                        && sideEffect.counter == 0
            })

    inline fun <reified F> inContextCancellationBefore(SC: MonadSuspend<F> = monadSuspend(), EQ: Eq<Kind<F, Int>>): Unit =
            forFew(5, genIntSmall(), { num: Int ->
                val sideEffect = SideEffect()
                val (binding, dispose) = SC.bindingCancellable {
                    val a = bindIn(CommonPool) { Thread.sleep(500); num }
                    sideEffect.increment()
                    val b = bindIn(CommonPool) { a + 1 }
                    val c = pure(b + 1).bind()
                    c
                }
                Try { Thread.sleep(250); dispose() }.recover { throw it }
                binding.equalUnderTheLaw(SC.raiseError(BindingCancellationException()), EQ) && sideEffect.counter == 0
            })

    inline fun <reified F> inContextCancellationAfter(SC: MonadSuspend<F> = monadSuspend(), EQ: Eq<Kind<F, Int>>): Unit =
            forFew(5, genIntSmall(), { num: Int ->
                val sideEffect = SideEffect()
                val (binding, dispose) = SC.bindingCancellable {
                    val a = bindIn(CommonPool) { num }
                    sideEffect.increment()
                    val b = bindIn(CommonPool) { Thread.sleep(500); sideEffect.increment(); a + 1 }
                    b
                }
                Try { Thread.sleep(250); dispose() }.recover { throw it }
                binding.equalUnderTheLaw(SC.raiseError(BindingCancellationException()), EQ)
                        && sideEffect.counter == 0
            })

    inline fun <reified F> inContextErrorThrow(SC: MonadSuspend<F> = monadSuspend(), EQ: Eq<Kind<F, Int>>): Unit =
            forFew(5, genThrowable(), { throwable: Throwable ->
                SC.bindingCancellable {
                    val a: Int = bindIn(newSingleThreadContext("1")) { throw throwable }
                    a
                }.a.equalUnderTheLaw(SC.raiseError(throwable), EQ)
            })

    inline fun <reified F> monadComprehensionsBindInContextEquivalent(SC: MonadSuspend<F> = monadSuspend(), EQ: Eq<Kind<F, Int>>): Unit =
            forFew(5, genIntSmall(), { num: Int ->
                val bindM = SC.bindingCancellable {
                    val a = bindDeferIn(newSingleThreadContext("$num")) { num + 1 }
                    val b = bindDeferIn(newSingleThreadContext("$a")) { a + 1 }
                    b
                }
                val bind = SC.bindingCancellable {
                    val a = bindIn(newSingleThreadContext("$num")) { num + 1 }
                    val b = bindIn(newSingleThreadContext("$a")) { a + 1 }
                    b
                }
                bindM.a.equalUnderTheLaw(bind.a, EQ)
            })
}
