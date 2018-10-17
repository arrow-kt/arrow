package arrow.typeclasses

import arrow.Kind
import arrow.Kind2

/**
 * A type to represent λ[α => Kind[F, α, C]]
 *
 * Use counnest to expand it, conest to re-compose it
 */
interface Conested<out F, out B>

typealias ConestedType<F, A, B> = Kind<Conested<F, B>, A>

typealias CounnestedType<F, A, B> = Kind<Kind<F, A>, B>

@Suppress("UNCHECKED_CAST")
fun <F, A, B> CounnestedType<F, A, B>.conest(): ConestedType<F, A, B> = this as ConestedType<F, A, B>

@Suppress("UNCHECKED_CAST")
fun <F, A, B> ConestedType<F, A, B>.counnest(): CounnestedType<F, A, B> = this as CounnestedType<F, A, B>

interface CocomposedFunctor<F, X> : Functor<Conested<F, X>> {
  fun F(): Bifunctor<F>

  override fun <A, B> Kind<Conested<F, X>, A>.map(f: (A) -> B): Kind<Conested<F, X>, B> =
    F().run { counnest().mapLeft(f) }.conest()

  fun <A, B> CounnestedType<F, A, X>.mapC(f: (A) -> B): Kind2<F, B, X> =
    F().run { mapLeft(f) }

  companion object {
    operator fun <F, X> invoke(BF: Bifunctor<F>): Functor<Conested<F, X>> =
      object : CocomposedFunctor<F, X> {
        override fun F(): Bifunctor<F> = BF
      }
  }
}
