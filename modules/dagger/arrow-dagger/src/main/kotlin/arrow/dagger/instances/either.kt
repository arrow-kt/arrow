package arrow.dagger.instances

import arrow.core.EitherPartialOf
import arrow.instances.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
abstract class EitherInstances<L> {

    @Provides
    fun eitherFunctor(ev: DaggerEitherFunctorInstance<L>): Functor<EitherPartialOf<L>> = ev

    @Provides
    fun eitherApplicative(ev: DaggerEitherApplicativeInstance<L>): Applicative<EitherPartialOf<L>> = ev

    @Provides
    fun eitherMonad(ev: DaggerEitherMonadInstance<L>): Monad<EitherPartialOf<L>> = ev

    @Provides
    fun eitherFoldable(ev: DaggerEitherFoldableInstance<L>): Foldable<EitherPartialOf<L>> = ev

    @Provides
    fun eitherTraverse(ev: DaggerEitherTraverseInstance<L>): Traverse<EitherPartialOf<L>> = ev

    @Provides
    fun eitherSemigroupK(ev: DaggerEitherSemigroupKInstance<L>): SemigroupK<EitherPartialOf<L>> = ev

}

class DaggerEitherFunctorInstance<F> @Inject constructor() : EitherFunctorInstance<F>
class DaggerEitherApplicativeInstance<F> @Inject constructor() : EitherApplicativeInstance<F>
class DaggerEitherMonadInstance<F> @Inject constructor() : EitherMonadInstance<F>
class DaggerEitherFoldableInstance<F> @Inject constructor() : EitherFoldableInstance<F>
class DaggerEitherTraverseInstance<F> @Inject constructor() : EitherTraverseInstance<F>
class DaggerEitherSemigroupKInstance<F> @Inject constructor() : EitherSemigroupKInstance<F>
