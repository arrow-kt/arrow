package arrow.ap.objects.renzu

import arrow.core.None
import arrow.core.Option
import arrow.extension
import arrow.typeclasses.Monoid

@extension
interface OptionMonoidInstance<A> : OptionSemigroupInstance<A>, Monoid<Option<A>> {
  override fun empty(): Option<A> = None
}
