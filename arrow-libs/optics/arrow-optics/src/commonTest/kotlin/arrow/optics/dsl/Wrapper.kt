package arrow.optics.dsl

import arrow.optics.Lens
import arrow.optics.PLens
import kotlin.jvm.JvmInline

@JvmInline
value class Wrapper<A>(val value: A) {
  companion object {
    fun <A> lens(): Lens<Wrapper<A>, A> =
      PLens(Wrapper<A>::value) { _, a -> Wrapper(a) }
  }
}
