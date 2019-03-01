package arrow.data.extensions

import arrow.Kind
import arrow.core.Eval
import arrow.core.Tuple2
import arrow.data.*

import arrow.extension
import arrow.typeclasses.*
import arrow.data.combineK as setCombineK
import kotlin.collections.plus as setPlus

@extension
interface SetKSemigroup<A> : Semigroup<SetK<A>> {
  override fun SetK<A>.combine(b: SetK<A>): SetK<A> =
    (this.setPlus(b)).k()
}

@extension
interface SetKMonoid<A> : Monoid<SetK<A>>, SetKSemigroup<A> {
  override fun empty(): SetK<A> = emptySet<A>().k()
}

@extension
interface SetKEq<A> : Eq<SetK<A>> {

  fun EQ(): Eq<A>

  override fun SetK<A>.eqv(b: SetK<A>): Boolean =
    if (size == b.size) map { aa ->
      b.find { bb -> EQ().run { aa.eqv(bb) } } != null
    }.fold(true) { acc, bool ->
      acc && bool
    }
    else false

}

@extension
interface SetKShow<A> : Show<SetK<A>> {
  override fun SetK<A>.show(): String =
    toString()
}

@extension
interface SetKFoldable : Foldable<ForSetK> {
  override fun <A, B> Kind<ForSetK, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> Kind<ForSetK, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)

  override fun <A> Kind<ForSetK, A>.isEmpty(): kotlin.Boolean =
    fix().isEmpty()
}

@extension
interface SetKSemigroupK : SemigroupK<ForSetK> {
  override fun <A> Kind<ForSetK, A>.combineK(y: Kind<ForSetK, A>): SetK<A> =
    fix().setCombineK(y)
}

@extension
interface SetKSemigroupal: Semigroupal<ForSetK> {
  override fun <A, B> Kind<ForSetK, A>.product(fb: Kind<ForSetK, B>): Kind<ForSetK, Tuple2<A, B>> =
    fb.fix().flatMap { b -> this.fix().map { a -> Tuple2(a,b) } }.toSet().k()
}

@extension
interface SetKMonoidK : MonoidK<ForSetK> {
  override fun <A> empty(): SetK<A> =
    SetK.empty()

  override fun <A> Kind<ForSetK, A>.combineK(y: Kind<ForSetK, A>): SetK<A> =
    fix().setCombineK(y)
}

@extension
interface SetKHash<A> : Hash<SetK<A>>, SetKEq<A> {
  fun HA(): Hash<A>

  override fun EQ(): Eq<A> = HA()

  override fun SetK<A>.hash(): Int = foldLeft(1) { hash, a ->
    31 * hash + HA().run { a.hash() }
  }
}
