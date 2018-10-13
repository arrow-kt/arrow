package arrow.instances

import arrow.Kind
import arrow.core.Either
import arrow.core.toT
import arrow.data.WriterT
import arrow.data.WriterTOf
import arrow.data.WriterTPartialOf
import arrow.data.fix
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.extension
import arrow.typeclasses.*

@extension
interface WriterTFunctorInstance<F, W> : Functor<WriterTPartialOf<F, W>> {
  fun FF(): Functor<F>

  override fun <A, B> Kind<WriterTPartialOf<F, W>, A>.map(f: (A) -> B): WriterT<F, W, B> = fix().map(FF()) { f(it) }
}

@extension
interface WriterTApplicativeInstance<F, W> : Applicative<WriterTPartialOf<F, W>>, WriterTFunctorInstance<F, W> {

  fun MF(): Monad<F>

  override fun FF(): Functor<F> = MF()

  fun MM(): Monoid<W>

  override fun <A> just(a: A): WriterTOf<F, W, A> =
    WriterT(MF().just(MM().empty() toT a))

  override fun <A, B> Kind<WriterTPartialOf<F, W>, A>.ap(ff: Kind<WriterTPartialOf<F, W>, (A) -> B>): WriterT<F, W, B> =
    fix().ap(MF(), MM(), ff)

  override fun <A, B> Kind<WriterTPartialOf<F, W>, A>.map(f: (A) -> B): WriterT<F, W, B> =
    fix().map(FF()) { f(it) }
}

@extension
interface WriterTMonadInstance<F, W> : Monad<WriterTPartialOf<F, W>>, WriterTApplicativeInstance<F, W> {

  override fun MF(): Monad<F>

  override fun MM(): Monoid<W>

  override fun <A, B> Kind<WriterTPartialOf<F, W>, A>.map(f: (A) -> B): WriterT<F, W, B> =
    fix().map(FF()) { f(it) }

  override fun <A, B> Kind<WriterTPartialOf<F, W>, A>.flatMap(f: (A) -> Kind<WriterTPartialOf<F, W>, B>): WriterT<F, W, B> =
    fix().flatMap(MF(), MM(), { f(it).fix() })

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<WriterTPartialOf<F, W>, Either<A, B>>): WriterT<F, W, B> =
    WriterT.tailRecM(MF(), a, f)

  override fun <A, B> Kind<WriterTPartialOf<F, W>, A>.ap(ff: Kind<WriterTPartialOf<F, W>, (A) -> B>): WriterT<F, W, B> =
    fix().ap(MF(), MM(), ff)
}

@extension
interface WriterTSemigroupKInstance<F, W> : SemigroupK<WriterTPartialOf<F, W>> {

  fun SS(): SemigroupK<F>

  override fun <A> Kind<WriterTPartialOf<F, W>, A>.combineK(y: Kind<WriterTPartialOf<F, W>, A>): WriterT<F, W, A> =
    fix().combineK(SS(), y)
}

@extension
interface WriterTMonoidKInstance<F, W> : MonoidK<WriterTPartialOf<F, W>>, WriterTSemigroupKInstance<F, W> {

  fun MF(): MonoidK<F>

  override fun SS(): SemigroupK<F> = MF()

  override fun <A> empty(): WriterT<F, W, A> = WriterT(MF().empty())
}

class WriterTContext<F, W>(val MF: Monad<F>, val MW: Monoid<W>) : WriterTMonadInstance<F, W> {
  override fun FF(): Functor<F> = MF
  override fun MF(): Monad<F> = MF
  override fun MM(): Monoid<W> = MW
}

class WriterTContextPartiallyApplied<F, W>(val MF: Monad<F>, val MW: Monoid<W>) {
  @Deprecated(ExtensionsDSLDeprecated)
  infix fun <A> extensions(f: WriterTContext<F, W>.() -> A): A =
    f(WriterTContext(MF, MW))
}

fun <F, W> ForWriterT(MF: Monad<F>, MW: Monoid<W>): WriterTContextPartiallyApplied<F, W> =
  WriterTContextPartiallyApplied(MF, MW)