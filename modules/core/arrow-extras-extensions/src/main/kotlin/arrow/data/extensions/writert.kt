package arrow.data.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.toT
import arrow.data.*

import arrow.extension
import arrow.typeclasses.*
import arrow.undocumented

@extension
@undocumented
interface WriterTFunctorInstance<F, W> : Functor<WriterTPartialOf<F, W>> {
  fun FF(): Functor<F>

  override fun <A, B> WriterTOf<F, W, A>.map(f: (A) -> B): WriterT<F, W, B> = fix().map(FF()) { f(it) }
}

@extension
@undocumented
interface WriterTApplicativeInstance<F, W> : Applicative<WriterTPartialOf<F, W>>, WriterTFunctorInstance<F, W> {

  fun AF(): Applicative<F>

  override fun FF(): Functor<F> = AF()

  fun MM(): Monoid<W>

  override fun <A> just(a: A): WriterTOf<F, W, A> =
    WriterT(AF().just(MM().empty() toT a))

  override fun <A, B> WriterTOf<F, W, A>.ap(ff: WriterTOf<F, W, (A) -> B>): WriterT<F, W, B> =
    fix().ap(AF(), MM(), ff)

  override fun <A, B> WriterTOf<F, W, A>.map(f: (A) -> B): WriterT<F, W, B> =
    fix().map(AF()) { f(it) }
}

@extension
@undocumented
interface WriterTMonadInstance<F, W> : Monad<WriterTPartialOf<F, W>>, WriterTApplicativeInstance<F, W> {

  fun MF(): Monad<F>

  override fun AF(): Applicative<F> = MF()

  override fun MM(): Monoid<W>

  override fun <A, B> WriterTOf<F, W, A>.map(f: (A) -> B): WriterT<F, W, B> =
    fix().map(FF()) { f(it) }

  override fun <A, B> WriterTOf<F, W, A>.flatMap(f: (A) -> WriterTOf<F, W, B>): WriterT<F, W, B> =
    fix().flatMap(MF(), MM()) { f(it) }

  override fun <A, B> tailRecM(a: A, f: (A) -> WriterTOf<F, W, Either<A, B>>): WriterT<F, W, B> =
    WriterT.tailRecM(MF(), a, f)

  override fun <A, B> WriterTOf<F, W, A>.ap(ff: WriterTOf<F, W, (A) -> B>): WriterT<F, W, B> =
    fix().ap(MF(), MM(), ff)
}

@extension
interface WriterTApplicativeError<F, W, E> : ApplicativeError<WriterTPartialOf<F, W>, E>, WriterTApplicativeInstance<F, W> {

  fun AE(): ApplicativeError<F, E>

  override fun MM(): Monoid<W>

  override fun AF(): Applicative<F> = AE()

  override fun <A> raiseError(e: E): WriterT<F, W, A> =
    WriterT(AE().raiseError(e))

  override fun <A> WriterTOf<F, W, A>.handleErrorWith(f: (E) -> WriterTOf<F, W, A>): WriterT<F, W, A> = AE().run {
    WriterT(value().handleErrorWith { e -> f(e).value() })
  }

}

@extension
interface WriterTMonadError<F, W, E> : MonadError<WriterTPartialOf<F, W>, E>, WriterTApplicativeError<F, W, E>, WriterTMonadInstance<F, W> {

  fun ME(): MonadError<F, E>

  override fun MM(): Monoid<W>

  override fun MF(): Monad<F> = ME()

  override fun AF(): Applicative<F> = ME()

  override fun AE(): ApplicativeError<F, E> = ME()

}

@extension
interface WriterTMonadThrow<F, W> : MonadThrow<WriterTPartialOf<F, W>>, WriterTMonadError<F, W, Throwable> {
  override fun ME(): MonadError<F, Throwable>
  override fun MM(): Monoid<W>
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
