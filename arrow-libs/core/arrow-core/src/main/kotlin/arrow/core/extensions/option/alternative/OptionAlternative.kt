package arrow.core.extensions.option.alternative

import arrow.Kind
import arrow.core.ForOption
import arrow.core.Option
import arrow.core.Option.Companion
import arrow.core.SequenceK
import arrow.core.extensions.OptionAlternative

/**
 * cached extension
 */
@PublishedApi()
internal val alternative_singleton: OptionAlternative = object :
    arrow.core.extensions.OptionAlternative {}

@JvmName("some")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "This method results in StackOverflow",
  level = DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, A>.some(): Option<SequenceK<A>> = arrow.core.Option.alternative().run {
  this@some.some<A>() as arrow.core.Option<arrow.core.SequenceK<A>>
}

@JvmName("many")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "This method results in StackOverflow",
  level = DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, A>.many(): Option<SequenceK<A>> = arrow.core.Option.alternative().run {
  this@many.many<A>() as arrow.core.Option<arrow.core.SequenceK<A>>
}

@JvmName("alt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "orElse { arg1 }",
  "arrow.core.orElse"
  ),
  DeprecationLevel.WARNING
)
infix fun <A> Kind<ForOption, A>.alt(arg1: Kind<ForOption, A>): Option<A> =
    arrow.core.Option.alternative().run {
  this@alt.alt<A>(arg1) as arrow.core.Option<A>
}

@JvmName("orElse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "orElse { arg1 }",
    "arrow.core.orElse"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, A>.orElse(arg1: Kind<ForOption, A>): Option<A> =
    arrow.core.Option.alternative().run {
  this@orElse.orElse<A>(arg1) as arrow.core.Option<A>
}

@JvmName("combineK")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "orElse { arg1 }",
    "arrow.core.orElse"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, A>.combineK(arg1: Kind<ForOption, A>): Option<A> =
    arrow.core.Option.alternative().run {
  this@combineK.combineK<A>(arg1) as arrow.core.Option<A>
}

@JvmName("optional")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map<Option<A>>(::Some).orElse { Some(None) }",
  "arrow.core.None", "arrow.core.Option", "arrow.core.Some", "arrow.core.orElse"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, A>.optional(): Option<Option<A>> = arrow.core.Option.alternative().run {
  this@optional.optional<A>() as arrow.core.Option<arrow.core.Option<A>>
}

@JvmName("guard")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "if (arg0) Some(Unit) else Option.empty()",
  "arrow.core.Option", "arrow.core.empty"
  ),
  DeprecationLevel.WARNING
)
fun guard(arg0: Boolean): Option<Unit> = arrow.core.Option
   .alternative()
   .guard(arg0) as arrow.core.Option<kotlin.Unit>

@JvmName("lazyOrElse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "orElse { arg1() }",
    "arrow.core.orElse"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, A>.lazyOrElse(arg1: Function0<Kind<ForOption, A>>): Option<A> =
    arrow.core.Option.alternative().run {
  this@lazyOrElse.lazyOrElse<A>(arg1) as arrow.core.Option<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Alternative typeclass is deprecated. Use concrete methods on Option",
  level = DeprecationLevel.WARNING
)
inline fun Companion.alternative(): OptionAlternative = alternative_singleton
