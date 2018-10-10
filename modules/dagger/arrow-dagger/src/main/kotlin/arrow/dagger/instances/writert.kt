package arrow.dagger.instances

import arrow.data.WriterTPartialOf
import arrow.instances.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
abstract class WriterTInstances<F, W> {

  @Provides
  fun writerTFunctor(ev: DaggerWriterTFunctorInstance<F, W>): Functor<WriterTPartialOf<F, W>> = ev

  @Provides
  fun writerTApplicative(ev: DaggerWriterTApplicativeInstance<F, W>): Applicative<WriterTPartialOf<F, W>> = ev

  @Provides
  fun writerTMonad(ev: DaggerWriterTMonadInstance<F, W>): Monad<WriterTPartialOf<F, W>> = ev

  @Provides
  fun writerTSemigroupK(ev: DaggerWriterTSemigroupKInstance<F, W>): SemigroupK<WriterTPartialOf<F, W>> = ev

  @Provides
  fun writerTMonoidK(ev: DaggerWriterTSemigroupKInstance<F, W>): SemigroupK<WriterTPartialOf<F, W>> = ev

}

class DaggerWriterTFunctorInstance<F, W> @Inject constructor(val FF: Functor<F>) : WriterTFunctorInstance<F, W> {
  override fun FF(): Functor<F> = FF
}

class DaggerWriterTApplicativeInstance<F, L> @Inject constructor(val MF: Monad<F>, val ML: Monoid<L>) : WriterTApplicativeInstance<F, L> {
  override fun FF(): Monad<F> = MF
  override fun MM(): Monoid<L> = ML
  override fun MF(): Monad<F> = MF
}

class DaggerWriterTMonadInstance<F, L> @Inject constructor(val MF: Monad<F>, val ML: Monoid<L>) : WriterTMonadInstance<F, L> {
  override fun FF(): Monad<F> = MF
  override fun MM(): Monoid<L> = ML
  override fun MF(): Monad<F> = MF
}

class DaggerWriterTSemigroupKInstance<F, L> @Inject constructor(val SKF: SemigroupK<F>) : WriterTSemigroupKInstance<F, L> {
  override fun SS(): SemigroupK<F> = SKF
}

class DaggerWriterTMonoidKInstance<F, L> @Inject constructor(val SKF: MonoidK<F>) : WriterTMonoidKInstance<F, L> {
  override fun SS(): MonoidK<F> = SKF
  override fun MF(): MonoidK<F> = SKF
}
