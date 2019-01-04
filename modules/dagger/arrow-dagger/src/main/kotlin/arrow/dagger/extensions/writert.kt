package arrow.dagger.extensions

import arrow.data.WriterTPartialOf
import arrow.data.extensions.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
abstract class WriterTInstances<F, W> {

  @Provides
  fun writerTFunctor(ev: DaggerWriterTFunctor<F, W>): Functor<WriterTPartialOf<F, W>> = ev

  @Provides
  fun writerTApplicative(ev: DaggerWriterTApplicative<F, W>): Applicative<WriterTPartialOf<F, W>> = ev

  @Provides
  fun writerTMonad(ev: DaggerWriterTMonad<F, W>): Monad<WriterTPartialOf<F, W>> = ev

  @Provides
  fun writerTSemigroupK(ev: DaggerWriterTSemigroupK<F, W>): SemigroupK<WriterTPartialOf<F, W>> = ev

  @Provides
  fun writerTMonoidK(ev: DaggerWriterTSemigroupK<F, W>): SemigroupK<WriterTPartialOf<F, W>> = ev

}

class DaggerWriterTFunctor<F, W> @Inject constructor(val FF: Functor<F>) : WriterTFunctor<F, W> {
  override fun FF(): Functor<F> = FF
}

class DaggerWriterTApplicative<F, L> @Inject constructor(val AF: Applicative<F>, val ML: Monoid<L>) : WriterTApplicative<F, L> {
  override fun FF(): Functor<F> = AF
  override fun MM(): Monoid<L> = ML
  override fun AF(): Applicative<F> = AF
}

class DaggerWriterTMonad<F, L> @Inject constructor(val MF: Monad<F>, val ML: Monoid<L>) : WriterTMonad<F, L> {
  override fun FF(): Monad<F> = MF
  override fun MM(): Monoid<L> = ML
  override fun MF(): Monad<F> = MF
}

class DaggerWriterTSemigroupK<F, L> @Inject constructor(val SKF: SemigroupK<F>) : WriterTSemigroupK<F, L> {
  override fun SS(): SemigroupK<F> = SKF
}

class DaggerWriterTMonoidK<F, L> @Inject constructor(val SKF: MonoidK<F>) : WriterTMonoidK<F, L> {
  override fun SS(): MonoidK<F> = SKF
  override fun MF(): MonoidK<F> = SKF
}
