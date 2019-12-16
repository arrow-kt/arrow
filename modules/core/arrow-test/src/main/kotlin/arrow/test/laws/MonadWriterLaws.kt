package arrow.test.laws

import arrow.Kind
import arrow.core.Tuple2
import arrow.core.extensions.eq
import arrow.core.extensions.tuple2.eq.eq
import arrow.mtl.typeclasses.MonadWriter
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Monad
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object MonadWriterLaws {

  fun <F, W> laws(
    MF: Monad<F>,
    MW: MonadWriter<F, W>,
    MOW: Monoid<W>,
    genW: Gen<W>,
    genTupleWA: Gen<Tuple2<W, Int>>,
    EQK: EqK<F>,
    EQW: Eq<W>
  ): List<Law> {
    val EQ_INT = EQK.liftEq(Int.eq())
    val EQ_TUPLE = EQK.liftEq(Tuple2.eq(EQW, Int.eq()))

    return MonadLaws.laws(MF, EQK) + listOf(
      Law("Monad Writer Laws: writer just") { MW.monadWriterWriterJust(MOW, EQ_INT) },
      Law("Monad Writer Laws: tell fusion") { MW.monadWriterTellFusion(genW, MOW) },
      Law("Monad Writer Laws: listen just") { MW.monadWriterListenJust(MOW, EQ_TUPLE) },
      Law("Monad Writer Laws: listen writer") { MW.monadWriterListenWriter(genTupleWA, EQ_TUPLE) })
  }

  fun <F, W> MonadWriter<F, W>.monadWriterWriterJust(
    MOW: Monoid<W>,
    EQ: Eq<Kind<F, Int>>
  ) {
    forAll(Gen.int()) { a: Int ->
      writer(Tuple2(MOW.empty(), a)).equalUnderTheLaw(just(a), EQ)
    }
  }

  fun <F, W> MonadWriter<F, W>.monadWriterTellFusion(
    genW: Gen<W>,
    MOW: Monoid<W>
  ) {
    forAll(genW, genW) { x: W, y: W ->
      tell(x).flatMap { tell(y) }.equalUnderTheLaw(tell(MOW.run { x.combine(y) }), Eq.any())
    }
  }

  fun <F, W> MonadWriter<F, W>.monadWriterListenJust(
    MOW: Monoid<W>,
    EqTupleWA: Eq<Kind<F, Tuple2<W, Int>>>
  ) {
    forAll(Gen.int()) { a: Int ->
      just(a).listen().equalUnderTheLaw(just(Tuple2(MOW.empty(), a)), EqTupleWA)
    }
  }

  fun <F, W> MonadWriter<F, W>.monadWriterListenWriter(
    genTupleWA: Gen<Tuple2<W, Int>>,
    EqTupleWA: Eq<Kind<F, Tuple2<W, Int>>>
  ) {
    forAll(genTupleWA) { tupleWA: Tuple2<W, Int> ->
      writer(tupleWA).listen().equalUnderTheLaw(tell(tupleWA.a).map { tupleWA }, EqTupleWA)
    }
  }
}
