package arrow.validation.refinedTypes.bool

import arrow.Kind
import arrow.validation.Refinement

interface Not<F, B> : Refinement<F, B> {

  fun REF(): Refinement<F, B>

  override fun B.refinement(): Boolean = REF().run { !refinement() }

  fun B.not(): Kind<F, B> = refine(this)

  fun <A> B.not(f: (B) -> A): Kind<F, A> = refine(this, f)
}
