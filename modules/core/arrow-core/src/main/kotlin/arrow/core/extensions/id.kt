@file:Suppress("UnusedImports")

package arrow.core.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.ForId
import arrow.core.Id
import arrow.core.IdOf
import arrow.core.extensions.id.monad.monad
import arrow.core.fix
import arrow.core.identity
import arrow.core.value
import arrow.extension
import arrow.typeclasses.Applicative
import arrow.typeclasses.Apply
import arrow.typeclasses.Bimonad
import arrow.typeclasses.Comonad
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.Hash
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadFx
import arrow.typeclasses.MonadSyntax
import arrow.typeclasses.Monoid
import arrow.typeclasses.Selective
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.Traverse
import arrow.typeclasses.Semialign
import arrow.core.extensions.traverse as idTraverse
import arrow.core.select as idSelect
import arrow.core.Ior

@extension
interface IdSemigroup<A> : Semigroup<Id<A>> {
  fun SA(): Semigroup<A>

  override fun Id<A>.combine(b: Id<A>): Id<A> = Id(SA().run { value().combine(b.value()) })
}

@extension
interface IdMonoid<A> : Monoid<Id<A>>, IdSemigroup<A> {
  fun MA(): Monoid<A>
  override fun SA(): Semigroup<A> = MA()

  override fun empty(): Id<A> = Id(MA().empty())
}

@extension
interface IdEq<A> : Eq<Id<A>> {

  fun EQ(): Eq<A>

  override fun Id<A>.eqv(b: Id<A>): Boolean =
    EQ().run { value().eqv(b.value()) }
}

@extension
interface IdShow<A> : Show<Id<A>> {
  override fun Id<A>.show(): String =
    toString()
}

@extension
interface IdFunctor : Functor<ForId> {
  override fun <A, B> IdOf<A>.map(f: (A) -> B): Id<B> =
    fix().map(f)
}

@extension
interface IdApply : Apply<ForId> {
  override fun <A, B> IdOf<A>.ap(ff: IdOf<(A) -> B>): Id<B> =
    fix().ap(ff)

  override fun <A, B> IdOf<A>.map(f: (A) -> B): Id<B> =
    fix().map(f)
}

@extension
interface IdApplicative : Applicative<ForId> {
  override fun <A, B> IdOf<A>.ap(ff: IdOf<(A) -> B>): Id<B> =
    fix().ap(ff)

  override fun <A, B> IdOf<A>.map(f: (A) -> B): Id<B> =
    fix().map(f)

  override fun <A> just(a: A): Id<A> =
    Id.just(a)
}

@extension
interface IdSelective : Selective<ForId>, IdApplicative {
  override fun <A, B> IdOf<Either<A, B>>.select(f: Kind<ForId, (A) -> B>): Kind<ForId, B> =
    fix().idSelect(f)
}

@extension
interface IdMonad : Monad<ForId> {
  override fun <A, B> IdOf<A>.ap(ff: IdOf<(A) -> B>): Id<B> =
    fix().ap(ff)

  override fun <A, B> IdOf<A>.flatMap(f: (A) -> IdOf<B>): Id<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> IdOf<Either<A, B>>): Id<B> =
    Id.tailRecM(a, f)

  override fun <A, B> IdOf<A>.map(f: (A) -> B): Id<B> =
    fix().map(f)

  override fun <A> just(a: A): Id<A> =
    Id.just(a)

  override fun <A, B> IdOf<Either<A, B>>.select(f: IdOf<(A) -> B>): Kind<ForId, B> =
    fix().idSelect(f)

  override val fx: MonadFx<ForId>
    get() = IdFxMonad
}

internal object IdFxMonad : MonadFx<ForId> {
  override val M: Monad<ForId> = Id.monad()
  override fun <A> monad(c: suspend MonadSyntax<ForId>.() -> A): Id<A> =
    super.monad(c).fix()
}

@extension
interface IdComonad : Comonad<ForId> {
  override fun <A, B> IdOf<A>.coflatMap(f: (IdOf<A>) -> B): Id<B> =
    fix().coflatMap(f)

  override fun <A> IdOf<A>.extract(): A =
    fix().extract()

  override fun <A, B> IdOf<A>.map(f: (A) -> B): Id<B> =
    fix().map(f)
}

@extension
interface IdBimonad : Bimonad<ForId> {
  override fun <A, B> IdOf<A>.ap(ff: IdOf<(A) -> B>): Id<B> =
    fix().ap(ff)

  override fun <A, B> IdOf<A>.flatMap(f: (A) -> IdOf<B>): Id<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> IdOf<Either<A, B>>): Id<B> =
    Id.tailRecM(a, f)

  override fun <A, B> IdOf<A>.map(f: (A) -> B): Id<B> =
    fix().map(f)

  override fun <A> just(a: A): Id<A> =
    Id.just(a)

  override fun <A, B> IdOf<A>.coflatMap(f: (IdOf<A>) -> B): Id<B> =
    fix().coflatMap(f)

  override fun <A> IdOf<A>.extract(): A =
    fix().extract()
}

@extension
interface IdFoldable : Foldable<ForId> {
  override fun <A, B> IdOf<A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> IdOf<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)
}

fun <A, G, B> IdOf<A>.traverse(GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Id<B>> = GA.run {
  f(value()).map { Id(it) }
}

fun <A, G> IdOf<Kind<G, A>>.sequence(GA: Applicative<G>): Kind<G, Id<A>> =
  idTraverse(GA, ::identity)

@extension
interface IdTraverse : Traverse<ForId> {
  override fun <A, B> IdOf<A>.map(f: (A) -> B): Id<B> =
    fix().map(f)

  override fun <G, A, B> IdOf<A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Id<B>> =
    idTraverse(AP, f)

  override fun <A, B> IdOf<A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> IdOf<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)
}

@extension
interface IdHash<A> : Hash<Id<A>>, IdEq<A> {

  fun HA(): Hash<A>

  override fun EQ(): Eq<A> = HA()

  override fun Id<A>.hash(): Int = HA().run { value().hash() }
}

fun <A> Id.Companion.fx(c: suspend MonadSyntax<ForId>.() -> A): Id<A> =
  Id.monad().fx.monad(c).fix()

@extension
interface IdEqK : EqK<ForId> {
  override fun <A> Kind<ForId, A>.eqK(other: Kind<ForId, A>, EQ: Eq<A>) =
    (this.fix() to other.fix()).let {
      EQ.run { it.first.extract().eqv(it.second.extract()) }
    }
}

@extension
interface IdSemialign : Semialign<ForId>, IdFunctor {
  override fun <A, B, C> alignWith(a: Kind<ForId, A>, b: Kind<ForId, B>, fa: (Ior<A, B>) -> C): Kind<ForId, C> =
    Id.just(fa(Ior.Both(a.fix().extract(), b.fix().extract())))
}
