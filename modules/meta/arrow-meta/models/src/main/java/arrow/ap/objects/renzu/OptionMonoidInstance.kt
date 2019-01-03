package arrow.ap.objects.renzu

import arrow.core.None
import arrow.core.Option
import arrow.extension
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup

@extension
interface OptionMonoidInstance<A> : Monoid<Option<A>>, OptionSemigroupInstance<A> {

  override fun SG(): Semigroup<A>

  override fun empty(): Option<A> = None
}
