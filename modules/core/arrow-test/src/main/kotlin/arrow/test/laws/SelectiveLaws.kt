package arrow.test.laws

import arrow.Kind
import arrow.core.Either
import arrow.core.Right
import arrow.core.Tuple2
import arrow.core.extensions.eq
import arrow.core.identity
import arrow.core.right
import arrow.core.toT
import arrow.test.generators.GenK
import arrow.test.generators.either
import arrow.test.generators.functionAToB
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Selective
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object SelectiveLaws {

  fun <F> laws(A: Selective<F>, GENK: GenK<F>, EQK: EqK<F>): List<Law> {
    val EQ = EQK.liftEq(Int.eq())

    return ApplicativeLaws.laws(A, GENK, EQK) + listOf(
      Law("Selective Laws: identity") { A.identityLaw(EQ) },
      Law("Selective Laws: distributivity") { A.distributivity(EQ) },
      Law("Selective Laws: associativity") { A.associativity(EQ) },
      Law("Selective Laws: branch") { A.branch(EQ) },
      Law("Selective Laws: ifS") { A.ifSLaw(EQ) }
    )
  }

  fun <F> Selective<F>.identityLaw(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.either(Gen.int(), Gen.int())) { either ->
      either.fold(
        { l -> just(either).select(just(::identity)).equalUnderTheLaw(just(l), EQ) },
        { r -> just(either).select(just(::identity)).equalUnderTheLaw(just(r), EQ) }
      )
    }

  fun <F, A, B> Applicative<F>.sequenceRight(fa: Kind<F, A>, fb: Kind<F, B>): Kind<F, B> =
    fa.product(fb).map { (_, b) -> b }

  fun <F> Selective<F>.distributivity(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.either(Gen.int(), Gen.int()),
      Gen.functionAToB<Int, Int>(Gen.int()),
      Gen.functionAToB<Int, Int>(Gen.int())) { either, ab1, ab2 ->
      val fe = just(either)
      val f = just(ab1)
      val g = just(ab2)
      fe.select(sequenceRight(f, g)).equalUnderTheLaw(sequenceRight(fe.select(f), fe.select(g)), EQ)
    }

  fun <F> Selective<F>.associativity(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.int(),
      Gen.functionAToB<Int, Int>(Gen.int()),
      Gen.functionAToB<Int, Int>(Gen.int())) { a, ab1, ab2 ->

      val x: Kind<F, Either<Int, Int>> = just(a.right())
      val y: Kind<F, Either<Int, (Int) -> Int>> = just(ab1.right())
      val z: Kind<F, (Int) -> (Int) -> Int> = just({ _: Int -> ab2 })

      val p: Kind<F, Either<Int, Either<Tuple2<Int, Int>, Int>>> = x.map { e -> e.map(::Right) }
      val q: Kind<F, (Int) -> Either<Tuple2<Int, Int>, Int>> =
        y.map { e -> { i: Int -> e.bimap({ l -> l toT i }, { r -> r(i) }) } }
      val r: Kind<F, (Tuple2<Int, Int>) -> Int> = z.map { { (t1, t2): Tuple2<Int, Int> -> it(t1)(t2) } }
      x.select(y.select(z)).equalUnderTheLaw(p.select(q).select(r), EQ)
    }

  fun <F> Selective<F>.branch(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.functionAToB<Double, Int>(Gen.int()),
      Gen.functionAToB<Float, Int>(Gen.int()),
      Gen.either(Gen.double(), Gen.float())) { di, fi, either ->
      val fl = just(di)
      val fr = just(fi)
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
