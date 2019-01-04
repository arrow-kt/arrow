package arrow.data.extensions

import arrow.Kind
import arrow.core.Eval
import arrow.data.*

import arrow.extension
import arrow.typeclasses.*
import arrow.undocumented
import arrow.data.traverse as validatedTraverse

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
