package arrow.data.fingertree

import arrow.Kind
import arrow.core.*
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


  fun prependAll(items: List<T>): FingerTree<T> {
    var buffer = this
    items.forEach {
      buffer = buffer.prepend(it)
    }
    return buffer
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

  fun appendAll(items: List<T>): FingerTree<T> {
    var buffer = this
    items.forEach {
      buffer = buffer.append(it)
    }
    return buffer
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

  fun size(): Int =
    when (this) {
      is Empty -> 0
      is Single -> 1
      is Deep -> this.asList().size
    }

  fun asList(): List<T> = this.asSequence().toList()

  fun asSequence(): Sequence<T> = sequence {
    this.asSequenceHelper(this@FingerTree)
  }

  fun rotateClockwise(times: Int): FingerTree<T> {
    var tree: FingerTree<T> = this

    val rotationCount = if (tree.size() == 0 || tree.size() == 1) {
      0
    } else {
      times % tree.size()
    }

    repeat(rotationCount) {
      val leftView = tree.viewL()
      if (leftView is Some) {
        tree = leftView.t.b.append(leftView.t.a)
      }
    }
    return tree
  }

  fun rotateCounterClockwise(times: Int): FingerTree<T> {
    var tree: FingerTree<T> = this

    val rotationCount = if (tree.size() == 0 || tree.size() == 1) {
      0
    } else {
      times % tree.size()
    }

    repeat(rotationCount) {
      val rightView = tree.viewR()
      if (rightView is Some) {
        tree = rightView.t.b.prepend(rightView.t.a)
      }
    }
    return tree
  }

  infix fun concat(tree: FingerTree<T>) = this.concatHelper(emptyList(), tree)

  fun <B> map(f: (T) -> B): FingerTree<B> = fromList(fix().asList().map(f))

  fun <B> flatMap(f: (T) -> FingerTreeOf<B>): FingerTree<B> =
    this.asList()
      .map { f(it) }
      .fold(empty(), { a, b -> a concat b.fix() })

  fun <B> ap(ff: FingerTreeOf<(T) -> B>): FingerTree<B> = ff.fix().flatMap { f -> map(f) }

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

  private fun concatHelper(items: List<T>, right: FingerTree<T>): FingerTree<T> =
    when (this) {
      is Deep -> when (right) {
        is Deep -> { // left and right are Deep
          Deep(
            this.prefix,
            this.deeper.concatHelper(Node.fromList(this.suffix.toList() + items + right.prefix.toList()), right.deeper),
            right.suffix
          )
        }

        else -> { // right is Empty or Single
          var tree = this.appendAll(items)

          if (right is Single) {
            tree = tree.append(right.a)
          }

          tree
        }
      }
      else -> { // left is Empty or Single
        var tree = right.prependAll(items.asReversed())

        if (this is Single) {
          tree = tree.prepend(this.a)
        }

        tree
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

    fun <A, B> tailRecM(a: A, f: (A) -> Kind<ForFingerTree, Either<A, B>>): FingerTree<B> =
      tailRecMHelper(empty(), f, f(a).fix())

    private tailrec fun <A, B> tailRecMHelper(
      buf: FingerTree<B>,
      f: (A) -> Kind<ForFingerTree, Either<A, B>>,
      v: FingerTree<Either<A, B>>
    ): FingerTree<B> {
      return if (!v.isEmpty()) {
        when (val head: Either<A, B> = v.head().getOrElse {
          throw RuntimeException("Non empty finger tree must have a head element.")
        }) {
          is Either.Right -> tailRecMHelper(
            buf.append(head.b),
            f,
            v.tail().fold(
              { empty<Either<A, B>>() },
              { it }
            )
          )

          is Either.Left -> tailRecMHelper(
            buf,
            f,
            v.tail().fold(
              { f(head.a).fix() },
              { f(head.a).fix() concat it }
            )
          )
        }
      } else buf
    }
  }

}
