package arrow.recursion.extensions

import arrow.Kind
import arrow.core.*
import arrow.core.extensions.eval.monad.binding
import arrow.core.extensions.option.functor.functor
import arrow.recursion.typeclasses.Birecursive
import arrow.typeclasses.Functor

interface IntBirecursive : Birecursive<Int, ForOption> {
  override fun FF(): Functor<ForOption> = Option.functor()

  override fun Kind<ForOption, Eval<Int>>.embedT(): Eval<Int> = binding {
    fix().fold({ 0 }, { it.bind() + 1 })
  }

  override fun Int.projectT(): Kind<ForOption, Int> = when {
    this < 0 -> throw IllegalArgumentException("IntBirecursive only works on natural numbers")
    this == 0 -> none()
    else -> (this - 1).some()
  }
}

fun Int.Companion.birecursive(): Birecursive<Int, ForOption> = object : IntBirecursive {}

interface LongBirecursive : Birecursive<Long, ForOption> {
  override fun FF(): Functor<ForOption> = Option.functor()

  override fun Kind<ForOption, Eval<Long>>.embedT(): Eval<Long> = binding {
    fix().fold({ 0L }, { it.bind() + 1L })
  }

  override fun Long.projectT(): Kind<ForOption, Long> = when {
    this < 0 -> throw IllegalArgumentException("IntBirecursive only works on natural numbers")
    this == 0L -> none()
    else -> (this - 1).some()
  }
}

fun Long.Companion.birecursive(): Birecursive<Long, ForOption> = object : LongBirecursive {}
