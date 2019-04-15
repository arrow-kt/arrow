package arrow.recursion

import arrow.Kind
import arrow.core.Tuple2
import arrow.higherkind
import arrow.typeclasses.Functor

sealed class IntList
object Nil : IntList()
data class Cons(val head: Int, val tail: IntList) : IntList()

@higherkind sealed class IntListPattern<out A> : IntListPatternOf<A> { companion object }
object NilPattern : IntListPattern<Nothing>()
@higherkind data class ConsPattern<out A>(val head: Int, val tail: A) : IntListPattern<A>()

interface IntListPatternFunctor : Functor<ForIntListPattern> {
  override fun <A, B> IntListPatternOf<A>.map(f: (A) -> B): IntListPatternOf<B> {
    val lp = fix()
    return when (lp) {
      NilPattern -> NilPattern
      is ConsPattern -> ConsPattern(lp.head, f(lp.tail))
    }
  }
}

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER"
)
fun <A, B> Kind<ForIntListPattern, A>.map(arg1: Function1<A, B>): IntListPattern<B> = IntListPattern.functor().run {
  map<A, B>(arg1) as IntListPattern<B>
}

@JvmName("imap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER"
)
fun <A, B> Kind<ForIntListPattern, A>.imap(arg1: Function1<A, B>, arg2: Function1<B, A>): IntListPattern<B> = IntListPattern.functor().run {
  imap<A, B>(arg1, arg2) as IntListPattern<B>
}

@JvmName("lift")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER"
)
fun <A, B> lift(arg0: Function1<A, B>): Function1<Kind<ForIntListPattern, A>, Kind<ForIntListPattern, B>> = IntListPattern
  .functor()
  .lift<A, B>(arg0) as kotlin.Function1<Kind<ForIntListPattern, A>, Kind<ForIntListPattern, B>>

@JvmName("unit")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER"
)
fun <A> Kind<ForIntListPattern, A>.void(): IntListPattern<Unit> = IntListPattern.functor().run {
  unit<A>() as IntListPattern<kotlin.Unit>
}

@JvmName("fproduct")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER"
)
fun <A, B> Kind<ForIntListPattern, A>.fproduct(arg1: Function1<A, B>): IntListPattern<Tuple2<A, B>> = IntListPattern.functor().run {
  fproduct<A, B>(arg1) as IntListPattern<arrow.core.Tuple2<A, B>>
}

@JvmName("as")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER"
)
fun <A, B> Kind<ForIntListPattern, A>.`as`(arg1: B): IntListPattern<B> = IntListPattern.functor().run {
  `as`<A, B>(arg1) as IntListPattern<B>
}

@JvmName("tupleLeft")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER"
)
fun <A, B> Kind<ForIntListPattern, A>.tupleLeft(arg1: B): IntListPattern<Tuple2<B, A>> = IntListPattern.functor().run {
  tupleLeft<A, B>(arg1) as IntListPattern<arrow.core.Tuple2<B, A>>
}

@JvmName("tupleRight")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER"
)
fun <A, B> Kind<ForIntListPattern, A>.tupleRight(arg1: B): IntListPattern<Tuple2<A, B>> = IntListPattern.functor().run {
  tupleRight<A, B>(arg1) as IntListPattern<arrow.core.Tuple2<A, B>>
}

@JvmName("widen")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER"
)
fun <B, A : B> Kind<ForIntListPattern, A>.widen(): IntListPattern<B> = IntListPattern.functor().run {
  widen<B, A>() as IntListPattern<B>
}

fun IntListPattern.Companion.functor(): IntListPatternFunctor = object : IntListPatternFunctor {
}

sealed class IntTree
data class Leaf(val value: Int) : IntTree()
data class Node(val left: IntTree, val right: IntTree) : IntTree()
