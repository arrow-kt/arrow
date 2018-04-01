package arrow.test.laws

import arrow.Kind
import arrow.core.Tuple2
import arrow.mtl.typeclasses.MonadWriter
import arrow.typeclasses.Eq
import arrow.typeclasses.Monad
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object MonadWriterLaws {

  inline fun <F, W> laws(MF: Monad<F>,
                         MW: MonadWriter<F, W>,
                         MOW: Monoid<W>,
                         genW: Gen<W>,
                         genTupleWA: Gen<Tuple2<W, Int>>,
                         EqInt: Eq<Kind<F, Int>>,
                         EqTupleWA: Eq<Kind<F, Tuple2<W, Int>>>): List<Law> =
    MonadLaws.laws(MF, EqInt) + listOf(
      Law("Monad Writer Laws: writer just", { MW.monadWriterWriterJust(MOW, EqInt) }),
      Law("Monad Writer Laws: tell fusion", { MW.monadWriterTellFusion(genW, MOW) }),
      Law("Monad Writer Laws: listen just", { MW.monadWriterListenJust(MOW, EqTupleWA) }),
      Law("Monad Writer Laws: listen writer", { MW.monadWriterListenWriter(genTupleWA, EqTupleWA) }))

  fun <F, W> MonadWriter<F, W>.monadWriterWriterJust(MOW: Monoid<W>,
                                                     EQ: Eq<Kind<F, Int>>): Unit {
    forAll(Gen.int(), { a: Int ->
      writer(Tuple2(MOW.empty(), a)).equalUnderTheLaw(just(a), EQ)
    })
  }

  fun <F, W> MonadWriter<F, W>.monadWriterTellFusion(genW: Gen<W>,
                                                     MOW: Monoid<W>): Unit {
    forAll(genW, genW, { x: W, y: W ->
      tell(x).flatMap({ tell(y) }).equalUnderTheLaw(tell(MOW.run { x.combine(y) }), Eq.any())
    })
  }

  fun <F, W> MonadWriter<F, W>.monadWriterListenJust(MOW: Monoid<W>,
                                                     EqTupleWA: Eq<Kind<F, Tuple2<W, Int>>>): Unit {
    forAll(Gen.int(), { a: Int ->
      just(a).listen().equalUnderTheLaw(just(Tuple2(MOW.empty(), a)), EqTupleWA)
    })
  }

  fun <F, W> MonadWriter<F, W>.monadWriterListenWriter(genTupleWA: Gen<Tuple2<W, Int>>,
                                                       EqTupleWA: Eq<Kind<F, Tuple2<W, Int>>>): Unit {
    forAll(genTupleWA, { tupleWA: Tuple2<W, Int> ->
      writer(tupleWA).listen().equalUnderTheLaw(tell(tupleWA.a).map({ tupleWA }), EqTupleWA)
    })
  }
}
