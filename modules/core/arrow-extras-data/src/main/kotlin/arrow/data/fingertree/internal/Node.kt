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

  companion object {

    fun <A> fromList(items: List<A>) = this.fromListHelper(items, emptyList())

    private tailrec fun <A> fromListHelper(remItems: List<A>, items: List<Node<A>>): List<Node<A>> = when(remItems.size) {
      0 -> TODO("Throw exception?")
      1 -> TODO("Throw exception?")
      2 -> items + listOf(Branch2(remItems[0], remItems[1]))
      3 -> items + listOf(Branch3(remItems[0], remItems[1], remItems[2]))
      else -> fromListHelper(remItems.drop(2), items + listOf(Branch2(remItems[0], remItems[1])))
    }
  }
}
