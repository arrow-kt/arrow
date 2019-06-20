package arrow.sample

import arrow.sample.ForOption //works because in the same module

interface Kind<out F, out A>

/**
package arrow.core

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

* class ForOption private constructor() { companion object }
typealias OptionOf<A> = arrow.Kind<ForOption, A>

@ExperimentalContracts
@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A> OptionOf<A>.fix(): Option<A> {
contract {
returns() implies (this@fix is Option<A>)
}
return this as Option<A>
}

 */

sealed class Option<out A>
object None : Option<Nothing>()
data class Some<out A>(val a: A) : Option<A>()

fun x() : OptionOf<Int> = TODO()