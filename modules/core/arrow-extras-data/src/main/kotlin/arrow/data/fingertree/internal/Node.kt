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
}
