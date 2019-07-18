package arrow.persistent.extensions

import arrow.Kind
import arrow.core.Eval
import arrow.extension
import arrow.persistent.data.PersistentMapK
import arrow.persistent.data.PersistentMapKPartialOf
import arrow.persistent.data.fix
import arrow.persistent.extensions.persistentmapk.foldable.forAll
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.MonoidK
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.Traverse
import arrow.undocumented

@extension
interface PersistentMapKFunctor<K> : Functor<PersistentMapKPartialOf<K>> {
  override fun <A, B> Kind<PersistentMapKPartialOf<K>, A>.map(f: (A) -> B): Kind<PersistentMapKPartialOf<K>, B> {
    return fix().map(f)
  }
}

@extension
interface PersistentMapKFoldable<K> : Foldable<PersistentMapKPartialOf<K>> {
  override fun <A, B> Kind<PersistentMapKPartialOf<K>, A>.foldLeft(b: B, f: (B, A) -> B): B {
    return fix().foldLeft(b, f)
  }

  override fun <A, B> Kind<PersistentMapKPartialOf<K>, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
    return fix().foldRight(lb, f)
  }

  override fun <A> Kind<PersistentMapKPartialOf<K>, A>.remove(element: A, monoidK: MonoidK<PersistentMapKPartialOf<K>>, AF: Applicative<PersistentMapKPartialOf<K>>): Kind<PersistentMapKPartialOf<K>, A> {
    return fix().remove(element)
  }
}

// TODO: implement Traverse

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
@extension
interface PersistentMapKSemigroup<K, A> : Semigroup<PersistentMapK<K, A>> {
  override fun PersistentMapK<K, A>.combine(b: PersistentMapK<K, A>): PersistentMapK<K, A> {
    return fix().combine(b)
  }
}

@extension
interface PersistentMapKMonoid<K, A> : Monoid<PersistentMapK<K, A>>, PersistentMapKSemigroup<K, A> {
  override fun empty(): PersistentMapK<K, A> = PersistentMapK()
}

@extension
interface PersistentMapKEq<K, A> : Eq<PersistentMapK<K, A>> {
  override fun PersistentMapK<K, A>.eqv(b: PersistentMapK<K, A>): Boolean {
    TODO("not implemented")
  }
}
