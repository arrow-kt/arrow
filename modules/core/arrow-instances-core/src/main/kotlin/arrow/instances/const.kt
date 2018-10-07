package arrow.instances

import arrow.Kind
import arrow.core.Eval
import arrow.instance
import arrow.typeclasses.*
import arrow.typeclasses.ap as constAp
import arrow.typeclasses.combine as combineAp

@instance(Const::class)
interface ConstInvariant<A> : Invariant<ConstPartialOf<A>> {
  override fun <T, U> Kind<ConstPartialOf<A>, T>.imap(f: (T) -> U, g: (U) -> T): Const<A, U> =
      fix().retag()
}

@instance(Const::class)
interface ConstContravariant<A> : Contravariant<ConstPartialOf<A>> {
  override fun <T, U> Kind<ConstPartialOf<A>, T>.contramap(f: (U) -> T): Const<A, U> =
      fix().retag()
}

@instance(Const::class)
interface ConstFunctorInstance<A> : Functor<ConstPartialOf<A>> {
  override fun <T, U> Kind<ConstPartialOf<A>, T>.map(f: (T) -> U): Const<A, U> =
    fix().retag()
}

@instance(Const::class)
interface ConstApplicativeInstance<A> : Applicative<ConstPartialOf<A>> {

  fun MA(): Monoid<A>

  override fun <T, U> Kind<ConstPartialOf<A>, T>.map(f: (T) -> U): Const<A, U> = fix().retag()

  override fun <T> just(a: T): Const<A, T> = object : ConstMonoidInstance<A, T> {
    override fun SA(): Monoid<A> = MA()
  }.empty().fix()

  override fun <T, U> Kind<ConstPartialOf<A>, T>.ap(ff: Kind<ConstPartialOf<A>, (T) -> U>): Const<A, U> =
    constAp(MA(), ff)
}

@instance(Const::class)
interface ConstFoldableInstance<A> : Foldable<ConstPartialOf<A>> {

  override fun <T, U> Kind<ConstPartialOf<A>, T>.foldLeft(b: U, f: (U, T) -> U): U = b

  override fun <T, U> Kind<ConstPartialOf<A>, T>.foldRight(lb: Eval<U>, f: (T, Eval<U>) -> Eval<U>): Eval<U> = lb

}

@instance(Const::class)
interface ConstTraverseInstance<X> : ConstFoldableInstance<X>, Traverse<ConstPartialOf<X>> {

  override fun <T, U> Kind<ConstPartialOf<X>, T>.map(f: (T) -> U): Const<X, U> = fix().retag()

  override fun <G, A, B> ConstOf<X, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, ConstOf<X, B>> =
    fix().traverse(AP, f)
}

@instance(Const::class)
interface ConstSemigroupInstance<A, T> : Semigroup<ConstOf<A, T>> {

  fun SA(): Semigroup<A>

  override fun ConstOf<A, T>.combine(b: ConstOf<A, T>): Const<A, T> =
    combineAp(SA(), b)
}

@instance(Const::class)
interface ConstMonoidInstance<A, T> : ConstSemigroupInstance<A, T>, Monoid<ConstOf<A, T>> {

  override fun SA(): Monoid<A>

  override fun empty(): Const<A, T> = Const(SA().empty())

}

@instance(Const::class)
interface ConstEqInstance<A, T> : Eq<Const<A, T>> {

  fun EQ(): Eq<A>

  override fun Const<A, T>.eqv(b: Const<A, T>): Boolean =
    EQ().run { value.eqv(b.value) }
}

@instance(Const::class)
interface ConstShowInstance<A, T> : Show<Const<A, T>> {
  override fun Const<A, T>.show(): String =
    toString()
}

class ConstContext<A>(val MA: Monoid<A>) : ConstApplicativeInstance<A>, ConstTraverseInstance<A> {
  override fun MA(): Monoid<A> = MA

  override fun <T, U> Kind<ConstPartialOf<A>, T>.map(f: (T) -> U): Const<A, U> =
    fix().map(f)
}

class ConstContextPartiallyApplied<L>(val MA: Monoid<L>) {
  infix fun <A> extensions(f: ConstContext<L>.() -> A): A =
    f(ConstContext(MA))
}

fun <L> ForConst(MA: Monoid<L>): ConstContextPartiallyApplied<L> =
  ConstContextPartiallyApplied(MA)