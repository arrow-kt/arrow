package arrow.data.fingertree.internal

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.Tuple2

internal sealed class FingerTreeInternal<T> {

  class Empty<T> : FingerTreeInternal<T>()

  data class Single<T>(val a: T) : FingerTreeInternal<T>()

  data class Deep<T>(
    val prefix: Affix<T>,
    val deeper: FingerTreeInternal<Node<T>>,
    val suffix: Affix<T>
  ) : FingerTreeInternal<T>()


  fun prepend(item: T): FingerTreeInternal<T> = when (val tree = this) {

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

  fun append(item: T): FingerTreeInternal<T> = when (val tree = this) {

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

  fun viewL(): Option<Tuple2<T, FingerTreeInternal<T>>> = when (this) {
    is Empty -> Option.empty()
    is Single -> Option.just(Tuple2(this.a, Empty()))
    is Deep -> when (this.prefix) {
      is Affix.One -> {

        this.deeper.viewL().fold(
          { Option.just(Tuple2(this.prefix.a, suffix.toFingerTree())) },
          { (node, tree) -> Option.just(Tuple2(this.prefix.a, Deep(Affix.fromList(node.toList()), tree, this.suffix))) }
        )
      }

      else -> Option.just(
        Tuple2(this.prefix.toList()[0],
          Deep(Affix.fromList(this.prefix.toList().drop(1)), this.deeper, this.suffix)
        ))
    }

  }


  fun viewR(): Option<Tuple2<T, FingerTreeInternal<T>>> = when (this) {
      is Empty -> Option.empty()
      is Single -> Option.just(Tuple2(this.a, Empty()))
      is Deep -> when (this.suffix) {
        is Affix.One -> {

          this.deeper.viewR().fold(
            { Option.just(Tuple2(this.suffix.a, prefix.toFingerTree())) },
            { (node, tree) -> Option.just(Tuple2(this.suffix.a, Deep(this.prefix, tree, Affix.fromList(node.toList())))) }
          )
        }

        else -> Option.just(
          Tuple2(this.suffix.toList().last(),
            Deep(this.prefix, this.deeper, Affix.fromList(this.suffix.toList().dropLast(1)))
          ))
      }
    }

  fun asList(): List<T> = this.asListHelper(emptyList(), this)

  fun asSequence(): Sequence<T> = TODO()

  private tailrec fun asListHelper(soFar: List<T>, tree: FingerTreeInternal<T>): List<T> {
    return when (val res = tree.viewL()) {
      is None -> soFar
      is Some -> asListHelper(soFar + listOf(res.t.a), res.t.b)
    }
  }

}
