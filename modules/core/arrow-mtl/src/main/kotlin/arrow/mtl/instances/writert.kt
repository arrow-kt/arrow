package arrow.mtl.instances

import arrow.Kind
import arrow.core.Tuple2
import arrow.data.WriterT
import arrow.data.WriterTOf
import arrow.data.WriterTPartialOf
import arrow.data.fix
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.extension
import arrow.instances.WriterTMonadInstance
import arrow.mtl.typeclasses.MonadFilter
import arrow.mtl.typeclasses.MonadWriter
import arrow.typeclasses.Monad
import arrow.typeclasses.Monoid

@extension
interface WriterTMonadFilterInstance<F, W> : MonadFilter<WriterTPartialOf<F, W>>, WriterTMonadInstance<F, W> {
  override fun FF(): MonadFilter<F>

  override fun MF(): Monad<F> = FF()

  override fun MM(): Monoid<W>

  override fun <A> empty(): WriterTOf<F, W, A> = WriterT(FF().empty())
}

@extension
interface WriterTMonadWriterInstance<F, W> : MonadWriter<WriterTPartialOf<F, W>, W>, WriterTMonadInstance<F, W> {

  override fun MF(): Monad<F>

  override fun MM(): Monoid<W>

  override fun <A> Kind<WriterTPartialOf<F, W>, A>.listen(): Kind<WriterTPartialOf<F, W>, Tuple2<W, A>> = MF().run {
    WriterT(fix().content(this).flatMap { a -> fix().write(this).map { l -> Tuple2(l, Tuple2(l, a)) } })
  }

  override fun <A> Kind<WriterTPartialOf<F, W>, Tuple2<(W) -> W, A>>.pass(): WriterT<F, W, A> = MF().run {
    WriterT(fix().content(this).flatMap { tuple2FA -> fix().write(this).map { l -> Tuple2(tuple2FA.a(l), tuple2FA.b) } })
  }

  override fun <A> writer(aw: Tuple2<W, A>): WriterT<F, W, A> = WriterT.put2(MF(), aw.b, aw.a)

  override fun tell(w: W): Kind<WriterTPartialOf<F, W>, Unit> = WriterT.tell2(MF(), w)

}

class WriterTMtlContext<F, W>(val MF: Monad<F>, val MW: Monoid<W>) : WriterTMonadWriterInstance<F, W> {
  override fun FF(): Monad<F> = MF

  override fun MF(): Monad<F> = MF

  override fun MM(): Monoid<W> = MW
}

class WriterTMtlContextPartiallyApplied<F, W>(val MF: Monad<F>, val MW: Monoid<W>) {
  @Deprecated(ExtensionsDSLDeprecated)
  infix fun <A> extensions(f: WriterTMtlContext<F, W>.() -> A): A =
    f(WriterTMtlContext(MF, MW))
}

fun <F, W> ForWriterT(MF: Monad<F>, MW: Monoid<W>): WriterTMtlContextPartiallyApplied<F, W> =
  WriterTMtlContextPartiallyApplied(MF, MW)
