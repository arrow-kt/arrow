package arrow.dagger.extensions

import arrow.core.Function1PartialOf
import arrow.core.extensions.Function1ApplicativeInstance
import arrow.core.extensions.Function1FunctorInstance
import arrow.core.extensions.Function1MonadInstance
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
abstract class Function1Instances<F> {

  @Provides
  fun function1Functor(ev: DaggerFunction1FunctorInstance<F>): Functor<Function1PartialOf<F>> = ev

  @Provides
  fun function1Applicative(ev: DaggerFunction1ApplicativeInstance<F>): Applicative<Function1PartialOf<F>> = ev

  @Provides
  fun function1Monad(ev: DaggerFunction1MonadInstance<F>): Monad<Function1PartialOf<F>> = ev

}

class DaggerFunction1FunctorInstance<F> @Inject constructor(val FF: Functor<F>) : Function1FunctorInstance<F>

class DaggerFunction1ApplicativeInstance<F> @Inject constructor(val FF: Monad<F>) : Function1ApplicativeInstance<F>

class DaggerFunction1MonadInstance<F> @Inject constructor(val FF: Monad<F>) : Function1MonadInstance<F>
