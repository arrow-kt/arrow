package arrow.generics.shallow.data.examples

import arrow.generics.shallow.End
import arrow.generics.shallow.Repr
import arrow.generics.shallow.data.And
import arrow.generics.shallow.data.Done
import arrow.generics.shallow.data.Generic
import arrow.generics.shallow.data.That
import arrow.generics.shallow.data.This

// this is the cool part! this is how we define a generic function
// simply pattern match over the generic interpretations
// Kotlin even knows when you have covered all cases!
public fun <R : Repr> Generic<R>.gstring(): String = when (this) {
  is This<*, *> -> "$choice { ${value.gstring()} }"
  is That<*, *> -> next.gstring()
  is And<*, *> -> when (rest) {
    is End -> "$name = $value"
    else -> "$name = $value, ${rest.gstring()}"
  }
  is Done -> ""
}

public fun <R : Repr> Generic<R>.geq(other: Generic<R>): Boolean = when (this) {
  is This<*, *> -> when (other) {
    is This<*, *> -> this.value.geq(other.value)
    else -> false
  }
  is That<*, *> -> when (other) {
    is That<*, *> -> this.next.geq(other.next)
    else -> false
  }
  is And<*, *> -> when (other) {
    is And<*, *> -> this.value == other.value && this.rest.geq(other.rest)
    else -> false
  }
  is Done -> when (other) {
    is Done -> true
    else -> false
  }
}

public fun <R : Repr> Generic<R>.gprintln(): Unit =
  println(gstring())
