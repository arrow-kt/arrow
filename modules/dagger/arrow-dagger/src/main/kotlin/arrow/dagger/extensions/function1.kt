package arrow.dagger.extensions

import arrow.core.Function1PartialOf
import arrow.core.extensions.Function1Applicative
import arrow.core.extensions.Function1Functor
import arrow.core.extensions.Function1Monad
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
abstract class Function1Instances<F> {

  @Provides
  fun function1Functor(ev: DaggerFunction1Functor<F>): Functor<Function1PartialOf<F>> = ev

  @Provides
  fun function1Applicative(ev: DaggerFunction1Applicative<F>): Applicative<Function1PartialOf<F>> = ev

  @Provides
  fun function1Monad(ev: DaggerFunction1Monad<F>): Monad<Function1PartialOf<F>> = ev

}

class DaggerFunction1Functor<F> @Inject constructor(val FF: Functor<F>) : Function1Functor<F>

class DaggerFunction1Applicative<F> @Inject constructor(val FF: Monad<F>) : Function1Applicative<F>

class DaggerFunction1Monad<F> @Inject constructor(val FF: Monad<F>) : Function1Monad<F>
