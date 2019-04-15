package arrow.test.laws

import arrow.Kind
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.Try
import arrow.core.left
import arrow.core.recover
import arrow.core.right
import arrow.data.k
import arrow.data.extensions.list.foldable.foldLeft
import arrow.effects.data.internal.BindingCancellationException
import arrow.effects.typeclasses.MonadDefer
import arrow.test.concurrency.SideEffect
import arrow.test.generators.intSmall
import arrow.test.generators.throwable
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext

object MonadDeferLaws {

  fun <F> laws(
    SC: MonadDefer<F>,
    EQ: Eq<Kind<F, Int>>,
    EQ_EITHER: Eq<Kind<F, Either<Throwable, Int>>>,
    EQERR: Eq<Kind<F, Int>> = EQ,
    testStackSafety: Boolean = true
  ): List<Law> =
    BracketLaws.laws(SC, EQ, EQ_EITHER, EQERR) + listOf(
      Law("MonadDefer bind: binding blocks") { SC.asyncBind(EQ) },
      Law("MonadDefer bind: binding failure") { SC.asyncBindError(EQERR) },
      Law("MonadDefer bind: unsafe binding") { SC.asyncBindUnsafe(EQ) },
      Law("MonadDefer bind: unsafe binding failure") { SC.asyncBindUnsafeError(EQERR) },
      Law("MonadDefer bind: binding in parallel") { SC.asyncParallelBind(EQ) },
      Law("MonadDefer bind: binding cancellation before flatMap") { SC.asyncCancellationBefore(EQ) },
      Law("MonadDefer bind: binding cancellation after flatMap") { SC.asyncCancellationAfter(EQ) },
      Law("MonadDefer bind: bindingInContext cancellation before flatMap") { SC.inContextCancellationBefore(EQ) },
      Law("MonadDefer bind: bindingInContext cancellation after flatMap") { SC.inContextCancellationAfter(EQ) },
      Law("MonadDefer bind: bindingInContext throw equivalent to raiseError") { SC.inContextErrorThrow(EQERR) },
      Law("MonadDefer bind: monad comprehensions binding in other threads equivalence") { SC.monadComprehensionsBindInContextEquivalent(EQ) },
      Law("MonadDefer laws: delay constant equals pure") { SC.delayConstantEqualsPure(EQ) },
      Law("MonadDefer laws: delay throw equals raiseError") { SC.delayThrowEqualsRaiseError(EQERR) },
      Law("MonadDefer laws: defer constant equals pure") { SC.deferConstantEqualsPure(EQ) },
      Law("MonadDefer laws: delayOrRaise constant right equals pure") { SC.delayOrRaiseConstantRightEqualsPure(EQ) },
      Law("MonadDefer laws: delayOrRaise constant left equals raiseError") { SC.delayOrRaiseConstantLeftEqualsRaiseError(EQERR) },
      Law("MonadDefer laws: propagate error through bind") { SC.propagateErrorsThroughBind(EQERR) },
      Law("MonadDefer laws: defer suspens evaluation") { SC.deferSuspendsEvaluation(EQ) },
      Law("MonadDefer laws: delay suspends evaluation") { SC.delaySuspendsEvaluation(EQ) },
      Law("MonadDefer laws: flatMap suspends evaluation") { SC.flatMapSuspendsEvaluation(EQ) },
      Law("MonadDefer laws: map suspends evaluation") { SC.mapSuspendsEvaluation(EQ) },
      Law("MonadDefer laws: Repeated evaluation not memoized") { SC.repeatedSyncEvaluationNotMemoized(EQ) }
    ) + if (testStackSafety) {
      listOf(
        Law("MonadDefer laws: stack safety over repeated left binds") { SC.stackSafetyOverRepeatedLeftBinds(5000, EQ) },
        Law("MonadDefer laws: stack safety over repeated right binds") { SC.stackSafetyOverRepeatedRightBinds(5000, EQ) },
        Law("MonadDefer laws: stack safety over repeated attempts") { SC.stackSafetyOverRepeatedAttempts(5000, EQ) },
        Law("MonadDefer laws: stack safety over repeated maps") { SC.stackSafetyOnRepeatedMaps(5000, EQ) }
      )
    } else {
      emptyList()
    }

  fun <F> MonadDefer<F>.delayConstantEqualsPure(EQ: Eq<Kind<F, Int>>) {
    forAll(Gen.intSmall()) { x ->
      delay { x }.equalUnderTheLaw(just(x), EQ)
    }
  }

  fun <F> MonadDefer<F>.deferConstantEqualsPure(EQ: Eq<Kind<F, Int>>) {
    forAll(Gen.intSmall()) { x ->
      defer { just(x) }.equalUnderTheLaw(just(x), EQ)
    }
  }

  fun <F> MonadDefer<F>.delayOrRaiseConstantRightEqualsPure(EQ: Eq<Kind<F, Int>>) {
    forAll(Gen.intSmall()) { x ->
      delayOrRaise { x.right() }.equalUnderTheLaw(just(x), EQ)
    }
  }

  fun <F> MonadDefer<F>.delayOrRaiseConstantLeftEqualsRaiseError(EQERR: Eq<Kind<F, Int>>) {
    forFew(5, Gen.throwable()) { t ->
      delayOrRaise { t.left() }.equalUnderTheLaw(raiseError(t), EQERR)
    }
  }

  fun <F> MonadDefer<F>.delayThrowEqualsRaiseError(EQERR: Eq<Kind<F, Int>>) {
    forFew(5, Gen.throwable()) { t ->
      delay { throw t }.equalUnderTheLaw(raiseError(t), EQERR)
    }
  }

  fun <F> MonadDefer<F>.propagateErrorsThroughBind(EQERR: Eq<Kind<F, Int>>) {
    forFew(5, Gen.throwable()) { t ->
      delay { throw t }.flatMap<Int, Int> { a: Int -> just(a) }.equalUnderTheLaw(raiseError(t), EQERR)
    }
  }

  fun <F> MonadDefer<F>.deferSuspendsEvaluation(EQ: Eq<Kind<F, Int>>) {
    val sideEffect = SideEffect(counter = 0)
    val df = defer { sideEffect.increment(); just(sideEffect.counter) }

    Thread.sleep(10)

    sideEffect.counter shouldBe 0
    df.equalUnderTheLaw(just(1), EQ) shouldBe true
  }

  fun <F> MonadDefer<F>.delaySuspendsEvaluation(EQ: Eq<Kind<F, Int>>) {
    val sideEffect = SideEffect(counter = 0)
    val df = delay { sideEffect.increment(); sideEffect.counter }

    Thread.sleep(10)

    sideEffect.counter shouldBe 0
    df.equalUnderTheLaw(just(1), EQ) shouldBe true
  }

  fun <F> MonadDefer<F>.flatMapSuspendsEvaluation(EQ: Eq<Kind<F, Int>>) {
    val sideEffect = SideEffect(counter = 0)
    val df = just(0).flatMap { sideEffect.increment(); just(sideEffect.counter) }

    Thread.sleep(10)

    sideEffect.counter shouldBe 0
    df.equalUnderTheLaw(just(1), EQ) shouldBe true
  }

  fun <F> MonadDefer<F>.mapSuspendsEvaluation(EQ: Eq<Kind<F, Int>>) {
    val sideEffect = SideEffect(counter = 0)
    val df = just(0).map { sideEffect.increment(); sideEffect.counter }

    Thread.sleep(10)

    sideEffect.counter shouldBe 0
    df.equalUnderTheLaw(just(1), EQ) shouldBe true
  }

  fun <F> MonadDefer<F>.repeatedSyncEvaluationNotMemoized(EQ: Eq<Kind<F, Int>>) {
    val sideEffect = SideEffect()
    val df = delay { sideEffect.increment(); sideEffect.counter }

    df.flatMap { df }.flatMap { df }.equalUnderTheLaw(just(3), EQ) shouldBe true
  }

  fun <F> MonadDefer<F>.stackSafetyOverRepeatedLeftBinds(iterations: Int = 5000, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.create { Unit }) {
      (0..iterations).toList().k().foldLeft(just(0)) { def, x ->
        def.flatMap { just(x) }
      }.equalUnderTheLaw(just(iterations), EQ)
    }

  fun <F> MonadDefer<F>.stackSafetyOverRepeatedRightBinds(iterations: Int = 5000, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.create { Unit }) {
      (0..iterations).toList().foldRight(just(iterations)) { x, def ->
        lazy().flatMap { def }
      }.equalUnderTheLaw(just(iterations), EQ)
    }

  fun <F> MonadDefer<F>.stackSafetyOverRepeatedAttempts(iterations: Int = 5000, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.create { Unit }) {
      (0..iterations).toList().foldLeft(just(0)) { def, x ->
        def.attempt().map { x }
      }.equalUnderTheLaw(just(iterations), EQ)
    }

  fun <F> MonadDefer<F>.stackSafetyOnRepeatedMaps(iterations: Int = 5000, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.create { Unit }) {
      (0..iterations).toList().foldLeft(just(0)) { def, x ->
        def.map { x }
      }.equalUnderTheLaw(just(iterations), EQ)
    }

  fun <F> MonadDefer<F>.asyncBind(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.intSmall(), Gen.intSmall(), Gen.intSmall()) { x: Int, y: Int, z: Int ->
      val (bound, _) = bindingCancellable {
        val a = bindDefer { x }
        val b = bindDefer { a + y }
        val c = bindDefer { b + z }
        c
      }
      bound.equalUnderTheLaw(just(x + y + z), EQ)
    }

  fun <F> MonadDefer<F>.asyncBindError(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.throwable()) { e: Throwable ->
      val (bound: Kind<F, Int>, _) = bindingCancellable<Int> {
        bindDefer { throw e }
      }
      bound.equalUnderTheLaw(raiseError(e), EQ)
    }

  fun <F> MonadDefer<F>.asyncBindUnsafe(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.intSmall(), Gen.intSmall(), Gen.intSmall()) { x: Int, y: Int, z: Int ->
      val (bound, _) = bindingCancellable {
        val a = bindDelayOrRaise { Right(x) }
        val b = bindDelayOrRaise { Right(a + y) }
        val c = bindDelayOrRaise { Right(b + z) }
        c
      }
      bound.equalUnderTheLaw(just<Int>(x + y + z), EQ)
    }

  fun <F> MonadDefer<F>.asyncBindUnsafeError(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.throwable()) { e: Throwable ->
      val (bound: Kind<F, Int>, _) = bindingCancellable<Int> {
        bindDelayOrRaise { Left(e) }
      }
      bound.equalUnderTheLaw(raiseError(e), EQ)
    }

  fun <F> MonadDefer<F>.asyncParallelBind(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.intSmall(), Gen.intSmall(), Gen.intSmall()) { x: Int, y: Int, z: Int ->
      val (bound, _) = bindingCancellable {
        val value = bind { tupled(delay { x }, delay { y }, delay { z }) }
        value.a + value.b + value.c
      }
      bound.equalUnderTheLaw(just(x + y + z), EQ)
    }

  fun <F> MonadDefer<F>.asyncCancellationBefore(EQ: Eq<Kind<F, Int>>): Unit =
    forFew(5, Gen.intSmall()) { num: Int ->
      val sideEffect = SideEffect()
      val (binding, dispose) = bindingCancellable {
        val a = bindDefer { Thread.sleep(20); num }
        sideEffect.increment()
        val b = bindDefer { a + 1 }
        val (c) = just(b + 1)
        c
      }
      Try { Thread.sleep(10); dispose() }.recover { throw it }
      binding.equalUnderTheLaw(raiseError(BindingCancellationException()), EQ) && sideEffect.counter == 0
    }

  fun <F> MonadDefer<F>.asyncCancellationAfter(EQ: Eq<Kind<F, Int>>): Unit =
    forFew(5, Gen.intSmall()) { num: Int ->
      val sideEffect = SideEffect()
      val (binding, dispose) = bindingCancellable {
        val a = bindDefer { num }
        sideEffect.increment()
        val b = bindDefer { Thread.sleep(20); sideEffect.increment(); a + 1 }
        b
      }
      Try { Thread.sleep(10); dispose() }.recover { throw it }
      binding.equalUnderTheLaw(raiseError(BindingCancellationException()), EQ) &&
        sideEffect.counter == 0
    }

  fun <F> MonadDefer<F>.inContextCancellationBefore(EQ: Eq<Kind<F, Int>>): Unit =
    forFew(5, Gen.intSmall()) { num: Int ->
      val sideEffect = SideEffect()
      val (binding, dispose) = bindingCancellable {
        val a = bindIn(Dispatchers.Default) { Thread.sleep(20); num }
        sideEffect.increment()
        val b = bindIn(Dispatchers.Default) { a + 1 }
        val (c) = just(b + 1)
        c
      }
      Try { Thread.sleep(10); dispose() }.recover { throw it }
      binding.equalUnderTheLaw(raiseError(BindingCancellationException()), EQ) && sideEffect.counter == 0
    }

  fun <F> MonadDefer<F>.inContextCancellationAfter(EQ: Eq<Kind<F, Int>>): Unit =
    forFew(5, Gen.intSmall()) { num: Int ->
      val sideEffect = SideEffect()
      val (binding, dispose) = bindingCancellable {
        val a = bindIn(Dispatchers.Default) { num }
        sideEffect.increment()
        val b = bindIn(Dispatchers.Default) { Thread.sleep(20); sideEffect.increment(); a + 1 }
        b
      }
      Try { Thread.sleep(10); dispose() }.recover { throw it }
      binding.equalUnderTheLaw(raiseError(BindingCancellationException()), EQ) &&
        sideEffect.counter == 0
    }

  @Suppress("UNREACHABLE_CODE")
  fun <F> MonadDefer<F>.inContextErrorThrow(EQ: Eq<Kind<F, Int>>): Unit =
    forFew(5, Gen.throwable()) { throwable: Throwable ->
      bindingCancellable {
        bindIn(newSingleThreadContext("1")) { throw throwable }
      }.a.equalUnderTheLaw(raiseError(throwable), EQ)
    }

  fun <F> MonadDefer<F>.monadComprehensionsBindInContextEquivalent(EQ: Eq<Kind<F, Int>>): Unit =
    forFew(5, Gen.intSmall()) { num: Int ->
      val bindM = bindingCancellable {
        val a = bindDeferIn(newSingleThreadContext("$num")) { num + 1 }
        val b = bindDeferIn(newSingleThreadContext("$a")) { a + 1 }
        b
      }
      val bind = bindingCancellable {
        val a = bindIn(newSingleThreadContext("$num")) { num + 1 }
        val b = bindIn(newSingleThreadContext("$a")) { a + 1 }
        b
      }
      bindM.a.equalUnderTheLaw(bind.a, EQ)
    }
}
