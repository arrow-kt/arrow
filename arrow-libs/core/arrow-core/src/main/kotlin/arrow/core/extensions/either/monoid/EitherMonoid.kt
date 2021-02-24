package arrow.core.extensions.either.monoid

import arrow.core.Either
import arrow.core.Either.Companion
import arrow.core.extensions.EitherMonoid
import arrow.typeclasses.Monoid
import kotlin.Suppress
import kotlin.collections.Collection
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("combineAll(MOL, MOR)", "arrow.core.combineAll"))
fun <L, R> Collection<Either<L, R>>.combineAll(MOL: Monoid<L>, MOR: Monoid<R>): Either<L, R> =
  arrow.core.Either.monoid<L, R>(MOL, MOR).run {
    this@combineAll.combineAll() as arrow.core.Either<L, R>
  }

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("arg0.combineAll(MOL, MOR)", "arrow.core.combineAll"))
fun <L, R> combineAll(
  MOL: Monoid<L>,
  MOR: Monoid<R>,
  arg0: List<Either<L, R>>
): Either<L, R> = arrow.core.Either
  .monoid<L, R>(MOL, MOR)
  .combineAll(arg0) as arrow.core.Either<L, R>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Monoid.either(MOL, MOR)", "arrow.core.either", "arrow.core.Monoid"))
inline fun <L, R> Companion.monoid(MOL: Monoid<L>, MOR: Monoid<R>): EitherMonoid<L, R> = object :
  arrow.core.extensions.EitherMonoid<L, R> {
  override fun MOL(): arrow.typeclasses.Monoid<L> = MOL

  override fun MOR(): arrow.typeclasses.Monoid<R> = MOR
}
