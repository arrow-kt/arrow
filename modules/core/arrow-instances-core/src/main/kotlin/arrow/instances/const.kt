package arrow.instances

import arrow.Kind
import arrow.core.Eval
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.extension
import arrow.typeclasses.*
import arrow.typeclasses.ap as constAp
import arrow.typeclasses.combine as combineAp

@extension
interface ConstInvariant<A> : Invariant<ConstPartialOf<A>> {
  override fun <T, U> ConstOf<A, T>.imap(f: (T) -> U, g: (U) -> T): Const<A, U> =
    fix().retag()
}

@extension
interface ConstContravariant<A> : Contravariant<ConstPartialOf<A>> {
  override fun <T, U> ConstOf<A, T>.contramap(f: (U) -> T): Const<A, U> =
    fix().retag()
}

@extension
interface ConstFunctorInstance<A> : Functor<ConstPartialOf<A>> {
  override fun <T, U> ConstOf<A, T>.map(f: (T) -> U): Const<A, U> =
    fix().retag()
}

@extension
interface ConstApplicativeInstance<A> : Applicative<ConstPartialOf<A>> {

  fun MA(): Monoid<A>

  override fun <T, U> ConstOf<A, T>.map(f: (T) -> U): Const<A, U> = fix().retag()

  override fun <T> just(a: T): Const<A, T> = object : ConstMonoidInstance<A, T> {
    override fun SA(): Semigroup<A> = MA()
    override fun MA(): Monoid<A> = this@ConstApplicativeInstance.MA()
  }.empty().fix()

  override fun <T, U> ConstOf<A, T>.ap(ff: ConstOf<A, (T) -> U>): Const<A, U> =
    constAp(MA(), ff)
}

@extension
interface ConstFoldableInstance<A> : Foldable<ConstPartialOf<A>> {

  override fun <T, U> ConstOf<A, T>.foldLeft(b: U, f: (U, T) -> U): U = b

  override fun <T, U> ConstOf<A, T>.foldRight(lb: Eval<U>, f: (T, Eval<U>) -> Eval<U>): Eval<U> = lb

}

@extension
interface ConstTraverseInstance<X> : Traverse<ConstPartialOf<X>>, ConstFoldableInstance<X> {

  override fun <T, U> Kind<ConstPartialOf<X>, T>.map(f: (T) -> U): Const<X, U> = fix().retag()

  override fun <G, A, B> ConstOf<X, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, ConstOf<X, B>> =
    fix().traverse(AP, f)
}

@extension
interface ConstSemigroupInstance<A, T> : Semigroup<ConstOf<A, T>> {

  fun SA(): Semigroup<A>

  override fun ConstOf<A, T>.combine(b: ConstOf<A, T>): Const<A, T> =
    combineAp(SA(), b)
}

@extension
interface ConstMonoidInstance<A, T> : Monoid<ConstOf<A, T>>, ConstSemigroupInstance<A, T> {

  fun MA(): Monoid<A>

  override fun SA(): Semigroup<A> = MA()

  override fun empty(): Const<A, T> = Const(MA().empty())

}

@extension
interface ConstEqInstance<A, T> : Eq<Const<A, T>> {

  fun EQ(): Eq<A>

  override fun Const<A, T>.eqv(b: Const<A, T>): Boolean =
    EQ().run { value().eqv(b.value()) }
}

@extension
interface ConstShowInstance<A, T> : Show<Const<A, T>> {
  override fun Const<A, T>.show(): String =
    toString()
}

@extension
interface ConstHashInstance<A, T> : Hash<Const<A, T>>, ConstEqInstance<A, T> {
  fun HA(): Hash<A>

  override fun EQ(): Eq<A> = HA()

  override fun Const<A, T>.hash(): Int = HA().run { value().hash() }
}

class ConstContext<A>(val MA: Monoid<A>) : ConstApplicativeInstance<A>, ConstTraverseInstance<A> {
  override fun MA(): Monoid<A> = MA

  override fun <T, U> ConstOf<A, T>.map(f: (T) -> U): Const<A, U> =
    fix().map(f)
}

class ConstContextPartiallyApplied<L>(val MA: Monoid<L>) {
  infix fun <A> extensions(f: ConstContext<L>.() -> A): A =
    f(ConstContext(MA))
}

@Deprecated(ExtensionsDSLDeprecated)
fun <L> ForConst(MA: Monoid<L>): ConstContextPartiallyApplied<L> =
  ConstContextPartiallyApplied(MA)