package arrow.dagger.instances

import arrow.core.Function1PartialOf
import arrow.instances.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
abstract class Function1Instances<F> {

  @Provides
  fun function1Functor(ev: DaggerFunction1FunctorInstance<F>): Functor<Function1PartialOf<F>> = ev

  @Provides
  fun function1Invariant(ev: DaggerFunction1InvariantInstance<F>): Invariant<Function1PartialOf<F>> = ev

  @Provides
  fun function1Contravariant(ev: DaggerFunction1ContravariantInstance<F>): Contravariant<Function1PartialOf<F>> = ev

  @Provides
  fun function1Applicative(ev: DaggerFunction1ApplicativeInstance<F>): Applicative<Function1PartialOf<F>> = ev

  @Provides
  fun function1Monad(ev: DaggerFunction1MonadInstance<F>): Monad<Function1PartialOf<F>> = ev

}

class DaggerFunction1FunctorInstance<F> @Inject constructor(val FF: Functor<F>) : Function1FunctorInstance<F>

class DaggerFunction1InvariantInstance<F> @Inject constructor(val FI: Invariant<F>) : Function1InvariantInstance<F>

class DaggerFunction1ContravariantInstance<F> @Inject constructor(val FC: Contravariant<F>) : Function1ContravariantInstance<F>

class DaggerFunction1ApplicativeInstance<F> @Inject constructor(val FF: Monad<F>) : Function1ApplicativeInstance<F>

class DaggerFunction1MonadInstance<F> @Inject constructor(val FF: Monad<F>) : Function1MonadInstance<F>
