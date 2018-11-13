package arrow.validation.refinedTypes.bool

import arrow.validation.Refinement

interface Not<F, B, A : Refinement<F, B>> : Refinement<F, B> {

  fun REF(): Refinement<F, B>

  override fun B.refinement(): Boolean = REF().run { !refinement() }

}