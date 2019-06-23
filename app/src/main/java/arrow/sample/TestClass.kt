package arrow.sample

import arrow.sample.ForOption //works because in the same module
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

interface Kind<out F, out A>

/**
package arrow.core

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

* class ForOption private constructor() { companion object }
typealias OptionOf<A> = arrow.Kind<ForOption, A>

 */

sealed class Option<out A> {
  fun h(): Option<Int> {
    val x : OptionOf<Int> = None
    val y = x
    return y
  }
}

object None : Option<Nothing>()

data class Some<out A>(val a: A) : Option<A>()