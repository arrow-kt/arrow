package arrow.dagger.instances

import arrow.data.OptionTKindPartial
import arrow.instances.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
abstract class OptionTInstances<F> {

    @Provides
    fun optionTFunctor(ev: DaggerOptionTFunctorInstance<F>): Functor<OptionTKindPartial<F>> = ev

    @Provides
    fun optionTApplicative(ev: DaggerOptionTApplicativeInstance<F>): Applicative<OptionTKindPartial<F>> = ev

    @Provides
    fun optionTMonad(ev: DaggerOptionTMonadInstance<F>): Monad<OptionTKindPartial<F>> = ev

    @Provides
    fun optionTFoldable(ev: DaggerOptionTFoldableInstance<F>): Foldable<OptionTKindPartial<F>> = ev

    @Provides
    fun optionTTraverse(ev: DaggerOptionTTraverseInstance<F>): Traverse<OptionTKindPartial<F>> = ev

    @Provides
    fun optionTSemigroupK(ev: DaggerOptionTSemigroupKInstance<F>): SemigroupK<OptionTKindPartial<F>> = ev

    @Provides
    fun optionTMonoidK(ev: DaggerOptionTMonoidKInstance<F>): MonoidK<OptionTKindPartial<F>> = ev

}

class DaggerOptionTFunctorInstance<F> @Inject constructor(val FF: Functor<F>) : OptionTFunctorInstance<F> {
    override fun FF(): Functor<F> = FF
}

class DaggerOptionTApplicativeInstance<F> @Inject constructor(val FF: Monad<F>) : OptionTApplicativeInstance<F> {
    override fun FF(): Monad<F> = FF
}

class DaggerOptionTMonadInstance<F> @Inject constructor(val FF: Monad<F>) : OptionTMonadInstance<F> {
    override fun FF(): Monad<F> = FF
}

class DaggerOptionTFoldableInstance<F> @Inject constructor(val FFF: Foldable<F>) : OptionTFoldableInstance<F> {
    override fun FFF(): Foldable<F> = FFF
}

class DaggerOptionTTraverseInstance<F> @Inject constructor(val FFF: Traverse<F>) : OptionTTraverseInstance<F> {
    override fun FFF(): Traverse<F> = FFF
}

class DaggerOptionTSemigroupKInstance<F> @Inject constructor(val FF: Monad<F>) : OptionTSemigroupKInstance<F> {
    override fun FF(): Monad<F> = FF
}

class DaggerOptionTMonoidKInstance<F> @Inject constructor(val FF: Monad<F>) : OptionTMonoidKInstance<F> {
    override fun FF(): Monad<F> = FF
}