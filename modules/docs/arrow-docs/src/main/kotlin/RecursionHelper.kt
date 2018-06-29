package arrow.recursion

import arrow.higherkind
import arrow.instance
import arrow.typeclasses.Functor

sealed class IntList
object Nil : IntList()
data class Cons(val head: Int, val tail: IntList) : IntList()

@higherkind sealed class IntListPattern<out A> : IntListPatternOf<A> { companion object }
object NilPattern : IntListPattern<Nothing>()
@higherkind data class ConsPattern<out A>(val head: Int, val tail: A) : IntListPattern<A>()

@instance(IntListPattern::class)
interface IntListPatternFunctorInstance : Functor<ForIntListPattern> {
  override fun <A, B> IntListPatternOf<A>.map(f: (A) -> B): IntListPatternOf<B> {
    val lp = fix()
    return when (lp) {
      NilPattern -> NilPattern
      is ConsPattern -> ConsPattern(lp.head, f(lp.tail))
    }
  }
}

sealed class IntTree
data class Leaf(val value: Int) : IntTree()
data class Node(val left: IntTree, val right: IntTree) : IntTree()
