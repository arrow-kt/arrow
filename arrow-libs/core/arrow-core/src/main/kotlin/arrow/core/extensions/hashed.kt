package arrow.core.extensions

import arrow.Kind
import arrow.core.Eval
import arrow.core.ForHashed
import arrow.core.Hashed
import arrow.core.Ordering
import arrow.core.fix
import arrow.extension
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Foldable
import arrow.typeclasses.Hash
import arrow.typeclasses.Order
import arrow.typeclasses.OrderDeprecation
import arrow.typeclasses.Show
import arrow.typeclasses.hashWithSalt

@extension
interface HashedEq<A> : Eq<Hashed<A>> {
  fun EQA(): Eq<A>
  override fun Hashed<A>.eqv(b: Hashed<A>): Boolean =
    this.hash == b.hash && EQA().run { value.eqv(b.value) }
}

@extension
interface HashedEqK : EqK<ForHashed> {
  override fun <A> Kind<ForHashed, A>.eqK(other: Kind<ForHashed, A>, EQ: Eq<A>): Boolean =
    fix().let { (h1, v1) ->
      other.fix().let { (h2, v2) ->
        h1 == h2 && EQ.run { v1.eqv(v2) }
      }
    }
}

@extension
@Deprecated(OrderDeprecation)
interface HashedOrder<A> : Order<Hashed<A>> {
  fun ORD(): Order<A>
  override fun Hashed<A>.compare(b: Hashed<A>): Ordering = ORD().run { value.compare(b.value) }
  override fun Hashed<A>.eqv(b: Hashed<A>): Boolean =
    this.hash == b.hash && ORD().run { value.eqv(b.value) }
}

@extension
interface HashedShow<A> : Show<Hashed<A>> {
  fun SA(): Show<A>
  override fun Hashed<A>.show(): String = "Hashed(${SA().run { value.show() }})"
}

@extension
interface HashedFoldable : Foldable<ForHashed> {
  override fun <A, B> Kind<ForHashed, A>.foldLeft(b: B, f: (B, A) -> B): B =
    f(b, fix().value)

  override fun <A, B> Kind<ForHashed, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    Eval.defer { f(fix().value, lb) }
}

@extension
interface HashedHash<A> : Hash<Hashed<A>> {
  override fun Hashed<A>.hash(): Int = hash
  override fun Hashed<A>.hashWithSalt(salt: Int): Int = hash.hashWithSalt(salt)
}
