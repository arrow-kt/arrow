package arrow.sample

import arrow.sample.ForOption //works because in the same module

interface Kind<out F, out A>

sealed class Option<out A>
object None : Option<Nothing>()
data class Some<out A>(val a: A) : Option<A>()