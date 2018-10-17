package arrow.dagger.instances

import arrow.core.*
import arrow.instances.eval.applicative.applicative
import arrow.instances.eval.bimonad.bimonad
import arrow.instances.eval.comonad.comonad
import arrow.instances.eval.functor.functor
import arrow.instances.eval.monad.monad
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides

@Module
class EvalInstances {

  @Provides
  fun evalFunctor(): Functor<ForEval> = Eval.functor()

  @Provides
  fun evalApplicative(): Applicative<ForEval> = Eval.applicative()

  @Provides
  fun evalMonad(): Monad<ForEval> = Eval.monad()

  @Provides
  fun evalComonad(): Comonad<ForEval> = Eval.comonad()

  @Provides
  fun evalBimonad(): Bimonad<ForEval> = Eval.bimonad()

}