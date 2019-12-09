package arrow.recursion.extensions

import arrow.Kind
import arrow.core.ForOption
import arrow.core.Option
import arrow.core.extensions.option.functor.functor
import arrow.core.fix
import arrow.core.none
import arrow.core.some
import arrow.recursion.typeclasses.Birecursive
import arrow.recursion.typeclasses.Corecursive
import arrow.recursion.typeclasses.Recursive
import arrow.typeclasses.Functor

interface IntBirecursive : Birecursive<Int, ForOption> {
  override fun FF(): Functor<ForOption> = Option.functor()

  override fun Kind<ForOption, Int>.embedT(): Int = fix().fold({ 0 }, { it + 1 })

  override fun Int.projectT(): Kind<ForOption, Int> = when {
    this < 0 -> throw IllegalArgumentException("IntBirecursive only works on natural numbers")
    this == 0 -> none()
    else -> (this - 1).some()
  }
}

fun Int.Companion.birecursive(): Birecursive<Int, ForOption> = object : IntBirecursive {}

fun Int.Companion.recursive(): Recursive<Int, ForOption> = object : IntBirecursive {}

fun Int.Companion.corecursive(): Corecursive<Int, ForOption> = object : IntBirecursive {}

interface LongBirecursive : Birecursive<Long, ForOption> {
  override fun FF(): Functor<ForOption> = Option.functor()

  override fun Kind<ForOption, Long>.embedT(): Long = fix().fold({ 0L }, { it + 1 })

  override fun Long.projectT(): Kind<ForOption, Long> = when {
    this < 0 -> throw IllegalArgumentException("LongBirecursive only works on natural numbers")
    this == 0L -> none()
    else -> (this - 1).some()
  }
}

fun Long.Companion.birecursive(): Birecursive<Long, ForOption> = object : LongBirecursive {}

fun Long.Companion.recursive(): Recursive<Long, ForOption> = object : LongBirecursive {}

fun Long.Companion.corecursive(): Corecursive<Long, ForOption> = object : LongBirecursive {}
