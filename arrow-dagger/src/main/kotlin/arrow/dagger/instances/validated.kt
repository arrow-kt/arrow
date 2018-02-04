package arrow.instances

import arrow.data.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
abstract class ValidatedInstances<L> {

    @Provides
    fun validatedFunctor(ev: DaggerValidatedFunctorInstance<L>): Functor<ValidatedKindPartial<L>> = ev

    @Provides
    fun validatedApplicative(ev: DaggerValidatedApplicativeInstance<L>): Applicative<ValidatedKindPartial<L>> = ev

    @Provides
    fun validatedFoldable(ev: DaggerValidatedFoldableInstance<L>): Foldable<ValidatedKindPartial<L>> = ev

    @Provides
    fun validatedTraverse(ev: DaggerValidatedTraverseInstance<L>): Traverse<ValidatedKindPartial<L>> = ev

    @Provides
    fun validatedSemigroupK(ev: DaggerValidatedSemigroupKInstance<L>): SemigroupK<ValidatedKindPartial<L>> = ev

}

class DaggerValidatedFunctorInstance<F> @Inject constructor() : ValidatedFunctorInstance<F>

class DaggerValidatedApplicativeInstance<F> @Inject constructor(val SE: Semigroup<F>) : ValidatedApplicativeInstance<F> {
    override fun SE(): Semigroup<F> = SE
}

class DaggerValidatedFoldableInstance<F> @Inject constructor() : ValidatedFoldableInstance<F>

class DaggerValidatedTraverseInstance<F> @Inject constructor() : ValidatedTraverseInstance<F>

class DaggerValidatedSemigroupKInstance<F> @Inject constructor(val SE: Semigroup<F>) : ValidatedSemigroupKInstance<F> {
    override fun SE(): Semigroup<F> = SE
}

class DaggerValidatedEqInstance<L, R> @Inject constructor(val eqL: Eq<L>, val eqR: Eq<R>) : ValidatedEqInstance<L, R> {
    override fun EQL(): Eq<L> = eqL
    override fun EQR(): Eq<R> = eqR
}
