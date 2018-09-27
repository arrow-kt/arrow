package arrow.test.laws

import arrow.Kind
import arrow.core.Either
import arrow.effects.typeclasses.Bracket
import arrow.effects.typeclasses.ExitCase
import arrow.test.generators.genConstructor
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genThrowable
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
      Law("Bracket: bracketCase with just Unit is eqv to Map") { BF.bracketCaseWithJustUnitEqvMap(cf, EQ) },
      Law("Bracket: bracketCase with just Unit is uncancelable") { BF.bracketCaseWithJustUnitIsUncancelable(cf, EQ) },
      Law("Bracket: bracketCase failure in acquisition remains failure") { BF.bracketCaseFailureInAcquisitionRemainsFailure(cf, EQ) },
      Law("Bracket: bracket is derived from bracketCase") { BF.bracketIsDerivedFromBracketCase(cf, EQ) },
      Law("Bracket: uncancelable prevents Cancelled case") { BF.uncancelablePreventsCanceledCase(cf, BF.just(Unit), BF.just(Unit), EQ) },
      Law("Bracket: acquire and release are uncancelable") { BF.acquireAndReleaseAreUncancelable(cf, { BF.just(Unit) }, EQ) },
      Law("Bracket: guarantee is derived from bracket") { BF.guaranteeIsDerivedFromBracket(cf, BF.just(Unit), EQ) },
      Law("Bracket: guaranteeCase is derived from bracketCase") { BF.guaranteeCaseIsDerivedFromBracketCase(cf, { BF.just(Unit) }, EQ) }
    )

  fun <F> Bracket<F, Throwable>.bracketCaseWithJustUnitEqvMap(cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(genConstructor(Gen.int(), cf), genFunctionAToB(Gen.int())
    ) { fa: Kind<F, Int>, f: (Int) -> Int ->
      fa.bracketCase(release = { _, _ -> just(Unit) }, use = { a -> just(f(a)) }).equalUnderTheLaw(fa.map(f), EQ)
    }

  fun <F> Bracket<F, Throwable>.bracketCaseWithJustUnitIsUncancelable(
    cf: (Int) -> Kind<F, Int>,
    EQ: Eq<Kind<F, Int>>): Unit =
    forAll(genConstructor(Gen.int(), cf)) { fa: Kind<F, Int> ->
      fa.bracketCase(release = { _, _ -> just(Unit) }, use = cf).equalUnderTheLaw(fa.uncancelable().flatMap(cf), EQ)
    }

  fun <F> Bracket<F, Throwable>.bracketCaseFailureInAcquisitionRemainsFailure(
    cf: (Int) -> Kind<F, Int>,
    EQ: Eq<Kind<F, Int>>): Unit =
    forAll(genThrowable()) { e ->
      raiseError<Int>(e).bracketCase(release = { _, _ -> just(Unit) }, use = cf).equalUnderTheLaw(raiseError(e), EQ)
    }

  fun <F> Bracket<F, Throwable>.bracketIsDerivedFromBracketCase(
    cf: (Int) -> Kind<F, Int>,
    EQ: Eq<Kind<F, Int>>): Unit =
    forAll(genConstructor(Gen.int(), cf)) { fa: Kind<F, Int> ->
      fa.bracket(release = { _ -> just(Unit) }, use = cf).equalUnderTheLaw(fa.bracketCase(release = { _, _ -> just(Unit) }, use = cf), EQ)
    }

  fun <F> Bracket<F, Throwable>.uncancelablePreventsCanceledCase(
    cf: (Int) -> Kind<F, Int>,
    onCancel: Kind<F, Unit>,
    onFinish: Kind<F, Unit>,
    EQ: Eq<Kind<F, Int>>): Unit =
    forAll(genConstructor(Gen.int(), cf)) { fa: Kind<F, Int> ->
      just(Unit).bracketCase(use = { fa }, release = { _, b ->
        if (b == ExitCase.Cancelled) onCancel else onFinish
      }).uncancelable().equalUnderTheLaw(fa.guarantee(onFinish), EQ)
    }

  fun <F> Bracket<F, Throwable>.acquireAndReleaseAreUncancelable(
    use: (Int) -> Kind<F, Int>,
    release: (Int) -> Kind<F, Unit>,
    EQ: Eq<Kind<F, Int>>): Unit =
    forAll(genConstructor(Gen.int(), use)) { fa: Kind<F, Int> ->
      fa.uncancelable().bracket(use) { a -> release(a).uncancelable() }.equalUnderTheLaw(fa.bracket(use, release), EQ)
    }

  fun <F> Bracket<F, Throwable>.guaranteeIsDerivedFromBracket(
    cf: (Int) -> Kind<F, Int>,
    finalizer: Kind<F, Unit>,
    EQ: Eq<Kind<F, Int>>): Unit =
    forAll(genConstructor(Gen.int(), cf)) { fa: Kind<F, Int> ->
      fa.guarantee(finalizer).equalUnderTheLaw(just(Unit).bracket({ fa }, { finalizer }), EQ)
    }

  fun <F> Bracket<F, Throwable>.guaranteeCaseIsDerivedFromBracketCase(
    cf: (Int) -> Kind<F, Int>,
    finalizer: (ExitCase<Throwable>) -> Kind<F, Unit>,
    EQ: Eq<Kind<F, Int>>): Unit =
    forAll(genConstructor(Gen.int(), cf)) { fa: Kind<F, Int> ->
      fa.guaranteeCase(finalizer).equalUnderTheLaw(just(Unit).bracketCase({ fa }, { _, e -> finalizer(e) }), EQ)
    }
}
