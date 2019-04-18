package arrow.data.fingertree.internal

internal sealed class Node<T> {

  abstract fun toList(): List<T>

  data class Branch2<T>(val first: T, val second: T) : Node<T>() {
    override fun toList() = listOf(this.first, this.second)
  }

  data class Branch3<T>(val first: T, val second: T, val third: T) : Node<T>() {
    override fun toList() = listOf(this.first, this.second, this.third)
  }

  companion object {
    fun <T> fromList(xs: List<T>): Node<T> = when (xs.size) {
      2 -> Branch2(xs[0], xs[1])
      3 -> Branch3(xs[0], xs[1], xs[2])
      else -> throw IllegalArgumentException("TODO")
    }
  }
}
