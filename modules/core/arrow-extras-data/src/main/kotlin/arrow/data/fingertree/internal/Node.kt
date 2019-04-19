package arrow.data.fingertree.internal

import arrow.data.fingertree.internal.Affix.Three
import arrow.data.fingertree.internal.Affix.Two

internal sealed class Node<T> {

  data class Branch2<T>(val first: T, val second: T) : Node<T>()
  data class Branch3<T>(val first: T, val second: T, val third: T) : Node<T>()

  fun toAffix(): Affix<T> =
    when (this) {
      is Branch2 -> Two(this.first, this.second)
      is Branch3 -> Three(this.first, this.second, this.third)
    }

  fun toArray(): Array<T> =
    when (this) {
      is Branch2 -> arrayOf<Any?>(this.first, this.second) as Array<T>
      is Branch3 -> arrayOf<Any?>(this.first, this.second, this.third) as Array<T>
    }

  companion object {
    fun <T> fromArray(xs: Array<T>): Node<T> = when (xs.size) {
      2 -> Branch2(xs[0], xs[1])
      3 -> Branch3(xs[0], xs[1], xs[2])
      else -> throw IllegalArgumentException("TODO")
    }
  }
}
