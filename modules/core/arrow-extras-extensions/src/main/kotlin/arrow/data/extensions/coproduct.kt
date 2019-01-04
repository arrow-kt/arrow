package arrow.data.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.data.Coproduct
import arrow.data.CoproductOf
import arrow.data.CoproductPartialOf
import arrow.data.fix
import arrow.extension
import arrow.core.extensions.either.eq.eq
import arrow.core.extensions.either.hash.hash
import arrow.typeclasses.*
import arrow.undocumented

@extension
@undocumented
interface CoproductFunctor<F, G> : Functor<CoproductPartialOf<F, G>> {

  fun FF(): Functor<F>

  fun FG(): Functor<G>

  override fun <A, B> Kind<CoproductPartialOf<F, G>, A>.map(f: (A) -> B): Coproduct<F, G, B> = fix().map(FF(), FG(), f)
}

@extension
@undocumented
interface CoproductContravariant<F, G> : Contravariant<CoproductPartialOf<F, G>> {

  fun CF(): Contravariant<F>

  fun CG(): Contravariant<G>

  override fun <A, B> Kind<CoproductPartialOf<F, G>, A>.contramap(f: (B) -> A): Coproduct<F, G, B> =
    fix().contramap(CF(), CG(), f)
}

@extension
@undocumented
interface CoproductComonad<F, G> : Comonad<CoproductPartialOf<F, G>> {

  fun CF(): Comonad<F>

  fun CG(): Comonad<G>

  override fun <A, B> Kind<CoproductPartialOf<F, G>, A>.coflatMap(f: (Kind<CoproductPartialOf<F, G>, A>) -> B): Coproduct<F, G, B> = fix().coflatMap(CF(), CG(), f)

  override fun <A> Kind<CoproductPartialOf<F, G>, A>.extract(): A = fix().extract(CF(), CG())

  override fun <A, B> Kind<CoproductPartialOf<F, G>, A>.map(f: (A) -> B): Coproduct<F, G, B> = fix().map(CF(), CG(), f)

}

@extension
@undocumented
interface CoproductFoldable<F, G> : Foldable<CoproductPartialOf<F, G>> {

  fun FF(): Foldable<F>

  fun FG(): Foldable<G>

  override fun <A, B> Kind<CoproductPartialOf<F, G>, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f, FF(), FG())

  override fun <A, B> Kind<CoproductPartialOf<F, G>, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f, FF(), FG())

}

@extension
@undocumented
interface CoproductTraverse<F, G> : Traverse<CoproductPartialOf<F, G>> {

  fun TF(): Traverse<F>

  fun TG(): Traverse<G>

  override fun <H, A, B> CoproductOf<F, G, A>.traverse(AP: Applicative<H>, f: (A) -> Kind<H, B>): Kind<H, Coproduct<F, G, B>> =
    fix().traverse(AP, TF(), TG(), f)

  override fun <A, B> Kind<CoproductPartialOf<F, G>, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f, TF(), TG())

  override fun <A, B> Kind<CoproductPartialOf<F, G>, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f, TF(), TG())
}

@extension
@undocumented
interface CoproductEq<F, G, A> : Eq<Coproduct<F, G, A>> {
  fun EQF(): Eq<Kind<F, A>>
  fun EQG(): Eq<Kind<G, A>>

  override fun Coproduct<F, G, A>.eqv(b: Coproduct<F, G, A>): Boolean = Either.eq(EQF(), EQG()).run { run.eqv(b.run) }
}

@extension
@undocumented
interface CoproductHash<F, G, A> : Hash<Coproduct<F, G, A>>, CoproductEq<F, G, A> {
  fun HF(): Hash<Kind<F, A>>
  fun HG(): Hash<Kind<G, A>>

  override fun EQF(): Eq<Kind<F, A>> = HF()
  override fun EQG(): Eq<Kind<G, A>> = HG()

  override fun Coproduct<F, G, A>.hash(): Int = Either.hash(HF(), HG()).run { run.hash() }
}
