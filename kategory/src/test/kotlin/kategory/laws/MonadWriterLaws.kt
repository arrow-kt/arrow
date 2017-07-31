package kategory

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object MonadWriterLaws {

    inline fun <reified F, reified W, A> laws(MF: Monad<F>,
                                              MW: MonadWriter<F, W>,
                                              MOW: Monoid<W> = monoid<W>(),
                                              genA: Gen<A>,
                                              genW: Gen<W>,
                                              EqA: Eq<HK<F, A>>,
                                              EqInt: Eq<HK<F, Int>>,
                                              EqUnit: Eq<HK<F, Unit>>): List<Law> =

            MonadLaws.laws(MF, EqInt) + listOf(
                    Law("Monad Writer Laws: writer pure", { monadWriterWriterPure(genA, MW, MOW, EqA) }),
                    Law("Monad Writer Laws: tell fusion", { monadWriterTellFusion(genW, MW, MOW, EqUnit)}))

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
                                                               MOW: Monoid<W> = monoid<W>(),
                                                               EQ: Eq<HK<F, Unit>>): Unit {
        forAll(genW, genW, { x: W, y: W ->
            MW.flatMap(MW.tell(x), { MW.tell(y) }).equalUnderTheLaw(MOW.combine(x, y), EQ)
        })
    }
}
