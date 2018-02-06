package arrow.dagger.instances

import arrow.data.*
import arrow.instances.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
abstract class CoproductInstances<F, G> {

    @Provides
    fun coproductFunctor(ev: DaggerCoproductFunctorInstance<F, G>): Functor<CoproductKindPartial<F, G>> = ev

    @Provides
    fun coproductComonad(ev: DaggerCoproductComonadInstance<F, G>): Comonad<CoproductKindPartial<F, G>> = ev

    @Provides
    fun coproductFoldable(ev: DaggerCoproductFoldableInstance<F, G>): Foldable<CoproductKindPartial<F, G>> = ev

    @Provides
    fun coproductTraverse(ev: DaggerCoproductTraverseInstance<F, G>): Traverse<CoproductKindPartial<F, G>> = ev

}

class DaggerCoproductFunctorInstance<F, G> @Inject constructor(val FF: Functor<F>, val FG: Functor<G>) : CoproductFunctorInstance<F, G> {
    override fun FF(): Functor<F> = FF
    override fun FG(): Functor<G> = FG
}

class DaggerCoproductComonadInstance<F, G> @Inject constructor(val CF: Comonad<F>, val CG: Comonad<G>) : CoproductComonadInstance<F, G> {
    override fun CF(): Comonad<F> = CF
    override fun CG(): Comonad<G> = CG
}

class DaggerCoproductFoldableInstance<F, G> @Inject constructor(val FF: Foldable<F>, val FG: Foldable<G>) : CoproductFoldableInstance<F, G> {
    override fun FF(): Foldable<F> = FF
    override fun FG(): Foldable<G> = FG
}

class DaggerCoproductTraverseInstance<F, G> @Inject constructor(val TF: Traverse<F>, val TG: Traverse<G>) : CoproductTraverseInstance<F, G> {
    override fun TF(): Traverse<F> = TF
    override fun TG(): Traverse<G> = TG
}