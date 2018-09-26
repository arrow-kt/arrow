package arrow.test.laws

import arrow.Kind
import arrow.core.Either
import arrow.effects.typeclasses.Bracket
import arrow.test.generators.genConstructor
import arrow.test.generators.genFunctionAToB
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object BracketLaws {
    inline fun <F> laws(BF: Bracket<F, Throwable>,
                        noinline cf: (Int) -> Kind<F, Int>,
                        EQ: Eq<Kind<F, Int>>,
                        EQ_EITHER: Eq<Kind<F, Either<Throwable, Int>>>,
                        EQERR: Eq<Kind<F, Int>> = EQ): List<Law> =
        MonadErrorLaws.laws(BF, EQERR, EQ_EITHER, EQ) + listOf(
            Law("Bracket: bracketCase with just Unit is eqv to Map") { BF.bracketCaseWithJustUnitEqvMap(cf, EQ) }
//            Law("Sync bind: binding failure") { SC.asyncBindError(EQERR) },
//            Law("Sync bind: unsafe binding") { SC.asyncBindUnsafe(EQ) },
//            Law("Sync bind: unsafe binding failure") { SC.asyncBindUnsafeError(EQERR) },
//            Law("Sync bind: binding in parallel") { SC.asyncParallelBind(EQ) },
//            Law("Sync bind: binding cancellation before flatMap") { SC.asyncCancellationBefore(EQ) },
//            Law("Sync bind: binding cancellation after flatMap") { SC.asyncCancellationAfter(EQ) },
//            Law("Sync bind: bindingInContext cancellation before flatMap") { SC.inContextCancellationBefore(EQ) },
//            Law("Sync bind: bindingInContext cancellation after flatMap") { SC.inContextCancellationAfter(EQ) },
//            Law("Sync bind: bindingInContext throw equivalent to raiseError") { SC.inContextErrorThrow(EQERR) },
//            Law("Sync bind: monad comprehensions binding in other threads equivalence") { SC.monadComprehensionsBindInContextEquivalent(EQ) }
        )

    fun <F> Bracket<F, Throwable>.bracketCaseWithJustUnitEqvMap(cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
        forAll(genConstructor(Gen.int(), cf), genFunctionAToB(Gen.int())
        ) { fa: Kind<F, Int>, f: (Int) -> Int ->
            fa.bracketCase(release = { a, b -> just(Unit) }, use = { a -> just(f(a)) })
                .equalUnderTheLaw(fa.map(f), EQ)
        }
    //F.bracketCase(fa)(a => f(a).pure[F])((_, _) => F.unit) <-> F.map(fa)(f)
//
//    fun <F> MonadDefer<F>.asyncBind(EQ: Eq<Kind<F, Int>>): Unit =
//        forAll(genIntSmall(), genIntSmall(), genIntSmall()) { x: Int, y: Int, z: Int ->
//            val (bound, dispose) = bindingCancellable {
//                val a = bindDefer { x }
//                val b = bindDefer { a + y }
//                val c = bindDefer { b + z }
//                c
//            }
//            bound.equalUnderTheLaw(just<Int>(x + y + z), EQ)
//        }
//
//    fun <F> MonadDefer<F>.asyncBindError(EQ: Eq<Kind<F, Int>>): Unit =
//        forAll(genThrowable()) { e: Throwable ->
//            val (bound: Kind<F, Int>, cancel) = bindingCancellable<F, Int> {
//                bindDefer { throw e }
//            }
//            bound.equalUnderTheLaw(raiseError<Int>(e), EQ)
//        }
//
//    fun <F> MonadDefer<F>.asyncBindUnsafe(EQ: Eq<Kind<F, Int>>): Unit =
//        forAll(genIntSmall(), genIntSmall(), genIntSmall()) { x: Int, y: Int, z: Int ->
//            val (bound, dispose) = bindingCancellable {
//                val a = bindDeferUnsafe { Right(x) }
//                val b = bindDeferUnsafe { Right(a + y) }
//                val c = bindDeferUnsafe { Right(b + z) }
//                c
//            }
//            bound.equalUnderTheLaw(just<Int>(x + y + z), EQ)
//        }
//
//    fun <F> MonadDefer<F>.asyncBindUnsafeError(EQ: Eq<Kind<F, Int>>): Unit =
//        forAll(genThrowable()) { e: Throwable ->
//            val (bound: Kind<F, Int>, dispose) = bindingCancellable<F, Int> {
//                bindDeferUnsafe { Left(e) }
//            }
//            bound.equalUnderTheLaw(raiseError<Int>(e), EQ)
//        }
//
//    fun <F> MonadDefer<F>.asyncParallelBind(EQ: Eq<Kind<F, Int>>): Unit =
//        forAll(genIntSmall(), genIntSmall(), genIntSmall()) { x: Int, y: Int, z: Int ->
//            val (bound, dispose) = bindingCancellable {
//                val value = bind { tupled(invoke { x }, invoke { y }, invoke { z }) }
//                value.a + value.b + value.c
//            }
//            bound.equalUnderTheLaw(just<Int>(x + y + z), EQ)
//        }
//
//    fun <F> MonadDefer<F>.asyncCancellationBefore(EQ: Eq<Kind<F, Int>>): Unit =
//        forFew(5, genIntSmall()) { num: Int ->
//            val sideEffect = SideEffect()
//            val (binding, dispose) = bindingCancellable {
//                val a = bindDefer { Thread.sleep(500); num }
//                sideEffect.increment()
//                val b = bindDefer { a + 1 }
//                val c = just(b + 1).bind()
//                c
//            }
//            Try { Thread.sleep(250); dispose() }.recover { throw it }
//            binding.equalUnderTheLaw(raiseError(BindingCancellationException()), EQ) && sideEffect.counter == 0
//        }
//
//    fun <F> MonadDefer<F>.asyncCancellationAfter(EQ: Eq<Kind<F, Int>>): Unit =
//        forFew(5, genIntSmall()) { num: Int ->
//            val sideEffect = SideEffect()
//            val (binding, dispose) = bindingCancellable {
//                val a = bindDefer { num }
//                sideEffect.increment()
//                val b = bindDefer { Thread.sleep(500); sideEffect.increment(); a + 1 }
//                b
//            }
//            Try { Thread.sleep(250); dispose() }.recover { throw it }
//            binding.equalUnderTheLaw(raiseError(BindingCancellationException()), EQ)
//                && sideEffect.counter == 0
//        }
//
//    fun <F> MonadDefer<F>.inContextCancellationBefore(EQ: Eq<Kind<F, Int>>): Unit =
//        forFew(5, genIntSmall()) { num: Int ->
//            val sideEffect = SideEffect()
//            val (binding, dispose) = bindingCancellable {
//                val a = bindIn(CommonPool) { Thread.sleep(500); num }
//                sideEffect.increment()
//                val b = bindIn(CommonPool) { a + 1 }
//                val c = just(b + 1).bind()
//                c
//            }
//            Try { Thread.sleep(250); dispose() }.recover { throw it }
//            binding.equalUnderTheLaw(raiseError(BindingCancellationException()), EQ) && sideEffect.counter == 0
//        }
//
//    fun <F> MonadDefer<F>.inContextCancellationAfter(EQ: Eq<Kind<F, Int>>): Unit =
//        forFew(5, genIntSmall()) { num: Int ->
//            val sideEffect = SideEffect()
//            val (binding, dispose) = bindingCancellable {
//                val a = bindIn(CommonPool) { num }
//                sideEffect.increment()
//                val b = bindIn(CommonPool) { Thread.sleep(500); sideEffect.increment(); a + 1 }
//                b
//            }
//            Try { Thread.sleep(250); dispose() }.recover { throw it }
//            binding.equalUnderTheLaw(raiseError(BindingCancellationException()), EQ)
//                && sideEffect.counter == 0
//        }
//
//    fun <F> MonadDefer<F>.inContextErrorThrow(EQ: Eq<Kind<F, Int>>): Unit =
//        forFew(5, genThrowable()) { throwable: Throwable ->
//            bindingCancellable {
//                val a: Int = bindIn(newSingleThreadContext("1")) { throw throwable }
//                a
//            }.a.equalUnderTheLaw(raiseError(throwable), EQ)
//        }
//
//    fun <F> MonadDefer<F>.monadComprehensionsBindInContextEquivalent(EQ: Eq<Kind<F, Int>>): Unit =
//        forFew(5, genIntSmall()) { num: Int ->
//            val bindM = bindingCancellable {
//                val a = bindDeferIn(newSingleThreadContext("$num")) { num + 1 }
//                val b = bindDeferIn(newSingleThreadContext("$a")) { a + 1 }
//                b
//            }
//            val bind = bindingCancellable {
//                val a = bindIn(newSingleThreadContext("$num")) { num + 1 }
//                val b = bindIn(newSingleThreadContext("$a")) { a + 1 }
//                b
//            }
//            bindM.a.equalUnderTheLaw(bind.a, EQ)
//        }
}
