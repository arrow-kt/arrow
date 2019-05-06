package arrow.data.fingertree

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.Tuple2
import arrow.data.fingertree.internal.Affix
import arrow.data.fingertree.internal.Node
import arrow.higherkind

@higherkind
sealed class FingerTree<T> : FingerTreeOf<T> {

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

  fun head() = this.viewL().flatMap { Option.just(it.a) }
  fun tail() = this.viewL().flatMap { Option.just(it.b) }

  fun last(): Option<T> =
    this.viewR().flatMap { Option.just(it.a) }

  fun init(): Option<FingerTree<T>> =
    this.viewR().flatMap { Option.just(it.b) }

  fun isEmpty() = this is Empty

  fun asList(): List<T> = this.asSequence().toList()

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

  infix fun concat(tree: FingerTree<T>) = this.concatHelper(emptyList(), tree)

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    return true
  }

  override fun hashCode(): Int {
    return javaClass.hashCode()
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

  private fun concatHelper(items: List<T>, right: FingerTree<T>): FingerTree<T> {

    return when (this) {
      is Deep -> when (right) {
        is Deep -> { // left and right are Deep
          Deep(
            this.prefix,
            this.deeper.concatHelper(Node.fromList(this.suffix.toList() + items + right.prefix.toList()), right.deeper),
            right.suffix
          )
        }

        else -> { // right is Empty or Single
          var tree = this
          items.forEach {
            tree = tree.append(it)
          }

          if (right is Single) {
            tree = tree.append(right.a)
          }

          tree
        }
      }
      else -> { // left is Empty or Single
        var tree = right
        items.asReversed().forEach {
          tree = tree.prepend(it)
        }

        if (this is Single) {
          tree = tree.prepend(this.a)
        }

        tree
      }
    }
  }

  companion object {

    fun <A> empty(): FingerTree<A> = Empty()

    fun <A> single(item: A): FingerTree<A> = Single(item)

    fun <A> fromList(list: List<A>): FingerTree<A> {
      var fingerTree = empty<A>()
      for (i in 0 until list.size) {
        fingerTree = fingerTree.append(list[i])
      }
      return fingerTree
    }

    fun <A> fromArgs(vararg items: A): FingerTree<A> =
      fromList(items.toList())
  }

}
