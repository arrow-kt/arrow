package arrow.instances

import arrow.Kind
import arrow.core.Either
import arrow.core.toT
import arrow.data.WriterT
import arrow.data.WriterTOf
import arrow.data.WriterTPartialOf
import arrow.data.fix
import arrow.instance
import arrow.typeclasses.*

@instance(WriterT::class)
interface WriterTFunctorInstance<F, W> : Functor<WriterTPartialOf<F, W>> {
  fun FF(): Functor<F>

  override fun <A, B> Kind<WriterTPartialOf<F, W>, A>.map(f: (A) -> B): WriterT<F, W, B> = fix().map(FF(), { f(it) })
}

@instance(WriterT::class)
interface WriterTApplicativeInstance<F, W> : Applicative<WriterTPartialOf<F, W>>, WriterTFunctorInstance<F, W> {

  override fun FF(): Monad<F>

  fun MM(): Monoid<W>

  override fun <A> just(a: A): WriterTOf<F, W, A> =
    WriterT(FF().just(MM().empty() toT a))

  override fun <A, B> Kind<WriterTPartialOf<F, W>, A>.ap(ff: Kind<WriterTPartialOf<F, W>, (A) -> B>): WriterT<F, W, B> =
    fix().ap(FF(), MM(), ff)

  override fun <A, B> Kind<WriterTPartialOf<F, W>, A>.map(f: (A) -> B): WriterT<F, W, B> =
    fix().map(FF(), { f(it) })
}

@instance(WriterT::class)
interface WriterTMonadInstance<F, W> : WriterTApplicativeInstance<F, W>, Monad<WriterTPartialOf<F, W>> {

  override fun <A, B> Kind<WriterTPartialOf<F, W>, A>.map(f: (A) -> B): WriterT<F, W, B> =
    fix().map(FF(), { f(it) })

  override fun <A, B> Kind<WriterTPartialOf<F, W>, A>.flatMap(f: (A) -> Kind<WriterTPartialOf<F, W>, B>): WriterT<F, W, B> =
    fix().flatMap(FF(), MM(), { f(it).fix() })

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<WriterTPartialOf<F, W>, Either<A, B>>): WriterT<F, W, B> =
    WriterT.tailRecM(FF(), a, f)

  override fun <A, B> Kind<WriterTPartialOf<F, W>, A>.ap(ff: Kind<WriterTPartialOf<F, W>, (A) -> B>): WriterT<F, W, B> =
    fix().ap(FF(), MM(), ff)
}

@instance(WriterT::class)
interface WriterTSemigroupKInstance<F, W> : SemigroupK<WriterTPartialOf<F, W>> {

  fun SS(): SemigroupK<F>

  override fun <A> Kind<WriterTPartialOf<F, W>, A>.combineK(y: Kind<WriterTPartialOf<F, W>, A>): WriterT<F, W, A> =
    fix().combineK(SS(), y)
}

@instance(WriterT::class)
interface WriterTMonoidKInstance<F, W> : MonoidK<WriterTPartialOf<F, W>>, WriterTSemigroupKInstance<F, W> {

  override fun SS(): MonoidK<F>

  override fun <A> empty(): WriterT<F, W, A> = WriterT(SS().empty())
}

class WriterTContext<F, W>(val MF: Monad<F>, val MW: Monoid<W>) : WriterTMonadInstance<F, W> {
  override fun FF(): Monad<F> = MF

  override fun MM(): Monoid<W> = MW
}

class WriterTContextPartiallyApplied<F, W>(val MF: Monad<F>, val MW: Monoid<W>) {
  infix fun <A> extensions(f: WriterTContext<F, W>.() -> A): A =
    f(WriterTContext(MF, MW))
}

fun <F, W> ForWriterT(MF: Monad<F>, MW: Monoid<W>): WriterTContextPartiallyApplied<F, W> =
  WriterTContextPartiallyApplied(MF, MW)