package arrow.core.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.ForValidated
import arrow.core.Invalid
import arrow.core.Valid
import arrow.core.Validated
import arrow.core.ValidatedOf
import arrow.core.ValidatedPartialOf
import arrow.core.ap
import arrow.core.combineK
import arrow.core.extensions.validated.eq.eq
import arrow.core.fix
import arrow.core.handleLeftWith
import arrow.extension
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Bifoldable
import arrow.typeclasses.Bitraverse
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.Hash
import arrow.typeclasses.Selective
import arrow.typeclasses.Semigroup
import arrow.typeclasses.SemigroupK
import arrow.typeclasses.Show
import arrow.typeclasses.Traverse
import arrow.undocumented
import arrow.core.traverse as validatedTraverse

@extension
@undocumented
interface ValidatedFunctor<E> : Functor<ValidatedPartialOf<E>> {
  override fun <A, B> Kind<ValidatedPartialOf<E>, A>.map(f: (A) -> B): Validated<E, B> = fix().map(f)
}

@extension
interface ValidatedApplicative<E> : Applicative<ValidatedPartialOf<E>>, ValidatedFunctor<E> {

  fun SE(): Semigroup<E>

  override fun <A> just(a: A): Validated<E, A> = Valid(a)

  override fun <A, B> Kind<ValidatedPartialOf<E>, A>.map(f: (A) -> B): Validated<E, B> = fix().map(f)

  override fun <A, B> Kind<ValidatedPartialOf<E>, A>.ap(ff: Kind<ValidatedPartialOf<E>, (A) -> B>): Validated<E, B> = fix().ap(SE(), ff.fix())
}

@extension
interface ValidatedSelective<E> : Selective<ValidatedPartialOf<E>>, ValidatedApplicative<E> {

  override fun SE(): Semigroup<E>

  override fun <A, B> Kind<ValidatedPartialOf<E>, Either<A, B>>.select(f: Kind<ValidatedPartialOf<E>, (A) -> B>): Kind<ValidatedPartialOf<E>, B> =
    fix().fold({ Invalid(it) }, { it.fold({ l -> f.map { ff -> ff(l) } }, { r -> just(r) }) })
}

@extension
interface ValidatedApplicativeError<E> : ApplicativeError<ValidatedPartialOf<E>, E>, ValidatedApplicative<E> {

  override fun SE(): Semigroup<E>

  override fun <A> raiseError(e: E): Validated<E, A> = Invalid(e)

  override fun <A> Kind<ValidatedPartialOf<E>, A>.handleErrorWith(f: (E) -> Kind<ValidatedPartialOf<E>, A>): Validated<E, A> =
    fix().handleLeftWith(f)
}

@extension
interface ValidatedFoldable<E> : Foldable<ValidatedPartialOf<E>> {

  override fun <A, B> Kind<ValidatedPartialOf<E>, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> Kind<ValidatedPartialOf<E>, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)
}

@extension
interface ValidatedTraverse<E> : Traverse<ValidatedPartialOf<E>>, ValidatedFoldable<E> {

  override fun <G, A, B> Kind<ValidatedPartialOf<E>, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Validated<E, B>> =
    fix().validatedTraverse(AP, f)
}

@extension
interface ValidatedBifoldable : Bifoldable<ForValidated> {
  override fun <A, B, C> ValidatedOf<A, B>.bifoldLeft(c: C, f: (C, A) -> C, g: (C, B) -> C): C =
    fix().fold({ f(c, it) }, { g(c, it) })

  override fun <A, B, C> ValidatedOf<A, B>.bifoldRight(c: Eval<C>, f: (A, Eval<C>) -> Eval<C>, g: (B, Eval<C>) -> Eval<C>): Eval<C> =
    fix().fold({ f(it, c) }, { g(it, c) })
}

@extension
interface ValidatedBitraverse : Bitraverse<ForValidated>, ValidatedBifoldable {
  override fun <G, A, B, C, D> ValidatedOf<A, B>.bitraverse(AP: Applicative<G>, f: (A) -> Kind<G, C>, g: (B) -> Kind<G, D>): Kind<G, ValidatedOf<C, D>> =
    fix().let {
      AP.run {
        it.fold({ f(it).map { Invalid(it) } },
          { g(it).map { Valid(it) } })
      }
    }
}

@extension
interface ValidatedSemigroupK<E> : SemigroupK<ValidatedPartialOf<E>> {

  fun SE(): Semigroup<E>

  override fun <B> Kind<ValidatedPartialOf<E>, B>.combineK(y: Kind<ValidatedPartialOf<E>, B>): Validated<E, B> =
    fix().combineK(SE(), y)
}

@extension
interface ValidatedEq<L, R> : Eq<Validated<L, R>> {

  fun EQL(): Eq<L>

  fun EQR(): Eq<R>

  override fun Validated<L, R>.eqv(b: Validated<L, R>): Boolean = when (this) {
    is Valid -> when (b) {
      is Invalid -> false
      is Valid -> EQR().run { a.eqv(b.a) }
    }
    is Invalid -> when (b) {
      is Invalid -> EQL().run { e.eqv(b.e) }
      is Valid -> false
    }
  }
}

@extension
interface ValidatedEqK<L> : EqK<ValidatedPartialOf<L>> {
  fun EQL(): Eq<L>

  override fun <R> Kind<ValidatedPartialOf<L>, R>.eqK(other: Kind<ValidatedPartialOf<L>, R>, EQ: Eq<R>): Boolean =
    Validated.eq(EQL(), EQ).run {
      this@eqK.fix().eqv(other.fix())
    }
}

@extension
interface ValidatedShow<L, R> : Show<Validated<L, R>> {
  override fun Validated<L, R>.show(): String =
    toString()
}

@extension
interface ValidatedHash<L, R> : Hash<Validated<L, R>>, ValidatedEq<L, R> {
  fun HL(): Hash<L>
  fun HR(): Hash<R>

  override fun EQL(): Eq<L> = HL()
  override fun EQR(): Eq<R> = HR()

  override fun Validated<L, R>.hash(): Int = fold({
    HL().run { it.hash() }
  }, {
    HR().run { it.hash() }
  })
}
