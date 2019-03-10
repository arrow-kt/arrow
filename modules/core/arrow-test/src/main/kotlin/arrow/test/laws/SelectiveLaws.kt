package arrow.test.laws

import arrow.Kind
import arrow.core.*
import arrow.test.generators.either
import arrow.typeclasses.Eq
import arrow.typeclasses.Selective
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object SelectiveLaws {

  fun <F> laws(A: Selective<F>, EQ: Eq<Kind<F, Int>>): List<Law> =
    ApplicativeLaws.laws(A, EQ) + listOf(
      Law("Selective Laws: identity") { A.identityLaw(EQ) },
      Law("Selective Laws: branch") { A.branchLaw(EQ) },
      Law("Selective Laws: ifS") { A.ifSLaw(EQ) }
    )

  fun <F> Selective<F>.identityLaw(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.either(Gen.int(), Gen.int())) { either ->
      either.fold(
        { l -> just(either).select(just(::identity)).equalUnderTheLaw(just(l), EQ) },
        { r -> just(either).select(just(::identity)).equalUnderTheLaw(just(r), EQ) }
      )
    }

  fun <F> Selective<F>.branchLaw(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.either(Gen.double(), Gen.float())) { either ->
      val fl = just(Double::toInt)
      val fr = just(Float::toInt)
      either.fold(
        { l -> just(either).branch(fl, fr).equalUnderTheLaw(fl.map { ff -> ff(l) }, EQ) },
        { r -> just(either).branch(fl, fr).equalUnderTheLaw(fr.map { ff -> ff(r) }, EQ) }
      )
    }

  fun <F> Selective<F>.ifSLaw(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.bool(), Gen.int(), Gen.int()) { bool, lInt, rInt ->
      if (bool) just(bool).ifS(just(lInt), just(rInt)).equalUnderTheLaw(just(lInt), EQ)
      else just(bool).ifS(just(lInt), just(rInt)).equalUnderTheLaw(just(rInt), EQ)
    }

}
