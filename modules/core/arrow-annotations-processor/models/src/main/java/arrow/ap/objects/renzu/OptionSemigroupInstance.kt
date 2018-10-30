package arrow.ap.objects.renzu

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.extension
import arrow.typeclasses.Semigroup

@extension
interface OptionSemigroupInstance<A> : Semigroup<Option<A>> {

  fun SG(): Semigroup<A>

  override fun Option<A>.combine(b: Option<A>): Option<A> =
    when (this) {
      is Some<A> -> when (b) {
        is Some<A> -> Some(SG().run { t.combine(b.t) })
        None -> b
      }
      None -> this
    }
}
