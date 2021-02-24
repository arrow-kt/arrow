package arrow.core.extensions.option.monoid

import arrow.core.Option
import arrow.core.Option.Companion
import arrow.core.extensions.OptionMonoid
import arrow.typeclasses.Semigroup

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "combineAll(SG)",
    "arrow.core.combineAll"
  ),
  DeprecationLevel.WARNING
)
fun <A> Collection<Option<A>>.combineAll(SG: Semigroup<A>): Option<A> =
  arrow.core.Option.monoid<A>(SG).run {
    this@combineAll.combineAll() as arrow.core.Option<A>
  }

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "arg0.combineAll(SG)",
    "arrow.core.combineAll"
  ),
  DeprecationLevel.WARNING
)
fun <A> combineAll(SG: Semigroup<A>, arg0: List<Option<A>>): Option<A> = arrow.core.Option
  .monoid<A>(SG)
  .combineAll(arg0) as arrow.core.Option<A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "@extension projected functions are deprecated",
  ReplaceWith(
    "Monoid.option<A>(SG)",
    "arrow.core.option",
    "arrow.typeclasses.Monoid"
  ),
  DeprecationLevel.WARNING
)
inline fun <A> Companion.monoid(SG: Semigroup<A>): OptionMonoid<A> = object :
  arrow.core.extensions.OptionMonoid<A> { override fun SG(): arrow.typeclasses.Semigroup<A> = SG }
