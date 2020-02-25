package arrow.aql

class ForBox private constructor() {
  companion object
}
typealias BoxOf<A> = arrow.Kind<ForBox, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A> BoxOf<A>.fix(): Box<A> =
  this as Box<A>

sealed class Box<out A> : BoxOf<A>
