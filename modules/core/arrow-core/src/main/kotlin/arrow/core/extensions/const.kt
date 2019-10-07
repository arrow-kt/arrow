package arrow.core.extensions

import arrow.Kind
import arrow.core.Eval
import arrow.core.Option
import arrow.core.Tuple2
import arrow.extension
import arrow.typeclasses.Applicative
import arrow.typeclasses.Apply
import arrow.typeclasses.Const
import arrow.typeclasses.ConstOf
import arrow.typeclasses.ConstPartialOf
import arrow.typeclasses.Contravariant
import arrow.typeclasses.Divide
import arrow.typeclasses.Divisible
import arrow.typeclasses.Eq
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.Hash
import arrow.typeclasses.Invariant
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.Traverse
import arrow.typeclasses.TraverseFilter
import arrow.typeclasses.fix

@extension
class ConstInvariant<A> : Invariant<ConstPartialOf<A>> {
  override fun <T, U> ConstOf<A, T>.imap(f: (T) -> U, g: (U) -> T): Const<A, U> =
    fix().retag()
}

@extension
class ConstContravariant<A> : Contravariant<ConstPartialOf<A>> {
  override fun <T, U> ConstOf<A, T>.contramap(f: (U) -> T): Const<A, U> =
    fix().retag()
}

@extension
class ConstDivideInstance<O> : Divide<ConstPartialOf<O>>, ConstContravariant<O> {
  fun MO(): Monoid<O>
  override fun <A, B, Z> divide(fa: Kind<ConstPartialOf<O>, A>, fb: Kind<ConstPartialOf<O>, B>, f: (Z) -> Tuple2<A, B>): Kind<ConstPartialOf<O>, Z> =
    Const(
      MO().run { fa.value() + fb.value() }
    )
}

@extension
class ConstDivisibleInstance<O> : Divisible<ConstPartialOf<O>>, ConstDivideInstance<O> {
  fun MOO(): Monoid<O>
  override fun MO(): Monoid<O> = MOO()

  override fun <A> conquer(): Kind<ConstPartialOf<O>, A> =
    Const(MOO().empty())
}

@extension
class ConstFunctor<A> : Functor<ConstPartialOf<A>> {
  override fun <T, U> ConstOf<A, T>.map(f: (T) -> U): Const<A, U> =
    fix().retag()
}

// metadebug

@extension
class ConstApply<A> : Apply<ConstPartialOf<A>> {

  fun MA(): Monoid<A>

  override fun <T, U> ConstOf<A, T>.map(f: (T) -> U): Const<A, U> = fix().retag()

  override fun <T, U> ConstOf<A, T>.ap(ff: ConstOf<A, (T) -> U>): Const<A, U> =
    constAp(MA(), ff)
}

@extension
class ConstApplicative<A> : Applicative<ConstPartialOf<A>> {

  fun MA(): Monoid<A>

  override fun <T, U> ConstOf<A, T>.map(f: (T) -> U): Const<A, U> = fix().retag()

  override fun <T> just(a: T): Const<A, T> = object : ConstMonoid<A, T> {
    override fun SA(): Semigroup<A> = MA()
    override fun MA(): Monoid<A> = this@ConstApplicative.MA()
  }.empty().fix()

  override fun <T, U> ConstOf<A, T>.ap(ff: ConstOf<A, (T) -> U>): Const<A, U> =
    constAp(MA(), ff)
}

@extension
class ConstFoldable<A> : Foldable<ConstPartialOf<A>> {

  override fun <T, U> ConstOf<A, T>.foldLeft(b: U, f: (U, T) -> U): U = b

  override fun <T, U> ConstOf<A, T>.foldRight(lb: Eval<U>, f: (T, Eval<U>) -> Eval<U>): Eval<U> = lb
}

@extension
class ConstTraverse<X> : Traverse<ConstPartialOf<X>>, ConstFoldable<X> {

  override fun <T, U> ConstOf<X, T>.map(f: (T) -> U): Const<X, U> = fix().retag()

  override fun <G, A, B> ConstOf<X, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, ConstOf<X, B>> =
    fix().traverse(AP, f)
}

@extension
class ConstTraverseFilter<X> : TraverseFilter<ConstPartialOf<X>>, ConstTraverse<X> {

  override fun <T, U> Kind<ConstPartialOf<X>, T>.map(f: (T) -> U): Const<X, U> = fix().retag()

  override fun <G, A, B> Kind<ConstPartialOf<X>, A>.traverseFilter(AP: Applicative<G>, f: (A) -> Kind<G, Option<B>>): Kind<G, ConstOf<X, B>> =
    fix().traverseFilter(AP, f)
}

@extension
class ConstSemigroup<A, T> : Semigroup<ConstOf<A, T>> {

  fun SA(): Semigroup<A>

  override fun ConstOf<A, T>.combine(b: ConstOf<A, T>): Const<A, T> =
    combineAp(SA(), b)
}

@extension
class ConstMonoid<A, T> : Monoid<ConstOf<A, T>>, ConstSemigroup<A, T> {

  fun MA(): Monoid<A>

  override fun SA(): Semigroup<A> = MA()

  override fun empty(): Const<A, T> = Const(MA().empty())
}

@extension
class ConstEq<A, T> : Eq<Const<A, T>> {

  fun EQ(): Eq<A>

  override fun Const<A, T>.eqv(b: Const<A, T>): Boolean =
    EQ().run { value().eqv(b.value()) }
}

@extension
class ConstShow<A, T> : Show<Const<A, T>> {
  override fun Const<A, T>.showed(): String =
    toString()
}

@extension
class ConstHash<A, T> : Hash<Const<A, T>>, ConstEq<A, T> {
  fun HA(): Hash<A>

  override fun EQ(): Eq<A> = HA()

  override fun Const<A, T>.hashed(): Int = HA().run { value().hashed() }
}
