package arrow.mtl.instances

import arrow.Kind
import arrow.core.Tuple2
import arrow.data.WriterT
import arrow.data.WriterTOf
import arrow.data.WriterTPartialOf
import arrow.data.fix
import arrow.instance
import arrow.instances.WriterTMonadInstance
import arrow.mtl.typeclasses.MonadFilter
import arrow.mtl.typeclasses.MonadWriter

@instance(WriterT::class)
interface WriterTMonadFilterInstance<F, W> : WriterTMonadInstance<F, W>, MonadFilter<WriterTPartialOf<F, W>> {
    override fun FF(): MonadFilter<F>

    override fun <A> empty(): WriterTOf<F, W, A> = WriterT(FF().empty())
}

@instance(WriterT::class)
interface WriterTMonadWriterInstance<F, W> : MonadWriter<WriterTPartialOf<F, W>, W>, WriterTMonadInstance<F, W> {

    override fun <A> Kind<WriterTPartialOf<F, W>, A>.listen(): Kind<WriterTPartialOf<F, W>, Tuple2<W, A>> = FF().run {
        WriterT(fix().content(this).flatMap({ a -> fix().write(this).map({ l -> Tuple2(l, Tuple2(l, a)) }) }))
    }

    override fun <A> Kind<WriterTPartialOf<F, W>, Tuple2<(W) -> W, A>>.pass(): WriterT<F, W, A> = FF().run {
        WriterT(fix().content(this).flatMap({ tuple2FA -> fix().write(this).map({ l -> Tuple2(tuple2FA.a(l), tuple2FA.b) }) }))
    }

    override fun <A> writer(aw: Tuple2<W, A>): WriterT<F, W, A> = WriterT.put2(FF(), aw.b, aw.a)

    override fun tell(w: W): Kind<WriterTPartialOf<F, W>, Unit> = WriterT.tell2(FF(), w)

}
