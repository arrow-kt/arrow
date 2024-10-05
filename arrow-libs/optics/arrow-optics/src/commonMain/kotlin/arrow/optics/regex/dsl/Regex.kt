package arrow.optics.regex.dsl

import arrow.optics.regex.onceOrMore as regexOnceOrMore
import arrow.optics.regex.zeroOrMore as regexZeroOrMore
import arrow.optics.Traversal

public fun <S, A> Traversal<S, A>.onceOrMore(traversal: Traversal<A, A>): Traversal<S, A> =
  this compose regexOnceOrMore(traversal)

public fun <S, A> Traversal<S, A>.zeroOrMore(traversal: Traversal<A, A>): Traversal<S, A> =
  this compose regexZeroOrMore(traversal)
