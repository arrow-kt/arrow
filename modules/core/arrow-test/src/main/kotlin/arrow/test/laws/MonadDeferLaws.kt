package arrow.test.laws

import arrow.Kind
import arrow.core.*
import arrow.data.k
import arrow.effects.data.internal.BindingCancellationException
import arrow.effects.typeclasses.MonadDefer
import arrow.instances.list.foldable.foldLeft
import arrow.test.concurrency.SideEffect
import arrow.test.generators.genIntSmall
import arrow.test.generators.genThrowable
import arrow.typeclasses.Eq
import io.kotlintest.matchers.Matcher
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.forAll
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
      Law("Sync bind: binding blocks") { SC.asyncBind(EQ) },
      Law("Sync bind: binding failure") { SC.asyncBindError(EQERR) },
      Law("Sync bind: unsafe binding") { SC.asyncBindUnsafe(EQ) },
      Law("Sync bind: unsafe binding failure") { SC.asyncBindUnsafeError(EQERR) },
      Law("Sync bind: binding in parallel") { SC.asyncParallelBind(EQ) },
      Law("Sync bind: binding cancellation before flatMap") { SC.asyncCancellationBefore(EQ) },
      Law("Sync bind: binding cancellation after flatMap") { SC.asyncCancellationAfter(EQ) },
      Law("Sync bind: bindingInContext cancellation before flatMap") { SC.inContextCancellationBefore(EQ) },
      Law("Sync bind: bindingInContext cancellation after flatMap") { SC.inContextCancellationAfter(EQ) },
      Law("Sync bind: bindingInContext throw equivalent to raiseError") { SC.inContextErrorThrow(EQERR) },
      Law("Sync bind: monad comprehensions binding in other threads equivalence") { SC.monadComprehensionsBindInContextEquivalent(EQ) },
      Law("Sync laws: delay constant equals pure") { SC.delayConstantEqualsPure(EQ) },
      Law("Sync laws: delay throw equals raiseError") { SC.delayThrowEqualsRaiseError(EQERR) },
      Law("Sync laws: defer constant equals pure") { SC.deferConstantEqualsPure(EQ) },
      Law("Sync laws: deferUnsafe constant right equals pure") { SC.deferUnsafeConstantRightEqualsPure(EQ) },
      Law("Sync laws: deferUnsafe constant left equals raiseError") { SC.deferUnsafeConstantLeftEqualsRaiseError(EQERR) },
      Law("Sync laws: propagate error through bind") { SC.propagateErrorsThroughBind(EQERR) },
      Law("Sync laws: defer suspens evaluation") { SC.deferSuspendsEvaluation(EQ) },
      Law("Sync laws: delay suspends evaluation") { SC.delaySuspendsEvaluation(EQ) },
      Law("Sync laws: flatMap suspends evaluation") { SC.flatMapSuspendsEvaluation(EQ) },
      Law("Sync laws: map suspends evaluation") { SC.mapSuspendsEvaluation(EQ) },
      Law("Sync laws: Repeated evaluation not memoized") { SC.repeatedSyncEvaluationNotMemoized(EQ) }
    ) + if (testStackSafety) {
      listOf(
        Law("Sync laws: stack safety over repeated left binds") { SC.stackSafetyOverRepeatedLeftBinds(5000, EQ) },
        Law("Sync laws: stack safety over repeated right binds") { SC.stackSafetyOverRepeatedRightBinds(5000, EQ) },
        Law("Sync laws: stack safety over repeated attempts") { SC.stackSafetyOverRepeatedAttempts(5000, EQ) },
        Law("Sync laws: stack safety over repeated maps") { SC.stackSafetyOnRepeatedMaps(5000, EQ) }
      )
    } else {
      emptyList()
    }

  fun <F> MonadDefer<F>.delayConstantEqualsPure(EQ: Eq<Kind<F, Int>>): Unit {
    forAll(genIntSmall()) { x ->
      delay { x }.equalUnderTheLaw(just(x), EQ)
    }
  }

  fun <F> MonadDefer<F>.deferConstantEqualsPure(EQ: Eq<Kind<F, Int>>): Unit {
    forAll(genIntSmall()) { x ->
      defer { just(x) }.equalUnderTheLaw(just(x), EQ)
    }
  }

  fun <F> MonadDefer<F>.deferUnsafeConstantRightEqualsPure(EQ: Eq<Kind<F, Int>>): Unit {
    forAll(genIntSmall()) { x ->
      deferUnsafe { x.right() }.equalUnderTheLaw(just(x), EQ)
    }
  }

  fun <F> MonadDefer<F>.deferUnsafeConstantLeftEqualsRaiseError(EQERR: Eq<Kind<F, Int>>): Unit {
    forFew(5, genThrowable()) { t ->
      deferUnsafe { t.left() }.equalUnderTheLaw(raiseError(t), EQERR)
    }
  }

  fun <F> MonadDefer<F>.delayThrowEqualsRaiseError(EQERR: Eq<Kind<F, Int>>): Unit {
    forFew(5, genThrowable()) { t ->
      delay { throw t }.equalUnderTheLaw(raiseError(t), EQERR)
    }
  }

  fun <F> MonadDefer<F>.propagateErrorsThroughBind(EQERR: Eq<Kind<F, Int>>): Unit {
    forFew(5, genThrowable()) { t ->
      delay { throw t }.flatMap<Int, Int> { a: Int -> just(a) }.equalUnderTheLaw(raiseError(t), EQERR)
    }
  }

  fun <F> MonadDefer<F>.deferSuspendsEvaluation(EQ: Eq<Kind<F, Int>>): Unit {
    val sideEffect = SideEffect(counter = 0)
    val df = defer { sideEffect.increment(); just(sideEffect.counter) }

    Thread.sleep(10)

    sideEffect.counter shouldBe 0
    df.equalUnderTheLaw(just(1), EQ) shouldBe true
  }

  fun <F> MonadDefer<F>.delaySuspendsEvaluation(EQ: Eq<Kind<F, Int>>): Unit {
    val sideEffect = SideEffect(counter = 0)
    val df = delay { sideEffect.increment(); sideEffect.counter }

    Thread.sleep(10)

    sideEffect.counter shouldBe 0
    df.equalUnderTheLaw(just(1), EQ) shouldBe true
  }

  fun <F> MonadDefer<F>.flatMapSuspendsEvaluation(EQ: Eq<Kind<F, Int>>): Unit {
    val sideEffect = SideEffect(counter = 0)
    val df = just(0).flatMap { sideEffect.increment(); just(sideEffect.counter) }

    Thread.sleep(10)

    sideEffect.counter shouldBe 0
    df.equalUnderTheLaw(just(1), EQ) shouldBe true
  }

  fun <F> MonadDefer<F>.mapSuspendsEvaluation(EQ: Eq<Kind<F, Int>>): Unit {
    val sideEffect = SideEffect(counter = 0)
    val df = just(0).map { sideEffect.increment(); sideEffect.counter }

    Thread.sleep(10)

    sideEffect.counter shouldBe 0
    df.equalUnderTheLaw(just(1), EQ) shouldBe true
  }

  fun <F> MonadDefer<F>.repeatedSyncEvaluationNotMemoized(EQ: Eq<Kind<F, Int>>): Unit {
    val sideEffect = SideEffect()
    val df = delay { sideEffect.increment(); sideEffect.counter }

    df.flatMap { df }.flatMap { df }.equalUnderTheLaw(just(3), EQ) shouldBe true
  }

  fun <F> MonadDefer<F>.stackSafetyOverRepeatedLeftBinds(iterations: Int = 5000, EQ: Eq<Kind<F, Int>>): Unit {
    (0..iterations).toList().k().foldLeft(just(0)) { def, x ->
      def.flatMap { just(x) }
    }.equalUnderTheLaw(just(iterations), EQ) shouldBe true
  }

  fun <F> MonadDefer<F>.stackSafetyOverRepeatedRightBinds(iterations: Int = 5000, EQ: Eq<Kind<F, Int>>): Unit {
    (0..iterations).toList().foldRight(just(iterations)) { x, def ->
      lazy().flatMap { def }
    }.equalUnderTheLaw(just(iterations), EQ) shouldBe true
  }

  fun <F> MonadDefer<F>.stackSafetyOverRepeatedAttempts(iterations: Int = 5000, EQ: Eq<Kind<F, Int>>): Unit {
    (0..iterations).toList().foldLeft(just(0)) { def, x ->
      def.attempt().map { x }
    }.equalUnderTheLaw(just(iterations), EQ) shouldBe true
  }

  fun <F> MonadDefer<F>.stackSafetyOnRepeatedMaps(iterations: Int = 5000, EQ: Eq<Kind<F, Int>>): Unit {
    (0..iterations).toList().foldLeft(just(0)) { def, x ->
      def.map { x }
    }.equalUnderTheLaw(just(iterations), EQ) shouldBe true
  }

  fun <F> MonadDefer<F>.asyncBind(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(genIntSmall(), genIntSmall(), genIntSmall()) { x: Int, y: Int, z: Int ->
      val (bound, _) = bindingCancellable {
        val a = bindDefer { x }
        val b = bindDefer { a + y }
        val c = bindDefer { b + z }
        c
      }
      bound.equalUnderTheLaw(just(x + y + z), EQ)
    }

  fun <F> MonadDefer<F>.asyncBindError(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(genThrowable()) { e: Throwable ->
      val (bound: Kind<F, Int>, _) = bindingCancellable<Int> {
        bindDefer { throw e }
      }
      bound.equalUnderTheLaw(raiseError(e), EQ)
    }

  fun <F> MonadDefer<F>.asyncBindUnsafe(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(genIntSmall(), genIntSmall(), genIntSmall()) { x: Int, y: Int, z: Int ->
      val (bound, _) = bindingCancellable {
        val a = bindDeferUnsafe { Right(x) }
        val b = bindDeferUnsafe { Right(a + y) }
        val c = bindDeferUnsafe { Right(b + z) }
        c
      }
      bound.equalUnderTheLaw(just<Int>(x + y + z), EQ)
    }

  fun <F> MonadDefer<F>.asyncBindUnsafeError(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(genThrowable()) { e: Throwable ->
      val (bound: Kind<F, Int>, _) = bindingCancellable<Int> {
        bindDeferUnsafe { Left(e) }
      }
      bound.equalUnderTheLaw(raiseError(e), EQ)
    }

  fun <F> MonadDefer<F>.asyncParallelBind(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(genIntSmall(), genIntSmall(), genIntSmall()) { x: Int, y: Int, z: Int ->
      val (bound, _) = bindingCancellable {
        val value = bind { tupled(delay { x }, delay { y }, delay { z }) }
        value.a + value.b + value.c
      }
      bound.equalUnderTheLaw(just(x + y + z), EQ)
    }

  fun <F> MonadDefer<F>.asyncCancellationBefore(EQ: Eq<Kind<F, Int>>): Unit =
    forFew(5, genIntSmall()) { num: Int ->
      val sideEffect = SideEffect()
      val (binding, dispose) = bindingCancellable {
        val a = bindDefer { Thread.sleep(500); num }
        sideEffect.increment()
        val b = bindDefer { a + 1 }
        val c = just(b + 1).bind()
        c
      }
      Try { Thread.sleep(250); dispose() }.recover { throw it }
      binding.equalUnderTheLaw(raiseError(BindingCancellationException()), EQ) && sideEffect.counter == 0
    }

  fun <F> MonadDefer<F>.asyncCancellationAfter(EQ: Eq<Kind<F, Int>>): Unit =
    forFew(5, genIntSmall()) { num: Int ->
      val sideEffect = SideEffect()
      val (binding, dispose) = bindingCancellable {
        val a = bindDefer { num }
        sideEffect.increment()
        val b = bindDefer { Thread.sleep(500); sideEffect.increment(); a + 1 }
        b
      }
      Try { Thread.sleep(250); dispose() }.recover { throw it }
      binding.equalUnderTheLaw(raiseError(BindingCancellationException()), EQ)
        && sideEffect.counter == 0
    }

  fun <F> MonadDefer<F>.inContextCancellationBefore(EQ: Eq<Kind<F, Int>>): Unit =
    forFew(5, genIntSmall()) { num: Int ->
      val sideEffect = SideEffect()
      val (binding, dispose) = bindingCancellable {
        val a = bindIn(Dispatchers.Default) { Thread.sleep(500); num }
        sideEffect.increment()
        val b = bindIn(Dispatchers.Default) { a + 1 }
        val c = just(b + 1).bind()
        c
      }
      Try { Thread.sleep(250); dispose() }.recover { throw it }
      binding.equalUnderTheLaw(raiseError(BindingCancellationException()), EQ) && sideEffect.counter == 0
    }

  fun <F> MonadDefer<F>.inContextCancellationAfter(EQ: Eq<Kind<F, Int>>): Unit =
    forFew(5, genIntSmall()) { num: Int ->
      val sideEffect = SideEffect()
      val (binding, dispose) = bindingCancellable {
        val a = bindIn(Dispatchers.Default) { num }
        sideEffect.increment()
        val b = bindIn(Dispatchers.Default) { Thread.sleep(500); sideEffect.increment(); a + 1 }
        b
      }
      Try { Thread.sleep(250); dispose() }.recover { throw it }
      binding.equalUnderTheLaw(raiseError(BindingCancellationException()), EQ)
        && sideEffect.counter == 0
    }

  @Suppress("UNREACHABLE_CODE")
  fun <F> MonadDefer<F>.inContextErrorThrow(EQ: Eq<Kind<F, Int>>): Unit =
    forFew(5, genThrowable()) { throwable: Throwable ->
      bindingCancellable {
        bindIn(newSingleThreadContext("1")) { throw throwable }
      }.a.equalUnderTheLaw(raiseError(throwable), EQ)
    }

  fun <F> MonadDefer<F>.monadComprehensionsBindInContextEquivalent(EQ: Eq<Kind<F, Int>>): Unit =
    forFew(5, genIntSmall()) { num: Int ->
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
