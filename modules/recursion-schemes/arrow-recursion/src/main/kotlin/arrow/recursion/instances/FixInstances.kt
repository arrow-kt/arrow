package arrow.recursion.instances

import arrow.*
import arrow.recursion.*
import arrow.recursion.typeclass.Birecursive
import arrow.typeclasses.Functor
import arrow.typeclasses.Nested

@instance(Fix::class)
interface FixInstances<F> : Birecursive<ForFix, F> {
  fun FF(): Functor<F>

  override fun projectT(fg: FixOf<F>): Kind<Nested<ForFix, F>, FixOf<F>> =
    fg.fix().projectT()

  override fun embedT(compFG: Kind<Nested<ForFix, F>, FixOf<F>>): FixOf<F> =
    Fix.embedT(compFG, FF())
}

class FixContext<F>(val FF: Functor<F>) : FixInstances<F> {
  override fun FF(): Functor<F> = FF
}

class FixContextPartiallyApplied<F>(val FF: Functor<F>) {
  fun <A> run(f: FixContext<F>.() -> A): A =
    f(FixContext(FF))
}

fun <F> Fix(FF: Functor<F>): FixContextPartiallyApplied<F> =
  FixContextPartiallyApplied(FF)

fun <F, A> with(c: FixContextPartiallyApplied<F>, f: FixContext<F>.() -> A): A =
  c.run(f)