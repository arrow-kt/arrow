package arrow.recursion.extensions

import arrow.Kind
import arrow.core.SequenceK
import arrow.core.k
import arrow.core.toOption
import arrow.extension
import arrow.recursion.extensions.listf.functor.functor
import arrow.recursion.pattern.ListF
import arrow.recursion.pattern.ListFPartialOf
import arrow.recursion.pattern.fix
import arrow.recursion.typeclasses.Birecursive
import arrow.recursion.typeclasses.Corecursive
import arrow.recursion.typeclasses.Recursive
import arrow.typeclasses.Functor

@extension
interface SequenceKBirecursive<A> : Birecursive<SequenceK<A>, ListFPartialOf<A>> {
  override fun FF(): Functor<ListFPartialOf<A>> = ListF.functor()

  override fun Kind<ListFPartialOf<A>, SequenceK<A>>.embedT(): SequenceK<A> = when (val l = fix()) {
    is ListF.NilF -> SequenceK.empty()
    is ListF.ConsF -> (sequenceOf(l.a) + l.tail).k()
  }

  override fun SequenceK<A>.projectT(): Kind<ListFPartialOf<A>, SequenceK<A>> =
    // firstOption is strict and breaks for infinite sequences!
    firstOrNull().toOption().fold({ ListF.NilF() }, { a -> ListF.ConsF(a, drop(1).k()) })
}

@extension
interface SequenceKRecursive<A> : Recursive<SequenceK<A>, ListFPartialOf<A>>, SequenceKBirecursive<A>

@extension
interface SequenceKCorecursive<A> : Corecursive<SequenceK<A>, ListFPartialOf<A>>, SequenceKBirecursive<A>
