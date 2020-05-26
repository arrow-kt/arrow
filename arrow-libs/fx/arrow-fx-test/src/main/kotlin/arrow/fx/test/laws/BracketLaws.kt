package arrow.fx.test.laws

import arrow.Kind
import arrow.core.extensions.eq
import arrow.fx.internal.AtomicIntW
import arrow.core.test.generators.GenK
import arrow.core.test.generators.applicativeError
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.throwable
import arrow.core.test.laws.Law
import arrow.core.test.laws.MonadErrorLaws
import arrow.fx.typeclasses.Bracket
import arrow.fx.typeclasses.ExitCase
import arrow.fx.test.generators.raiseError
import arrow.typeclasses.Apply
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Functor
import arrow.typeclasses.Selective
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object BracketLaws {

  private fun <F> bracketLaws(
    BF: Bracket<F, Throwable>,
    EQK: EqK<F>,
    testStackSafety: Boolean,
    iterations: Int
  ): List<Law> {
    val EQ = EQK.liftEq(Int.eq())

    return listOf(
      Law("Bracket: bracketCase with just Unit is eqv to Map") { BF.bracketCaseWithJustUnitEqvMap(EQ) },
      Law("Bracket: bracketCase with just Unit is uncancellable") { BF.bracketCaseWithJustUnitIsUncancellable(EQ) },
      Law("Bracket: bracketCase failure in acquisition remains failure") { BF.bracketCaseFailureInAcquisitionRemainsFailure(EQ) },
      Law("Bracket: uncancellable prevents Cancelled case") { BF.uncancellablePreventsCancelledCase(BF.just(Unit), BF.just(Unit), EQ) },
      Law("Bracket: acquire and release are uncancellable") { BF.acquireAndReleaseAreUncancellable({ BF.just(Unit) }, EQ) },
      Law("Bracket: bracket propagates transformer effects") { BF.bracketPropagatesTransformerEffects(EQ) },
      Law("Bracket: bracket must run release task on use error") { BF.bracketMustRunReleaseTaskOnUseError(EQ) },
      Law("Bracket: bracket must not run release task on acquire error") { BF.bracketMustNotRunReleaseTaskOnAcquireError(EQ) },
      Law("Bracket: guaranteeCase must run finalizer task") { BF.guaranteeCaseMustRunFinalizerOnError(EQ) },
      Law("Bracket: bracket is derived from bracketCase") { BF.bracketIsDerivedFromBracketCase(EQ) },
      Law("Bracket: guarantee is derived from bracket") { BF.guaranteeIsDerivedFromBracket(BF.just(Unit), EQ) },
      Law("Bracket: guaranteeCase is derived from bracketCase") { BF.guaranteeCaseIsDerivedFromBracketCase({ BF.just(Unit) }, EQ) },
      // onCancel cannot be tested as Bracket doesn't have the power to cancel
      Law("Bracket: onError must run finalizer task") { BF.onErrorMustRunFinalizerOnError(EQ) }
    ) + (if (testStackSafety) {
      listOf(
        Law("Bracket: bracket should be stack-safe") { BF.bracketShouldBeStackSafe(iterations, EQ) },
        Law("Bracket: guaranteeCase should be stack-safe") { BF.guaranteeCaseShouldBeStackSafe(iterations, EQ) }
      )
    } else emptyList())
  }

  fun <F> laws(
    BF: Bracket<F, Throwable>,
    GENK: GenK<F>,
    EQK: EqK<F>,
    testStackSafety: Boolean = true,
    iterations: Int = 20_000
  ): List<Law> =
    MonadErrorLaws.laws(BF, GENK, EQK) +
      bracketLaws(BF, EQK, testStackSafety, iterations)

  fun <F> laws(
    BF: Bracket<F, Throwable>,
    FF: Functor<F>,
    AP: Apply<F>,
    SL: Selective<F>,
    GENK: GenK<F>,
    EQK: EqK<F>,
    testStackSafety: Boolean = true,
    iterations: Int = 20_000
  ): List<Law> =
    MonadErrorLaws.laws(BF, FF, AP, SL, GENK, EQK) +
      bracketLaws(BF, EQK, testStackSafety, iterations)

  fun <F> Bracket<F, Throwable>.bracketCaseWithJustUnitEqvMap(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.int().applicativeError(this), Gen.functionAToB<Int, Int>(Gen.int())
    ) { fa: Kind<F, Int>, f: (Int) -> Int ->
      fa.bracketCase(release = { _, _ -> just<Unit>(Unit) }, use = { a -> just(f(a)) }).equalUnderTheLaw(fa.map(f), EQ)
    }

  fun <F> Bracket<F, Throwable>.bracketCaseWithJustUnitIsUncancellable(
    EQ: Eq<Kind<F, Int>>
  ): Unit =
    forAll(Gen.int().applicativeError(this)) { fa: Kind<F, Int> ->
      fa.bracketCase(release = { _, _ -> just<Unit>(Unit) }, use = { just(it) }).equalUnderTheLaw(fa.uncancellable().flatMap { just(it) }, EQ)
    }

  fun <F> Bracket<F, Throwable>.bracketCaseFailureInAcquisitionRemainsFailure(
    EQ: Eq<Kind<F, Int>>
  ): Unit =
    forAll(Gen.throwable()) { e ->
      raiseError<Int>(e).bracketCase(release = { _, _ -> just<Unit>(Unit) }, use = { just(it) }).equalUnderTheLaw(raiseError(e), EQ)
    }

  fun <F> Bracket<F, Throwable>.bracketIsDerivedFromBracketCase(
    EQ: Eq<Kind<F, Int>>
  ): Unit =
    forAll(Gen.int().applicativeError(this)) { fa: Kind<F, Int> ->
      fa.bracket(release = { just<Unit>(Unit) }, use = { just(it) }).equalUnderTheLaw(fa.bracketCase(release = { _, _ -> just<Unit>(Unit) }, use = { just(it) }), EQ)
    }

  fun <F> Bracket<F, Throwable>.uncancellablePreventsCancelledCase(
    onCancel: Kind<F, Unit>,
    onFinish: Kind<F, Unit>,
    EQ: Eq<Kind<F, Int>>
  ): Unit =
    forAll(Gen.int().applicativeError(this)) { fa: Kind<F, Int> ->
      just(Unit).bracketCase(use = { fa }, release = { _, b ->
        if (b == ExitCase.Cancelled) onCancel else onFinish
      }).uncancellable().equalUnderTheLaw(fa.guarantee(onFinish), EQ)
    }

  fun <F> Bracket<F, Throwable>.acquireAndReleaseAreUncancellable(
    release: (Int) -> Kind<F, Unit>,
    EQ: Eq<Kind<F, Int>>
  ): Unit =
    forAll(Gen.int().applicativeError(this)) { fa: Kind<F, Int> ->
      fa.uncancellable().bracket({ a -> release(a).uncancellable() }) { just(it) }.equalUnderTheLaw(fa.bracket(release) { just(it) }, EQ)
    }

  fun <F> Bracket<F, Throwable>.guaranteeIsDerivedFromBracket(
    finalizer: Kind<F, Unit>,
    EQ: Eq<Kind<F, Int>>
  ): Unit =
    forAll(Gen.int().applicativeError(this)) { fa: Kind<F, Int> ->
      fa.guarantee(finalizer).equalUnderTheLaw(just(Unit).bracket({ finalizer }, use = { fa }), EQ)
    }

  fun <F> Bracket<F, Throwable>.guaranteeCaseIsDerivedFromBracketCase(
    finalizer: (ExitCase<Throwable>) -> Kind<F, Unit>,
    EQ: Eq<Kind<F, Int>>
  ): Unit =
    forAll(Gen.int().applicativeError(this)) { fa: Kind<F, Int> ->
      fa.guaranteeCase(finalizer).equalUnderTheLaw(just(Unit).bracketCase({ _, e -> finalizer(e) }) { fa }, EQ)
    }

  fun <F> Bracket<F, Throwable>.bracketPropagatesTransformerEffects(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.string().applicativeError(this),
      Gen.functionAToB<String, Kind<F, Int>>(Gen.int().applicativeError(this)),
      Gen.functionAToB<String, Kind<F, Unit>>(Gen.create { just(Unit) })) { acquire, use, release ->
      acquire.bracket(use = use, release = release).equalUnderTheLaw(
        acquire.flatMap { a -> use(a).flatMap { b -> release(a).map { b } } }, EQ)
    }

  fun <F> Bracket<F, Throwable>.bracketMustRunReleaseTaskOnUseError(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.int()) { i ->
      val msg = AtomicIntW(0)
      just(i).bracket<Int, Int>(
          release = { ii -> unit().map { msg.value = ii } },
          use = { throw Throwable("Expected failure!") }
        )
        .attempt()
        .map { msg.value }
        .equalUnderTheLaw(just(i), EQ)
    }

  fun <F> Bracket<F, Throwable>.bracketMustNotRunReleaseTaskOnAcquireError(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.int(), Gen.int()) { expected, other ->
      val actual = AtomicIntW(expected)
      raiseError<Int>(Throwable("Expected failure!")).bracket(
          release = { unit().map { actual.value = other } },
          use = { just(it) }
        )
        .attempt()
        .map { actual.value }
        .equalUnderTheLaw(just(expected), EQ)
    }

  fun <F> Bracket<F, Throwable>.guaranteeCaseMustRunFinalizerOnError(
    EQ: Eq<Kind<F, Int>>
  ): Unit =
    forAll(Gen.int(), Gen.throwable().raiseError<F, Int, Throwable>(this)) { i, fe ->
      val msg = AtomicIntW(0)
      fe
        .guaranteeCase { unit().map { msg.value = i } }
        .attempt()
        .map { msg.value }
        .equalUnderTheLaw(just(i), EQ)
    }

  fun <F> Bracket<F, Throwable>.onErrorMustRunFinalizerOnError(
    EQ: Eq<Kind<F, Int>>
  ): Unit =
    forAll(
      Gen.throwable().raiseError<F, Int, Throwable>(this),
      Gen.int()
    ) { fe: Kind<F, Int>, i: Int ->
      val msg = AtomicIntW(0)
      fe
        .onError { unit().map { msg.value = i } }
        .attempt()
        .map { msg.value }
        .equalUnderTheLaw(just(i), EQ)
    }

  fun <F> Bracket<F, Throwable>.bracketShouldBeStackSafe(
    iterations: Int,
    EQ: Eq<Kind<F, Int>>
  ) {

    fun bracketLoop(i: Int): Kind<F, Int> =
      unit().bracket(use = { just(i + 1) }, release = { unit() }).flatMap { ii ->
        if (ii < iterations) bracketLoop(ii)
        else just(ii)
      }

    unit().flatMap { bracketLoop(0) }.equalUnderTheLaw(just(iterations), EQ)
  }

  fun <F> Bracket<F, Throwable>.guaranteeCaseShouldBeStackSafe(
    iterations: Int,
    EQ: Eq<Kind<F, Int>>
  ) {
    fun guaranteeCaseLoop(i: Int): Kind<F, Int> =
      unit().guaranteeCase { unit() }.flatMap {
        val ii = i + 1
        if (ii < iterations) guaranteeCaseLoop(ii)
        else just(ii)
      }

    unit().flatMap { guaranteeCaseLoop(0) }.equalUnderTheLaw(just(iterations), EQ)
  }
}
