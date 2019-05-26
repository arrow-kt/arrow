package arrow.aql.extensions

import arrow.aql.From
import arrow.aql.Select
import arrow.core.Eval
import arrow.core.ForEval
import arrow.extension
import arrow.core.extensions.eval.applicative.applicative
import arrow.core.extensions.eval.functor.functor
import arrow.core.typeclasses.Applicative
import arrow.core.typeclasses.Functor

@extension
interface EvalSelect : Select<ForEval> {
  override fun functor(): Functor<ForEval> = Eval.functor()
}

@extension
interface EvalFrom : From<ForEval> {
  override fun applicative(): Applicative<ForEval> = Eval.applicative()
}
