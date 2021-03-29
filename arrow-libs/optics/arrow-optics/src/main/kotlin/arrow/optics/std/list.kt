package arrow.optics

import arrow.optics.typeclasses.Cons
import arrow.optics.typeclasses.Index
import arrow.optics.typeclasses.Snoc

operator fun <A, T> PLens<T, T, List<A>, List<A>>.get(i: Int): POptional<T, T, A, A> =
  Index.list<A>().run { this@get.get(i) }

infix fun <A> A.cons(tail: List<A>): List<A> =
  Cons.list<A>().run { this@cons.cons(tail) }

fun <A> List<A>.uncons(): Pair<A, List<A>>? =
  Cons.list<A>().run { this@uncons.uncons() }

infix fun <A> List<A>.snoc(last: A): List<A> =
  Snoc.list<A>().run { this@snoc.snoc(last) }

fun <A> List<A>.unsnoc(): Pair<List<A>, A>? =
  Snoc.list<A>().run { this@unsnoc.unsnoc() }
