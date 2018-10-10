package arrow.instances

import arrow.Kind
import arrow.core.Eval
import arrow.data.*
import arrow.instance
import arrow.typeclasses.*
import arrow.data.traverse as validatedTraverse

@instance
interface ValidatedFunctorInstance<E> : Functor<ValidatedPartialOf<E>> {
  override fun <A, B> Kind<ValidatedPartialOf<E>, A>.map(f: (A) -> B): Validated<E, B> = fix().map(f)
}

@instance
interface ValidatedApplicativeInstance<E> : Applicative<ValidatedPartialOf<E>>, ValidatedFunctorInstance<E> {

  fun SE(): Semigroup<E>

  override fun <A> just(a: A): Validated<E, A> = Valid(a)

  override fun <A, B> Kind<ValidatedPartialOf<E>, A>.map(f: (A) -> B): Validated<E, B> = fix().map(f)

  override fun <A, B> Kind<ValidatedPartialOf<E>, A>.ap(ff: Kind<ValidatedPartialOf<E>, (A) -> B>): Validated<E, B> = fix().ap(SE(), ff.fix())

}

@instance
interface ValidatedApplicativeErrorInstance<E> : ApplicativeError<ValidatedPartialOf<E>, E>, ValidatedApplicativeInstance<E> {

  override fun SE(): Semigroup<E>

  override fun <A> raiseError(e: E): Validated<E, A> = Invalid(e)

  override fun <A> Kind<ValidatedPartialOf<E>, A>.handleErrorWith(f: (E) -> Kind<ValidatedPartialOf<E>, A>): Validated<E, A> =
    fix().handleLeftWith(f)

}

@instance
interface ValidatedFoldableInstance<E> : Foldable<ValidatedPartialOf<E>> {

  override fun <A, B> Kind<ValidatedPartialOf<E>, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> Kind<ValidatedPartialOf<E>, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)
}

@instance
interface ValidatedTraverseInstance<E> : Traverse<ValidatedPartialOf<E>>, ValidatedFoldableInstance<E> {

  override fun <G, A, B> Kind<ValidatedPartialOf<E>, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Validated<E, B>> =
    fix().validatedTraverse(AP, f)
}

@instance
interface ValidatedSemigroupKInstance<E> : SemigroupK<ValidatedPartialOf<E>> {

  fun SE(): Semigroup<E>

  override fun <B> Kind<ValidatedPartialOf<E>, B>.combineK(y: Kind<ValidatedPartialOf<E>, B>): Validated<E, B> =
    fix().combineK(SE(), y)
}

@instance
interface ValidatedEqInstance<L, R> : Eq<Validated<L, R>> {

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

@instance
interface ValidatedShowInstance<L, R> : Show<Validated<L, R>> {
  override fun Validated<L, R>.show(): String =
    toString()
}

class ValidatedContext<L>(val SL: Semigroup<L>) : ValidatedApplicativeErrorInstance<L>, ValidatedTraverseInstance<L>, ValidatedSemigroupKInstance<L> {
  override fun SE(): Semigroup<L> = SL

  override fun <A, B> Kind<ValidatedPartialOf<L>, A>.map(f: (A) -> B): Validated<L, B> =
    fix().map(f)
}

class ValidatedContextPartiallyApplied<L>(val SL: Semigroup<L>) {
  infix fun <A> extensions(f: ValidatedContext<L>.() -> A): A =
    f(ValidatedContext(SL))
}

fun <L> ForValidated(SL: Semigroup<L>): ValidatedContextPartiallyApplied<L> =
  ValidatedContextPartiallyApplied(SL)