package kategory

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object MonadWriterLaws {

    inline fun <reified F, reified W, A> laws(MF: Monad<F>,
                                              MW: MonadWriter<F, W>,
                                              MOW: Monoid<W> = monoid<W>(),
                                              genA: Gen<A>,
                                              genW: Gen<W>,
                                              genTupleWA: Gen<Tuple2<W, A>>,
                                              EqA: Eq<HK<F, A>>,
                                              EqInt: Eq<HK<F, Int>>,
                                              EqTupleWA: Eq<HK<F, Tuple2<W, A>>>): List<Law> =

            MonadLaws.laws(MF, EqInt) + listOf(
                    Law("Monad Writer Laws: writer pure", { monadWriterWriterPure(genA, MW, MOW, EqA) }),
                    Law("Monad Writer Laws: tell fusion", { monadWriterTellFusion(genW, MW, MOW) }),
                    Law("Monad Writer Laws: listen pure", { monadWriterListenPure(genA, MW, MOW, EqTupleWA) }),
                    Law("Monad Writer Laws: listen writer", { monadWriterListenWriter(genTupleWA, MW, EqTupleWA) }))

    inline fun <reified F, reified W, A> monadWriterWriterPure(genA: Gen<A>,
                                                               MW: MonadWriter<F, W>,
                                                               MOW: Monoid<W> = monoid<W>(),
                                                               EQ: Eq<HK<F, A>>): Unit {
        forAll(genA, { a: A ->
            MW.writer(Tuple2(MOW.empty(), a)).equalUnderTheLaw(MW.pure(a), EQ)
        })
    }

    inline fun <reified F, reified W> monadWriterTellFusion(genW: Gen<W>,
                                                            MW: MonadWriter<F, W>,
                                                            MOW: Monoid<W> = monoid<W>()): Unit {
        forAll(genW, genW, { x: W, y: W ->
            MW.flatMap(MW.tell(x), { MW.tell(y) }).equalUnderTheLaw(MOW.combine(x, y), Eq.any())
        })
    }

    inline fun <reified F, reified W, A> monadWriterListenPure(genA: Gen<A>,
                                                               MW: MonadWriter<F, W>,
                                                               MOW: Monoid<W> = monoid<W>(),
                                                               EqTupleWA: Eq<HK<F, Tuple2<W, A>>>): Unit {
        forAll(genA, { a: A ->
            MW.listen(MW.pure(a)).equalUnderTheLaw(MW.pure(Tuple2(MOW.empty(), a)), EqTupleWA)
        })
    }

    inline fun <reified F, reified W, A> monadWriterListenWriter(genTupleWA: Gen<Tuple2<W, A>>,
                                                                 MW: MonadWriter<F, W>,
                                                                 EqTupleWA: Eq<HK<F, Tuple2<W, A>>>): Unit {
        forAll(genTupleWA, { tupleWA: Tuple2<W, A> ->
            MW.listen(MW.writer(tupleWA)).equalUnderTheLaw(MW.map(MW.tell(tupleWA.a), { tupleWA }), EqTupleWA)
        })
    }
}
