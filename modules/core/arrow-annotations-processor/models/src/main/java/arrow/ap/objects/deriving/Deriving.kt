package arrow.ap.objects.deriving

import arrow.deriving
import arrow.typeclasses.Functor

class ForBox private constructor() { companion object }
typealias BoxOf<A> = arrow.Kind<ForBox, A>

@deriving(Functor::class)
data class Box<A>(val value: A): BoxOf<A> {
  fun <B> map(f: (A) -> B): Box<B> = Box(f(value))
}