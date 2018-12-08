package arrow.instances

import arrow.Kind
import arrow.core.Either
import arrow.core.toT
import arrow.data.*
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.extension
import arrow.typeclasses.*

@extension
interface WriterTFunctorInstance<F, W> : Functor<WriterTPartialOf<F, W>> {
  fun FF(): Functor<F>

  override fun <A, B> WriterTOf<F, W, A>.map(f: (A) -> B): WriterT<F, W, B> = fix().map(FF()) { f(it) }
}

//fun <F, W> WriterT.Companion.functor(FF: Functor<F>): Functor<WriterTPartialOf<F, W>> = object : WriterTFunctorInstance<F, W> {
//  override fun FF(): Functor<F> = FF
//}

@extension
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

//fun <F, W> WriterT.Companion.applicative(AF: Applicative<F>, MM: Monoid<W>): Applicative<WriterTPartialOf<F, W>> = object : WriterTApplicativeInstance<F, W> {
//  override fun AF(): Applicative<F> = AF
//  override fun MM(): Monoid<W> = MM
//}

@extension
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

//fun <F, W> WriterT.Companion.monad(MF: Monad<F>, MM: Monoid<W>): Monad<WriterTPartialOf<F, W>> = object : WriterTMonadInstance<F, W> {
//  override fun MF(): Monad<F> = MF
//  override fun MM(): Monoid<W> = MM
//}

@extension
interface WriterTApplicativeError<F, W, E> : ApplicativeError<WriterTPartialOf<F, W>, E>, WriterTApplicativeInstance<F, W> {

  fun AE(): ApplicativeError<F, E>

  override fun MM(): Monoid<W>

  override fun AF(): Applicative<F> = AE()

  override fun <A> raiseError(e: E): WriterT<F, W, A> =
    WriterT(AE().raiseError(e))

  override fun <A> WriterTOf<F, W, A>.handleErrorWith(f: (E) -> WriterTOf<F, W, A>): WriterT<F, W, A> = AE().run {
    WriterT(value.handleErrorWith { e -> f(e).value })
  }

}

//fun <F, W, E> WriterT.Companion.applicativeError(AE: ApplicativeError<F, E>, MM: Monoid<W>): ApplicativeError<WriterTPartialOf<F, W>, E> = object : WriterTApplicativeError<F, W, E> {
//  override fun AE(): ApplicativeError<F, E> = AE
//  override fun MM(): Monoid<W> = MM
//}

@extension
interface WriterTMonadError<F, W, E> : MonadError<WriterTPartialOf<F, W>, E>, WriterTApplicativeError<F, W, E>, WriterTMonadInstance<F, W> {

  fun ME(): MonadError<F, E>

  override fun MM(): Monoid<W>

  override fun MF(): Monad<F> = ME()

  override fun AF(): Applicative<F> = ME()

  override fun AE(): ApplicativeError<F, E> = ME()

}

//fun <F, W, E> WriterT.Companion.monadError(ME: MonadError<F, E>, MM: Monoid<W>): MonadError<WriterTPartialOf<F, W>, E> = object : WriterTMonadError<F, W, E> {
//  override fun ME(): MonadError<F, E> = ME
//  override fun MM(): Monoid<W> = MM
//}

@extension
interface WriterTMonadThrow<F, W> : MonadThrow<WriterTPartialOf<F, W>>, WriterTMonadError<F, W, Throwable> {
  override fun ME(): MonadError<F, Throwable>
  override fun MM(): Monoid<W>
}

//fun <F, W> WriterT.Companion.monadThrow(ME: MonadError<F, Throwable>, MM: Monoid<W>): MonadThrow<WriterTPartialOf<F, W>> = object : WriterTMonadThrow<F, W> {
//  override fun ME(): MonadError<F, Throwable> = ME
//  override fun MM(): Monoid<W> = MM
//}

@extension
interface WriterTSemigroupKInstance<F, W> : SemigroupK<WriterTPartialOf<F, W>> {

  fun SS(): SemigroupK<F>

  override fun <A> Kind<WriterTPartialOf<F, W>, A>.combineK(y: Kind<WriterTPartialOf<F, W>, A>): WriterT<F, W, A> =
    fix().combineK(SS(), y)
}

//fun <F, W> WriterT.Companion.sempigroupK(SS: SemigroupK<F>): SemigroupK<WriterTPartialOf<F, W>> = object : WriterTSemigroupKInstance<F, W> {
//  override fun SS(): SemigroupK<F> = SS
//}

@extension
interface WriterTMonoidKInstance<F, W> : MonoidK<WriterTPartialOf<F, W>>, WriterTSemigroupKInstance<F, W> {

  fun MF(): MonoidK<F>

  override fun SS(): SemigroupK<F> = MF()

  override fun <A> empty(): WriterT<F, W, A> = WriterT(MF().empty())
}

//fun <F, W> WriterT.Companion.monoidK(MF: MonoidK<F>): MonoidK<WriterTPartialOf<F, W>> = object : WriterTMonoidKInstance<F, W> {
//  override fun MF(): MonoidK<F> = MF
//}

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