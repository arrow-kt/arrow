package arrow.mtl.instances

import arrow.Kind
import arrow.core.Tuple2
import arrow.data.WriterT
import arrow.data.WriterTOf
import arrow.data.WriterTPartialOf
import arrow.data.reify
import arrow.instance
import arrow.instances.WriterTMonadInstance
import arrow.mtl.MonadFilter
import arrow.mtl.MonadWriter

@instance(WriterT::class)
interface WriterTMonadFilterInstance<F, W> : WriterTMonadInstance<F, W>, MonadFilter<WriterTPartialOf<F, W>> {
    override fun FF(): MonadFilter<F>

    override fun <A> empty(): WriterTOf<F, W, A> = WriterT(FF().empty())
}

@instance(WriterT::class)
interface WriterTMonadWriterInstance<F, W> : MonadWriter<WriterTPartialOf<F, W>, W>, WriterTMonadInstance<F, W> {

    override fun <A> listen(fa: WriterTOf<F, W, A>): Kind<WriterTPartialOf<F, W>, Tuple2<W, A>> =
            WriterT(FF().flatMap(fa.extract().content(FF()), { a -> FF().map(fa.extract().write(FF()), { l -> Tuple2(l, Tuple2(l, a)) }) }))

    override fun <A> pass(fa: Kind<WriterTPartialOf<F, W>, Tuple2<(W) -> W, A>>): WriterT<F, W, A> =
            WriterT(FF().flatMap(fa.extract().content(FF()), { tuple2FA -> FF().map(fa.extract().write(FF()), { l -> Tuple2(tuple2FA.a(l), tuple2FA.b) }) }))

    override fun <A> writer(aw: Tuple2<W, A>): WriterT<F, W, A> = WriterT.put2(aw.b, aw.a, FF())

    override fun tell(w: W): Kind<WriterTPartialOf<F, W>, Unit> = WriterT.tell2(w, FF())

}
