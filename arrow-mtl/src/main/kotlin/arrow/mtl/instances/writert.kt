package arrow.mtl.instances

import arrow.*
import arrow.core.*
import arrow.data.*
import arrow.instances.*
import arrow.mtl.MonadFilter
import arrow.mtl.MonadWriter

@instance(WriterT::class)
interface WriterTMonadFilterInstance<F, W> : WriterTMonadInstance<F, W>, MonadFilter<WriterTKindPartial<F, W>> {
    override fun FF(): MonadFilter<F>

    override fun <A> empty(): WriterTKind<F, W, A> = WriterT(FF().empty())
}

@instance(WriterT::class)
interface WriterTMonadWriterInstance<F, W> : MonadWriter<WriterTKindPartial<F, W>, W>, WriterTMonadInstance<F, W> {

    override fun <A> listen(fa: WriterTKind<F, W, A>): HK<WriterTKindPartial<F, W>, Tuple2<W, A>> =
            WriterT(FF().flatMap(fa.ev().content(FF()), { a -> FF().map(fa.ev().write(FF()), { l -> Tuple2(l, Tuple2(l, a)) }) }))

    override fun <A> pass(fa: HK<WriterTKindPartial<F, W>, Tuple2<(W) -> W, A>>): WriterT<F, W, A> =
            WriterT(FF().flatMap(fa.ev().content(FF()), { tuple2FA -> FF().map(fa.ev().write(FF()), { l -> Tuple2(tuple2FA.a(l), tuple2FA.b) }) }))

    override fun <A> writer(aw: Tuple2<W, A>): WriterT<F, W, A> = WriterT.put2(aw.b, aw.a, FF())

    override fun tell(w: W): HK<WriterTKindPartial<F, W>, Unit> = WriterT.tell2(w, FF())

}