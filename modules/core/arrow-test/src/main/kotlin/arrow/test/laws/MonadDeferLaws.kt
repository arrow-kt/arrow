package arrow.test.laws

import arrow.Kind
import arrow.core.extensions.eq
import arrow.core.extensions.list.foldable.foldLeft
import arrow.core.k
import arrow.core.left
import arrow.core.right
import arrow.fx.typeclasses.MonadDefer
import arrow.test.concurrency.SideEffect
import arrow.test.generators.GenK
import arrow.test.generators.intSmall
import arrow.test.generators.throwable
import arrow.typeclasses.Apply
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Functor
import arrow.typeclasses.Selective
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe

object MonadDeferLaws {

  private fun <F> monadDeferLaws(
    SC: MonadDefer<F>,
    GENK: GenK<F>,
    EQK: EqK<F>,
    testStackSafety: Boolean = true
  ): List<Law> {
    val EQ = EQK.liftEq(Int.eq())

    return BracketLaws.laws(SC, GENK, EQK) + listOf(
      Law("MonadDefer laws: later constant equals pure") { SC.delayConstantEqualsPure(EQ) },
      Law("MonadDefer laws: later throw equals raiseError") { SC.delayThrowEqualsRaiseError(EQ) },
      Law("MonadDefer laws: later constant equals pure") { SC.deferConstantEqualsPure(EQ) },
      Law("MonadDefer laws: laterOrRaise constant right equals pure") { SC.delayOrRaiseConstantRightEqualsPure(EQ) },
      Law("MonadDefer laws: laterOrRaise constant left equals raiseError") { SC.delayOrRaiseConstantLeftEqualsRaiseError(EQ) },
      Law("MonadDefer laws: propagate error through bind") { SC.propagateErrorsThroughBind(EQ) },
      Law("MonadDefer laws: defer suspends evaluation") { SC.deferSuspendsEvaluation(EQ) },
      Law("MonadDefer laws: later suspends evaluation") { SC.delaySuspendsEvaluation(EQ) },
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
  }

  fun <F> laws(
    SC: MonadDefer<F>,
    GENK: GenK<F>,
    EQK: EqK<F>,
    testStackSafety: Boolean = true
  ): List<Law> =
    BracketLaws.laws(SC, GENK, EQK) +
      monadDeferLaws(SC, GENK, EQK, testStackSafety)

  fun <F> laws(
    SC: MonadDefer<F>,
    FF: Functor<F>,
    AP: Apply<F>,
    SL: Selective<F>,
    GENK: GenK<F>,
    EQK: EqK<F>,
    testStackSafety: Boolean = true
  ): List<Law> =
    BracketLaws.laws(SC, FF, AP, SL, GENK, EQK) +
      monadDeferLaws(SC, GENK, EQK, testStackSafety)

  fun <F> MonadDefer<F>.delayConstantEqualsPure(EQ: Eq<Kind<F, Int>>) {
    forAll(Gen.intSmall()) { x ->
      later { x }.equalUnderTheLaw(just(x), EQ)
    }
  }

  fun <F> MonadDefer<F>.deferConstantEqualsPure(EQ: Eq<Kind<F, Int>>) {
    forAll(Gen.intSmall()) { x ->
      defer { just(x) }.equalUnderTheLaw(just(x), EQ)
    }
  }

  fun <F> MonadDefer<F>.delayOrRaiseConstantRightEqualsPure(EQ: Eq<Kind<F, Int>>) {
    forAll(Gen.intSmall()) { x ->
      laterOrRaise { x.right() }.equalUnderTheLaw(just(x), EQ)
    }
  }

  fun <F> MonadDefer<F>.delayOrRaiseConstantLeftEqualsRaiseError(EQERR: Eq<Kind<F, Int>>) {
    forFew(5, Gen.throwable()) { t ->
      laterOrRaise { t.left() }.equalUnderTheLaw(raiseError(t), EQERR)
    }
  }

  fun <F> MonadDefer<F>.delayThrowEqualsRaiseError(EQERR: Eq<Kind<F, Int>>) {
    forFew(5, Gen.throwable()) { t ->
      later { throw t }.equalUnderTheLaw(raiseError(t), EQERR)
    }
  }

  fun <F> MonadDefer<F>.propagateErrorsThroughBind(EQERR: Eq<Kind<F, Int>>) {
    forFew(5, Gen.throwable()) { t ->
      later { throw t }.flatMap<Int, Int> { a: Int -> just(a) }.equalUnderTheLaw(raiseError(t), EQERR)
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
    val df = later { sideEffect.increment(); sideEffect.counter }

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
    val df = later { sideEffect.increment(); sideEffect.counter }

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
}
