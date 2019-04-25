package arrow.data.fingertree

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.Tuple2
import arrow.data.fingertree.internal.Affix
import arrow.data.fingertree.internal.Node

sealed class FingerTree<T> {

  internal class Empty<T> : FingerTree<T>() {
    override fun toString(): String = "Empty()"
  }

  internal data class Single<T>(val a: T) : FingerTree<T>()

  internal data class Deep<T>(
    val prefix: Affix<T>,
    val deeper: FingerTree<Node<T>>,
    val suffix: Affix<T>
  ) : FingerTree<T>()


  fun prepend(item: T): FingerTree<T> =
    when (val tree = this) {

      is Empty -> Single(item)
      is Single -> Deep(Affix.One(item), Empty(), Affix.One(tree.a))
      is Deep -> {
        when (val prefix = tree.prefix) {
          is Affix.Four ->
            Deep(
              Affix.Two(item, prefix.a),
              tree.deeper.prepend(Node.Branch3(prefix.b, prefix.c, prefix.d)),
              tree.suffix
            )
          else -> Deep(tree.prefix.prepend(item), tree.deeper, tree.suffix)
        }
      }
    }

  fun append(item: T): FingerTree<T> =
    when (val tree = this) {

      is Empty -> Single(item)
      is Single -> Deep(Affix.One(tree.a), Empty(), Affix.One(item))
      is Deep -> {
        when (val suffix = tree.suffix) {
          is Affix.Four ->
            Deep(
              tree.prefix,
              tree.deeper.append(Node.Branch3(suffix.a, suffix.b, suffix.c)),
              Affix.Two(suffix.d, item)
            )

          else -> Deep(tree.prefix, tree.deeper, tree.suffix.append(item))
        }
      }
    }

  fun viewL(): Option<Tuple2<T, FingerTree<T>>> =
    when (this) {
      is Empty -> Option.empty()
      is Single -> Option.just(Tuple2(this.a, Empty()))
      is Deep -> when (this.prefix) {
        is Affix.One -> {

          this.deeper.viewL().fold(
            { Option.just(Tuple2(this.prefix.a, suffix.toFingerTree())) },
            { (node, tree) -> Option.just(Tuple2(this.prefix.a, Deep(node.toAffix(), tree, this.suffix))) }
          )
        }

        else -> Option.just(
          Tuple2(this.prefix.head(),
            Deep(this.prefix.dropHead(), this.deeper, this.suffix)
          ))
      }

    }

  fun viewR(): Option<Tuple2<T, FingerTree<T>>> =
    when (this) {
      is Empty -> Option.empty()
      is Single -> Option.just(Tuple2(this.a, Empty()))
      is Deep -> when (this.suffix) {
        is Affix.One -> {

          this.deeper.viewR().fold(
            { Option.just(Tuple2(this.suffix.a, prefix.toFingerTree())) },
            { (node, tree) -> Option.just(Tuple2(this.suffix.a, Deep(this.prefix, tree, node.toAffix()))) }
          )
        }

        else -> Option.just(
          Tuple2(this.suffix.last(),
            Deep(this.prefix, this.deeper, this.suffix.dropLast())
          ))
      }
    }

  fun isEmpty() = this is Empty

  fun asList(): List<T> = this.asListHelper(emptyList(), this)

  fun asSequence(): Sequence<T> = sequence {
    this.asSequenceHelper(this@FingerTree)
  }

  fun rotateClockwise(times: Int): FingerTree<T> {
    var tree: FingerTree<T> = this

    repeat(times) {
      when (val head = tree.viewL()) {
        is None -> Empty<T>()
        is Some -> {
          tree = head.t.b.append(head.t.a)
        }
      }
    }
    return tree
  }

  fun rotateCounterClockwise(times: Int): FingerTree<T> {
    var tree: FingerTree<T> = this
    repeat(times) {

      when (val head = tree.viewR()) {
        is None -> Empty<T>()
        is Some -> {
          tree = head.t.b.prepend(head.t.a)
        }
      }
    }
    return tree
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    return true
  }

  override fun hashCode(): Int {
    return javaClass.hashCode()
  }

  private tailrec fun asListHelper(soFar: List<T>, tree: FingerTree<T>): List<T> {
    return when (val res = tree.viewL()) {
      is None -> soFar
      is Some -> asListHelper(soFar + listOf(res.t.a), res.t.b)
    }
  }

  private tailrec suspend fun SequenceScope<T>.asSequenceHelper(fingerTree: FingerTree<T>) {
    when (val res = fingerTree.viewL()) {
      is Some -> {
        yield(res.t.a)
        return asSequenceHelper(res.t.b)
      }
      is None -> return
    }
  }

  companion object {

    fun <A> empty(): FingerTree<A> = Empty()

    fun <A> single(item: A): FingerTree<A> = Single(item)

    fun <A> fromList(list: List<A>): FingerTree<A> {
      var fingerTree = empty<A>()
      list.forEach {
        fingerTree = fingerTree.append(it)
      }
      return fingerTree
    }

    fun <A> fromArgs(vararg items: A): FingerTree<A> =
      fromList(items.toList())
  }

}
