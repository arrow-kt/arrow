package arrow.dagger.instances

import arrow.core.EitherKindPartial
import arrow.instances.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
abstract class EitherInstances<L> {

    @Provides
    fun eitherFunctor(ev: DaggerEitherFunctorInstance<L>): Functor<EitherKindPartial<L>> = ev

    @Provides
    fun eitherApplicative(ev: DaggerEitherApplicativeInstance<L>): Applicative<EitherKindPartial<L>> = ev

    @Provides
    fun eitherMonad(ev: DaggerEitherMonadInstance<L>): Monad<EitherKindPartial<L>> = ev

    @Provides
    fun eitherFoldable(ev: DaggerEitherFoldableInstance<L>): Foldable<EitherKindPartial<L>> = ev

    @Provides
    fun eitherTraverse(ev: DaggerEitherTraverseInstance<L>): Traverse<EitherKindPartial<L>> = ev

    @Provides
    fun eitherSemigroupK(ev: DaggerEitherSemigroupKInstance<L>): SemigroupK<EitherKindPartial<L>> = ev

}

class DaggerEitherFunctorInstance<F> @Inject constructor() : EitherFunctorInstance<F>
class DaggerEitherApplicativeInstance<F> @Inject constructor() : EitherApplicativeInstance<F>
class DaggerEitherMonadInstance<F> @Inject constructor() : EitherMonadInstance<F>
class DaggerEitherFoldableInstance<F> @Inject constructor() : EitherFoldableInstance<F>
class DaggerEitherTraverseInstance<F> @Inject constructor() : EitherTraverseInstance<F>
class DaggerEitherSemigroupKInstance<F> @Inject constructor() : EitherSemigroupKInstance<F>
