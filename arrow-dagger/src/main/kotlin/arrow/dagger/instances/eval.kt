package arrow.dagger.instances

import arrow.core.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides

@Module
class EvalInstances {

    @Provides
    fun evalFunctor(): Functor<EvalHK> = Eval.functor()

    @Provides
    fun evalApplicative(): Applicative<EvalHK> = Eval.applicative()

    @Provides
    fun evalMonad(): Monad<EvalHK> = Eval.monad()

    @Provides
    fun evalComonad(): Comonad<EvalHK> = Eval.comonad()

    @Provides
    fun evalBimonad(): Bimonad<EvalHK> = Eval.bimonad()

}