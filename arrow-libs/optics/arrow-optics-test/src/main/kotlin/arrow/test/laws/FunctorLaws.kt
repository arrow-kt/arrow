package arrow.test.laws

import arrow.Kind
import arrow.core.Option
import arrow.core.Some
import arrow.core.Tuple2
import arrow.core.andThen
import arrow.core.extensions.eq
import arrow.core.extensions.list.functor.widen
import arrow.core.extensions.tuple2.eq.eq
import arrow.core.identity
import arrow.test.generators.GenK
import arrow.test.generators.functionAToB
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Functor
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object FunctorLaws {

  private val fproductIdentityTupleEq = Tuple2.eq(Int.eq(), String.eq())
  private val fproductCompositionTupleEq = Tuple2.eq(Tuple2.eq(Int.eq(), String.eq()), String.eq())

  private val tupleLeftIdentityTupleEq = Tuple2.eq(String.eq(), Int.eq())
  private val tupleLeftCompositionTupleEq = Tuple2.eq(Int.eq(), Tuple2.eq(String.eq(), Int.eq()))

  private val tupleRightIdentityTupleEq = Tuple2.eq(Int.eq(), String.eq())
  private val tupleRightCompositionTupleEq = Tuple2.eq(Tuple2.eq(Int.eq(), String.eq()), Int.eq())

  fun <F> laws(FF: Functor<F>, GENK: GenK<F>, EQK: EqK<F>): List<Law> {
    val G1 = GENK.genK(Gen.int())
    val EQ = EQK.liftEq(Int.eq())

    return InvariantLaws.laws(FF, GENK, EQK) + listOf(
      Law("Functor Laws: Covariant Identity") { FF.covariantIdentity(G1, EQ) },
      Law("Functor Laws: Covariant Composition") { FF.covariantComposition(G1, EQ) },
      Law("Functor Laws: Lift Identity") { FF.liftIdentity(G1, EQ) },
      Law("Functor Laws: Lift Composition") { FF.liftComposition(G1, EQ) },
      Law("Functor Laws: Unit Identity") { FF.unitIdentity(G1, EQK.liftEq(Eq.any())) },
      Law("Functor Laws: Unit Composition") { FF.unitComposition(G1, EQK.liftEq(Eq.any())) },
      Law("Functor Laws: FProduct Identity") { FF.fproductIdentity(G1, EQK.liftEq(fproductIdentityTupleEq)) },
      Law("Functor Laws: FProduct Composition") { FF.fproductComposition(G1, EQK.liftEq(fproductCompositionTupleEq)) },
      Law("Functor Laws: mapConstValue Identity") { FF.mapConstValueIdentity(G1, EQK.liftEq(String.eq())) },
      Law("Functor Laws: mapConstValue Composition") { FF.mapConstValueComposition(G1, EQ) },
      Law("Functor Laws: mapConstKind Identity") { FF.mapConstKindIdentity(G1, EQK.liftEq(String.eq())) },
      Law("Functor Laws: mapConstKind Composition") { FF.mapConstKindComposition(G1, EQ) },
      Law("Functor Laws: tupleLeft Identity") { FF.tupleLeftIdentity(G1, EQK.liftEq(tupleLeftIdentityTupleEq)) },
      Law("Functor Laws: tupleLeft Composition") { FF.tupleLeftComposition(G1, EQK.liftEq(tupleLeftCompositionTupleEq)) },
      Law("Functor Laws: tupleRight Identity") { FF.tupleRightIdentity(G1, EQK.liftEq(tupleRightIdentityTupleEq)) },
      Law("Functor Laws: tupleRight Composition") { FF.tupleRightComposition(G1, EQK.liftEq(tupleRightCompositionTupleEq)) },
      Law("Functor Laws: widen") { widenIdentity() }
    )
  }

  fun <F> Functor<F>.covariantIdentity(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(G) { fa: Kind<F, Int> ->
      fa.map(::identity).equalUnderTheLaw(fa, EQ)
    }

  fun <F> Functor<F>.covariantComposition(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(G, Gen.functionAToB<Int, Int>(Gen.int()), Gen.functionAToB<Int, Int>(Gen.int())) { fa: Kind<F, Int>, f, g ->
      fa.map(f).map(g).equalUnderTheLaw(fa.map(f andThen g), EQ)
    }

  fun <F> Functor<F>.liftIdentity(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(G) { fa: Kind<F, Int> ->
      val f: (Kind<F, Int>) -> Kind<F, Int> = lift(::identity)
      f(fa).equalUnderTheLaw(fa, EQ)
    }

  fun <F> Functor<F>.liftComposition(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(G, Gen.functionAToB<Int, Int>(Gen.int()), Gen.functionAToB<Int, Int>(Gen.int())) { fa: Kind<F, Int>, f, g ->
      val ff = lift(f)
      val gg = lift(g)
      val fg = lift(f andThen g)
      gg(ff(fa)).equalUnderTheLaw(fg(fa), EQ)
    }

  fun <F> Functor<F>.unitIdentity(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Unit>>): Unit =
    forAll(G) { fa: Kind<F, Int> ->
      fa.unit().equalUnderTheLaw(fa.map { Unit }, EQ)
    }

  fun <F> Functor<F>.unitComposition(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Unit>>): Unit =
    forAll(G) { fa: Kind<F, Int> ->
      fa.unit().unit().equalUnderTheLaw(fa.map { Unit }, EQ)
    }

  fun <F> Functor<F>.fproductIdentity(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Tuple2<Int, String>>>): Unit =
    forAll(G, Gen.string()) { fa: Kind<F, Int>, b: String ->
      fa.fproduct { _ -> b }.equalUnderTheLaw(fa.map { a -> Tuple2(a, b) }, EQ)
    }

  fun <F> Functor<F>.fproductComposition(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Tuple2<Tuple2<Int, String>, String>>>): Unit =
    forAll(G, Gen.functionAToB<Int, String>(Gen.string()), Gen.functionAToB<Tuple2<Int, String>, String>(Gen.string())) { fa: Kind<F, Int>, f, g ->
      val ff: Kind<F, Tuple2<Int, String>> = fa.fproduct(f)
      val gg: Kind<F, Tuple2<Tuple2<Int, String>, String>> = ff.fproduct(g)
      val t: Kind<F, Tuple2<Tuple2<Int, String>, String>> = fa.map { a -> Tuple2(a, f(a)) }.map { tuple -> Tuple2(tuple, g(tuple)) }
      gg.equalUnderTheLaw(t, EQ)
    }

  fun <F> Functor<F>.mapConstValueIdentity(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, String>>): Unit =
    forAll(G, Gen.string()) { fa: Kind<F, Int>, b: String ->
      fa.mapConst(b).equalUnderTheLaw(fa.map { b }, EQ)
    }

  fun <F> Functor<F>.mapConstValueComposition(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(G, Gen.string(), Gen.int()) { fa: Kind<F, Int>, b: String, c: Int ->
      fa.mapConst(b).mapConst(c).equalUnderTheLaw(fa.map { c }, EQ)
    }

  fun <F> Functor<F>.mapConstKindIdentity(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, String>>): Unit =
    forAll(G, Gen.string()) { fb: Kind<F, Int>, a: String ->
      a.mapConst(fb).equalUnderTheLaw(fb.map { a }, EQ)
    }

  fun <F> Functor<F>.mapConstKindComposition(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(G, Gen.int(), Gen.string()) { fa: Kind<F, Int>, b: Int, c: String ->
      c.mapConst(fa).mapConst(b).equalUnderTheLaw(fa.map { b }, EQ)
    }

  fun <F> Functor<F>.tupleLeftIdentity(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Tuple2<String, Int>>>): Unit =
    forAll(G, Gen.string()) { fa: Kind<F, Int>, b: String ->
      fa.tupleLeft(b).equalUnderTheLaw(fa.map { a -> Tuple2(b, a) }, EQ)
    }

  fun <F> Functor<F>.tupleLeftComposition(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Tuple2<Int, Tuple2<String, Int>>>>): Unit =
    forAll(G, Gen.string(), Gen.int()) { fa: Kind<F, Int>, b: String, c: Int ->
      fa.tupleLeft(b).tupleLeft(c).equalUnderTheLaw(fa.map { a -> Tuple2(b, a) }.map { tuple2 -> Tuple2(c, tuple2) }, EQ)
    }

  fun <F> Functor<F>.tupleRightIdentity(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Tuple2<Int, String>>>): Unit =
    forAll(G, Gen.string()) { fa: Kind<F, Int>, b: String ->
      fa.tupleRight(b).equalUnderTheLaw(fa.map { a -> Tuple2(a, b) }, EQ)
    }

  fun <F> Functor<F>.tupleRightComposition(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Tuple2<Tuple2<Int, String>, Int>>>): Unit =
    forAll(G, Gen.string(), Gen.int()) { fa: Kind<F, Int>, b: String, c: Int ->
      val t: Kind<F, Tuple2<Tuple2<Int, String>, Int>> = fa.map { a -> Tuple2(a, b) }.map { tuple2 -> Tuple2(tuple2, c) }
      fa.tupleRight(b).tupleRight(c).equalUnderTheLaw(t, EQ)
    }

  fun widenIdentity(): Unit =
    forAll(Gen.int()) { a: Int ->
      val list: List<Some<Int>> = listOf(Some(a))
      val widened: List<Option<Int>> = list.widen()
      widened == list.map { identity(it) }
    }
}
