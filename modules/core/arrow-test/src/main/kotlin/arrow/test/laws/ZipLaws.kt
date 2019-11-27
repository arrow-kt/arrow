package arrow.test.laws

import arrow.Kind
import arrow.core.Ior
import arrow.core.Tuple2
import arrow.core.extensions.eq
import arrow.core.extensions.ior.eq.eq
import arrow.core.extensions.tuple2.eq.eq
import arrow.core.leftIor
import arrow.core.toT
import arrow.test.generators.GenK
import arrow.test.generators.tuple2
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Foldable
import arrow.typeclasses.Zip
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object ZipLaws {

  private val intTupleEq = Tuple2.eq(Int.eq(), Int.eq())

  private val iorIntIntTupleEq = Ior.eq(Int.eq(), intTupleEq)

  fun <F> laws(
    ZIP: Zip<F>,
    GENK: GenK<F>,
    EQK: EqK<F>
  ): List<Law> =
    SemialignLaws.laws(ZIP, GENK, EQK) + zipLaws(ZIP, GENK, EQK)

  fun <F> laws(
    ZIP: Zip<F>,
    GENK: GenK<F>,
    EQK: EqK<F>,
    FOLD: Foldable<F>
  ) = SemialignLaws.laws(ZIP, GENK, EQK, FOLD) + zipLaws(ZIP, GENK, EQK)

  private fun <F> zipLaws(
    ZIP: Zip<F>,
    GENK: GenK<F>,
    EQK: EqK<F>
  ): List<Law> {
    val intGen = GENK.genK(Gen.int())
    val tupleGen = GENK.genK(Gen.tuple2(Gen.int(), Gen.int()))

    return listOf(
      Law("Zip Laws: Idempotency") { ZIP.idempotency(intGen, EQK.liftEq(intTupleEq)) },
      Law("Zip Laws: Commutativity") { ZIP.commutativity(intGen, EQK.liftEq(intTupleEq)) },
      Law("Zip Laws: Associativity") { ZIP.associativity(intGen, EQK.liftEq(Tuple2.eq(Int.eq(), intTupleEq))) },
      Law("Zip Laws: Absorption #1") { ZIP.absorption1(intGen, EQK.liftEq(Int.eq())) },
      Law("Zip Laws: Absorption #2") { ZIP.absorption2(intGen, EQK.liftEq(iorIntIntTupleEq)) },
      Law("Zip Laws: With") { ZIP.zipWith(intGen, EQK.liftEq(String.eq())) },
      Law("Zip Laws: Functoriality") { ZIP.functoriality(intGen, EQK.liftEq(Tuple2.eq(String.eq(), String.eq()))) },
      Law("Zip Laws: Zippyness #1") { ZIP.zippyness1(intGen, EQK.liftEq(Int.eq())) },
      Law("Zip Laws: Zippyness #2") { ZIP.zippyness2(intGen, EQK.liftEq(Int.eq())) },
      Law("Zip Laws: Zippyness #3") { ZIP.zippyness3(tupleGen, EQK.liftEq(intTupleEq)) },
      Law("Zip Laws: Distributivity #1") { ZIP.distributivity1(intGen, EQK.liftEq(Ior.eq(intTupleEq, Int.eq()))) },
      Law("Zip Laws: Distributivity #2") { ZIP.distributivity2(intGen, EQK.liftEq(Ior.eq(intTupleEq, intTupleEq))) },
      Law("Zip Laws: Distributivity #3") { ZIP.distributivity3(intGen, EQK.liftEq(Tuple2.eq(Ior.eq(Int.eq(), Int.eq()), Int.eq()))) }
    )
  }

  fun <F, A> Zip<F>.idempotency(G: Gen<Kind<F, A>>, EQ: Eq<Kind<F, Tuple2<A, A>>>) =
    forAll(G) { x ->
      x.zip(x).equalUnderTheLaw(x.map { it toT it }, EQ)
    }

  fun <F, A> Zip<F>.commutativity(G: Gen<Kind<F, A>>, EQ: Eq<Kind<F, Tuple2<A, A>>>) =
    forAll(G, G) { x, y ->
      x.zip(y).equalUnderTheLaw(y.zip(x).map { it.reverse() }, EQ)
    }

  fun <F, A> Zip<F>.associativity(G: Gen<Kind<F, A>>, EQ: Eq<Kind<F, Tuple2<A, Tuple2<A, A>>>>) =
    forAll(G, G, G) { x, y, z ->
      val ls = x.zip(y.zip(z))
      val rs = (x.zip(y)).zip(z).map { it.assoc() }

      ls.equalUnderTheLaw(rs, EQ)
    }

  fun <F, A> Zip<F>.absorption1(G: Gen<Kind<F, A>>, EQ: Eq<Kind<F, A>>) =
    forAll(G, G) { x, y ->
      x.zip(align(x, y)).map { it.a }.equalUnderTheLaw(x, EQ)
    }

  fun <F, A> Zip<F>.absorption2(G: Gen<Kind<F, A>>, EQ: Eq<Kind<F, Ior<A, Tuple2<A, A>>>>) =
    forAll(G, G) { x, y ->
      val ls = align(x, x.zip(y)).map { it.toLeft() }
      val rs = x.map { it.leftIor() }

      ls.equalUnderTheLaw(rs, EQ)
    }

  fun <F, A> Zip<F>.zipWith(G: Gen<Kind<F, A>>, EQ: Eq<Kind<F, String>>) =
    forAll(G, G) { x, y ->
      val f = { a: A, b: A -> "f($a,$b)" }
      val ls = x.zipWith(y, f)
      val rs = x.zip(y).map { f(it.a, it.b) }

      ls.equalUnderTheLaw(rs, EQ)
    }

  fun <F, A> Zip<F>.functoriality(G: Gen<Kind<F, A>>, EQ: Eq<Kind<F, Tuple2<String, String>>>) =
    forAll(G, G) { x, y ->
      val f = { a: A -> "f($a)" }
      val g = { a: A -> "g($a)" }

      val ls = x.map(f).zip(y.map(g))
      val rs = (x.zip(y)).map { it.bimap(f, g) }

      ls.equalUnderTheLaw(rs, EQ)
    }

  fun <F, A> Zip<F>.zippyness1(G: Gen<Kind<F, A>>, EQ: Eq<Kind<F, A>>) =
    forAll(G) { x ->
      x.zip(x).map { it.a }.equalUnderTheLaw(x, EQ)
    }

  fun <F, A> Zip<F>.zippyness2(G: Gen<Kind<F, A>>, EQ: Eq<Kind<F, A>>) =
    forAll(G) { x ->
      x.zip(x).map { it.b }.equalUnderTheLaw(x, EQ)
    }

  fun <F, A> Zip<F>.zippyness3(G: Gen<Kind<F, Tuple2<A, A>>>, EQ: Eq<Kind<F, Tuple2<A, A>>>) =
    forAll(G) { x ->
      (x.map { it.a }).zip(x.map { it.b }).equalUnderTheLaw(x, EQ)
    }

  fun <F, A> Zip<F>.distributivity1(G: Gen<Kind<F, A>>, EQ: Eq<Kind<F, Ior<Tuple2<A, A>, A>>>) =
    forAll(G, G, G) { x, y, z ->
      val ls = align(x.zip(y), z)
      val rs = align(x, z).zip(align(y, z)).map { it.undistrThesePair() }

      ls.equalUnderTheLaw(rs, EQ)
    }

  fun <F, A> Zip<F>.distributivity2(G: Gen<Kind<F, A>>, EQ: Eq<Kind<F, Ior<Tuple2<A, A>, Tuple2<A, A>>>>) =
    forAll(G, G, G) { x, y, z ->
      val ls = align(x, y).zip(z).map { it.distrPairThese() }
      val rs = align(x.zip(z), y.zip(z))

      ls.equalUnderTheLaw(rs, EQ)
    }

  fun <F, A> Zip<F>.distributivity3(G: Gen<Kind<F, A>>, EQ: Eq<Kind<F, Tuple2<Ior<A, A>, A>>>) =
    forAll(G, G, G) { x, y, z ->
      val ls = align(x, y).zip(z)
      val rs = align(x.zip(z), y.zip(z)).map { it.undistrPairThese() }
      ls.equalUnderTheLaw(rs, EQ)
    }
}

private fun <A, B> Ior<A, B>.toLeft() =
  fold({ this }, { this }, { l, _ -> Ior.Left(l) })

private fun <A, B, C> Tuple2<Tuple2<A, B>, C>.assoc(): Tuple2<A, Tuple2<B, C>> =
  this.a.a toT (this.a.b toT this.b)

private fun <A, B, C> Tuple2<Ior<A, C>, Ior<B, C>>.undistrThesePair(): Ior<Tuple2<A, B>, C> =
  when (val l = this.a) {
    is Ior.Left -> {
      when (val r = this.b) {
        is Ior.Left -> Ior.Left(l.value toT r.value)
        is Ior.Both -> Ior.Both(l.value toT r.leftValue, r.rightValue)
        is Ior.Right -> Ior.Right(r.value)
      }
    }
    is Ior.Both -> when (val r = this.b) {
      is Ior.Left -> Ior.Both(l.leftValue toT r.value, l.rightValue)
      is Ior.Both -> Ior.Both(l.leftValue toT r.leftValue, l.rightValue)
      is Ior.Right -> Ior.Right(l.rightValue)
    }
    is Ior.Right -> Ior.Right(l.value)
  }

private fun <A, B, C> Tuple2<Ior<A, B>, C>.distrPairThese(): Ior<Tuple2<A, C>, Tuple2<B, C>> =
  when (val l = this.a) {
    is Ior.Left -> Ior.Left(l.value toT this.b)
    is Ior.Right -> Ior.Right(l.value toT this.b)
    is Ior.Both -> Ior.Both(l.leftValue toT this.b, l.rightValue toT this.b)
  }

private fun <A, B, C> Ior<Tuple2<A, C>, Tuple2<B, C>>.undistrPairThese(): Tuple2<Ior<A, B>, C> =
  when (val e = this) {
    is Ior.Left -> Ior.Left(e.value.a) toT e.value.b
    is Ior.Both -> Ior.Both(e.leftValue.a, e.rightValue.a) toT e.leftValue.b
    is Ior.Right -> Ior.Right(e.value.a) toT e.value.b
  }
