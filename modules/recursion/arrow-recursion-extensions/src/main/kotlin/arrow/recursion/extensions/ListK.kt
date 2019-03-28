package arrow.recursion.extensions

import arrow.Kind
import arrow.core.Eval
import arrow.core.extensions.eval.monad.binding
import arrow.data.ListK
import arrow.data.combineK
import arrow.data.k
import arrow.extension
import arrow.recursion.data.ListF
import arrow.recursion.data.ListFPartialOf
import arrow.recursion.data.fix
import arrow.recursion.extensions.listf.functor.functor
import arrow.recursion.typeclasses.Birecursive
import arrow.typeclasses.Functor

@extension
interface ListFFunctor<I> : Functor<ListFPartialOf<I>> {
  override fun <R, S> Kind<ListFPartialOf<I>, R>.map(f: (R) -> S): Kind<ListFPartialOf<I>, S> =
    fix().map(f)
}

@extension
interface ListKBirecursive<A> : Birecursive<ListK<A>, ListFPartialOf<A>> {
  override fun FF(): Functor<ListFPartialOf<A>> = ListF.functor()

  override fun ListK<A>.projectT(): Kind<ListFPartialOf<A>, ListK<A>> = when {
    isEmpty() -> ListF.NilF()
    else -> ListF.ConsF(first(), drop(1).k())
  }

  override fun Kind<ListFPartialOf<A>, Eval<ListK<A>>>.embedT(): Eval<ListK<A>> =
    when (val ls = fix()) {
      is ListF.NilF -> Eval.now(ListK.empty())
      is ListF.ConsF -> binding { ListK.just(ls.a).combineK(ls.tail.bind()) }
    }
}

