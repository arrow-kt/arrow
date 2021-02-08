package arrow.core

import arrow.typeclasses.Hash

private object CharHash : Hash<Char> {
  override fun Char.hash(): Int = this.hashCode()
}

fun Hash.Companion.char(): Hash<Char> =
  CharHash
