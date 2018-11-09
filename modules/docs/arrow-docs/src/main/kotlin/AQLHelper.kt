package arrow.aql

import arrow.aql.box.foldable.foldable
import arrow.aql.box.functor.functor
import arrow.aql.box.functorFilter.functorFilter
import arrow.core.Eval
import arrow.core.Option
import arrow.extension
import arrow.mtl.typeclasses.FunctorFilter
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor

//import arrow.higherkind

class ForBox private constructor() {
  companion object
}
typealias BoxOf<A> = arrow.Kind<ForBox, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A> BoxOf<A>.fix(): Box<A> =
  this as Box<A>

//@higherkind 
sealed class Box<out A> : BoxOf<A> {

  object Empty : Box<Nothing>()

  data class Full<A>(val value: A) : Box<A>()

  companion object {
    fun <A> empty(): Box<A> = Empty
  }

}

@extension
interface BoxFunctor : Functor<ForBox> {
  override fun <A, B> BoxOf<A>.map(f: (A) -> B): Box<B> =
    when (val box = fix()) {
      Box.Empty -> Box.empty()
      is Box.Full -> Box.Full(f(box.value))
    }
}

@extension
interface BoxSelect : Select<ForBox> {
  override fun functor(): Functor<ForBox> = Box.functor()
}

@extension
interface BoxFunctorFilter : FunctorFilter<ForBox>, BoxFunctor {
  override fun <A, B> BoxOf<A>.mapFilter(f: (A) -> Option<B>): Box<B> =
    when (val box = fix()) {
      Box.Empty -> Box.empty()
      is Box.Full -> f(box.value).fold(
        { Box.empty<B>() },
        { Box.Full(it) }
      )
    }
}

@extension
interface BoxWhere : Where<ForBox> {
  override fun functorFilter(): FunctorFilter<ForBox> = Box.functorFilter()
}

@extension
interface BoxFoldable : Foldable<ForBox> {
  override fun <A, B> BoxOf<A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().let {
      when (it) {
        is Box.Full -> f(b, it.value)
        Box.Empty -> b
      }
    }

  override fun <A, B> BoxOf<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().let {
      when (it) {
        is Box.Full -> f(it.value, lb)
        Box.Empty -> lb
      }
    }
}

@extension
interface BoxGroupBy : GroupBy<ForBox> {
  override fun foldable(): Foldable<ForBox> = Box.foldable()
}

@extension
interface BoxCount : Count<ForBox> {
  override fun foldable(): Foldable<ForBox> = Box.foldable()
}

@extension
interface BoxSum : Sum<ForBox> {
  override fun foldable(): Foldable<ForBox> = Box.foldable()
}

@extension
interface BoxOrderBy : OrderBy<ForBox> {
  override fun foldable(): Foldable<ForBox> = Box.foldable()
}

@extension
interface BoxUnion : Union<ForBox> {
  override fun foldable(): Foldable<ForBox> = Box.foldable()
}
