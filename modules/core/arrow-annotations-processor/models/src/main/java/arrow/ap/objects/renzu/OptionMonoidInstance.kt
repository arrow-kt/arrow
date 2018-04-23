package arrow.ap.objects.renzu

import arrow.core.None
import arrow.core.Option
import arrow.instance
import arrow.typeclasses.Monoid

@instance(Option::class)
interface OptionMonoidInstance<A> : OptionSemigroupInstance<A>, Monoid<Option<A>> {
  override fun empty(): Option<A> = None
}