package arrow.core.extensions.either.semigroup

import arrow.core.Either
import arrow.core.Either.Companion
import arrow.core.extensions.EitherSemigroup
import arrow.typeclasses.Semigroup
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("plus")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("combine(SGL, SGR, arg1)", "arrow.core.combine"))
fun <L, R> Either<L, R>.plus(
  SGL: Semigroup<L>,
  SGR: Semigroup<R>,
  arg1: Either<L, R>
): Either<L, R> = arrow.core.Either.semigroup<L, R>(SGL, SGR).run {
  this@plus.plus(arg1) as arrow.core.Either<L, R>
}

@JvmName("maybeCombine")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("maybeCombine(SGL, SGR, arg1)", "arrow.core.combine"))
fun <L, R> Either<L, R>.maybeCombine(
  SGL: Semigroup<L>,
  SGR: Semigroup<R>,
  arg1: Either<L, R>
): Either<L, R> = arrow.core.Either.semigroup<L, R>(SGL, SGR).run {
  this@maybeCombine.maybeCombine(arg1) as arrow.core.Either<L, R>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Semigroup.either(SGL, SGR)", "arrow.core.Semigroup", "arrow.core.either"))
inline fun <L, R> Companion.semigroup(SGL: Semigroup<L>, SGR: Semigroup<R>): EitherSemigroup<L, R> =
  object : arrow.core.extensions.EitherSemigroup<L, R> {
    override fun SGL():
      arrow.typeclasses.Semigroup<L> = SGL

    override fun SGR(): arrow.typeclasses.Semigroup<R> = SGR
  }
