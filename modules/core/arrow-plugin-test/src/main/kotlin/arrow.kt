package arrow.extreme

import arrow.Kind
import kotlin.reflect.KProperty

/**
 * General purpose runtime validation
 */
open class Refined<T>(private val value: T, private val validator: (T) -> Boolean) {
  operator fun getValue(thisRef: Any?, property: KProperty<*>): T? =
    if (validator(value)) value else null
}

operator fun Int?.plus(b: Int?): Int? =
  if (this != null && b != null) this + b
  else null

fun Int.Companion.Positive(value: Int): Refined<Int> = Refined(value, { it > 0 })




operator fun <F, A> Kind<F, A>.getValue(thisRef: Any?, property: KProperty<*>): A = TODO()
class ForId
class Id<A>(a: A) : Kind<ForId, A>

class ForList
class List<A>(vararg val a: A) : Kind<ForList, A> {
  companion object {
    operator fun <A> A.getValue(thisRef: Any?, property: KProperty<*>): List<A> =
      List(this)
  }
}
