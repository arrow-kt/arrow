package arrow.ap.objects.renzu

import arrow.core.None
import arrow.core.Option
import arrow.instance
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup

@instance
interface OptionMonoidInstance<A> : Monoid<Option<A>>, OptionSemigroupInstance<A> {

  override fun SG(): Semigroup<A>

  override fun empty(): Option<A> = None
}
