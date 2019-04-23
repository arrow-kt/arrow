package arrow.dagger.extensions

import arrow.core.Eval
import arrow.core.ForEval
import arrow.core.extensions.eval.applicative.applicative
import arrow.core.extensions.eval.bimonad.bimonad
import arrow.core.extensions.eval.comonad.comonad
import arrow.core.extensions.eval.functor.functor
import arrow.core.extensions.eval.monad.monad
import arrow.typeclasses.Applicative
import arrow.typeclasses.Bimonad
import arrow.typeclasses.Comonad
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
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
