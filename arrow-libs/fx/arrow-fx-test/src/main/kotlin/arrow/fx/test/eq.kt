package arrow.fx.test

import arrow.fx.IOResult
import arrow.typeclasses.Eq

fun <E, A> IOResult.Companion.eq(EQE: Eq<E>, EQA: Eq<A>, EQT: Eq<Throwable>): Eq<IOResult<E, A>> =
  object : Eq<IOResult<E, A>> {
    override fun IOResult<E, A>.eqv(b: IOResult<E, A>): Boolean =
      when (this) {
        is IOResult.Success -> when (b) {
          is IOResult.Success -> EQA.run { value.eqv(b.value) }
          else -> false
        }
        is IOResult.Error -> when (b) {
          is IOResult.Error -> EQE.run { error.eqv(b.error) }
          else -> false
        }
        is IOResult.Exception -> when (b) {
          is IOResult.Exception -> EQT.run { exception.eqv(b.exception) }
          else -> false
        }
      }
  }
