package arrow.core.extensions.ior.monad

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.ForIor
import arrow.core.Ior
import arrow.core.Ior.Companion
import arrow.core.Tuple2
import arrow.core.extensions.IorMonad
import arrow.typeclasses.Semigroup

@JvmName("flatMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.flatMap(SL, arg1)",
    "arrow.core.flatMap"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B> Kind<Kind<ForIor, L>, A>.flatMap(
  SL: Semigroup<L>,
  arg1: Function1<A, Kind<Kind<ForIor, L>, B>>
): Ior<L, B> = arrow.core.Ior.monad<L>(SL).run {
  this@flatMap.flatMap<A, B>(arg1) as arrow.core.Ior<L, B>
}

@JvmName("tailRecM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.tailRecM(SL, arg0, arg1)"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B> tailRecM(
  SL: Semigroup<L>,
  arg0: A,
  arg1: Function1<A, Kind<Kind<ForIor, L>, Either<A, B>>>
): Ior<L, B> = arrow.core.Ior
  .monad<L>(SL)
  .tailRecM<A, B>(arg0, arg1) as arrow.core.Ior<L, B>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.map(SL, arg1)"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B> Kind<Kind<ForIor, L>, A>.map(SL: Semigroup<L>, arg1: Function1<A, B>): Ior<L, B> =
  arrow.core.Ior.monad<L>(SL).run {
    this@map.map<A, B>(arg1) as arrow.core.Ior<L, B>
  }

/**
 *  @see [Apply.ap]
 */
@JvmName("ap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.ap(SL, arg1)",
    "arrow.core.ap"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B> Kind<Kind<ForIor, L>, A>.ap(
  SL: Semigroup<L>,
  arg1: Kind<Kind<ForIor, L>, Function1<A, B>>
): Ior<L, B> = arrow.core.Ior.monad<L>(SL).run {
  this@ap.ap<A, B>(arg1) as arrow.core.Ior<L, B>
}

@JvmName("flatten")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.flatten(SL)",
    "arrow.core.flatten"
  ),
  DeprecationLevel.WARNING
)
fun <L, A> Kind<Kind<ForIor, L>, Kind<Kind<ForIor, L>, A>>.flatten(SL: Semigroup<L>): Ior<L, A> =
  arrow.core.Ior.monad<L>(SL).run {
    this@flatten.flatten<A>() as arrow.core.Ior<L, A>
  }

@JvmName("followedBy")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.flatMap(SL) { arg1 }",
    "arrow.core.flatMap"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B> Kind<Kind<ForIor, L>, A>.followedBy(SL: Semigroup<L>, arg1: Kind<Kind<ForIor, L>, B>):
  Ior<L, B> = arrow.core.Ior.monad<L>(SL).run {
    this@followedBy.followedBy<A, B>(arg1) as arrow.core.Ior<L, B>
  }

@JvmName("apTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "Ior.mapN(SL, this, arg1) { left, _ -> left }",
    "arrow.core.Ior"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B> Kind<Kind<ForIor, L>, A>.apTap(SL: Semigroup<L>, arg1: Kind<Kind<ForIor, L>, B>):
  Ior<L, A> = arrow.core.Ior.monad<L>(SL).run {
    this@apTap.apTap<A, B>(arg1) as arrow.core.Ior<L, A>
  }

@JvmName("followedByEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.flatMap(SL) { arg1.value() }",
    "arrow.core.flatMap"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B> Kind<Kind<ForIor, L>, A>.followedByEval(
  SL: Semigroup<L>,
  arg1: Eval<Kind<Kind<ForIor, L>, B>>
): Ior<L, B> = arrow.core.Ior.monad<L>(SL).run {
  this@followedByEval.followedByEval<A, B>(arg1) as arrow.core.Ior<L, B>
}

@JvmName("effectM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.flatMap(SL) { a -> arg1(a).map { a } }",
    "arrow.core.flatMap"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B> Kind<Kind<ForIor, L>, A>.effectM(
  SL: Semigroup<L>,
  arg1: Function1<A, Kind<Kind<ForIor, L>, B>>
): Ior<L, A> = arrow.core.Ior.monad<L>(SL).run {
  this@effectM.effectM<A, B>(arg1) as arrow.core.Ior<L, A>
}

@JvmName("flatTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.flatMap { a -> arg1(a).map { a } }",
    "arrow.core.flatMap"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B> Kind<Kind<ForIor, L>, A>.flatTap(
  SL: Semigroup<L>,
  arg1: Function1<A, Kind<Kind<ForIor, L>, B>>
): Ior<L, A> = arrow.core.Ior.monad<L>(SL).run {
  this@flatTap.flatTap<A, B>(arg1) as arrow.core.Ior<L, A>
}

@JvmName("productL")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.flatMap { a -> arg1.map { a } }",
    "arrow.core.flatMap"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B> Kind<Kind<ForIor, L>, A>.productL(SL: Semigroup<L>, arg1: Kind<Kind<ForIor, L>, B>):
  Ior<L, A> = arrow.core.Ior.monad<L>(SL).run {
    this@productL.productL<A, B>(arg1) as arrow.core.Ior<L, A>
  }

@JvmName("forEffect")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.flatMap { a -> arg1.map { a } }",
    "arrow.core.flatMap"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B> Kind<Kind<ForIor, L>, A>.forEffect(SL: Semigroup<L>, arg1: Kind<Kind<ForIor, L>, B>):
  Ior<L, A> = arrow.core.Ior.monad<L>(SL).run {
    this@forEffect.forEffect<A, B>(arg1) as arrow.core.Ior<L, A>
  }

@JvmName("productLEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.flatMap { a -> arg1.value().map { a } }",
    "arrow.core.flatMap"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B> Kind<Kind<ForIor, L>, A>.productLEval(
  SL: Semigroup<L>,
  arg1: Eval<Kind<Kind<ForIor, L>, B>>
): Ior<L, A> = arrow.core.Ior.monad<L>(SL).run {
  this@productLEval.productLEval<A, B>(arg1) as arrow.core.Ior<L, A>
}

@JvmName("forEffectEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.flatMap { a -> arg1.value().map { a } }",
    "arrow.core.flatMap"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B> Kind<Kind<ForIor, L>, A>.forEffectEval(
  SL: Semigroup<L>,
  arg1: Eval<Kind<Kind<ForIor, L>, B>>
): Ior<L, A> = arrow.core.Ior.monad<L>(SL).run {
  this@forEffectEval.forEffectEval<A, B>(arg1) as arrow.core.Ior<L, A>
}

@JvmName("mproduct")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.mproduct(SL, arg1)",
    "arrow.core.mproduct"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B> Kind<Kind<ForIor, L>, A>.mproduct(
  SL: Semigroup<L>,
  arg1: Function1<A, Kind<Kind<ForIor, L>, B>>
): Ior<L, Tuple2<A, B>> = arrow.core.Ior.monad<L>(SL).run {
  this@mproduct.mproduct<A, B>(arg1) as arrow.core.Ior<L, arrow.core.Tuple2<A, B>>
}

@JvmName("ifM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.ifM(SL, arg1, arg2)",
    "arrow.core.ifM"
  ),
  DeprecationLevel.WARNING
)
fun <L, B> Kind<Kind<ForIor, L>, Boolean>.ifM(
  SL: Semigroup<L>,
  arg1: Function0<Kind<Kind<ForIor, L>, B>>,
  arg2: Function0<Kind<Kind<ForIor, L>, B>>
): Ior<L, B> = arrow.core.Ior.monad<L>(SL).run {
  this@ifM.ifM<B>(arg1, arg2) as arrow.core.Ior<L, B>
}

@JvmName("selectM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.selectM(SL, arg1)",
    "arrow.core.selectM"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B> Kind<Kind<ForIor, L>, Either<A, B>>.selectM(
  SL: Semigroup<L>,
  arg1: Kind<Kind<ForIor, L>, Function1<A, B>>
): Ior<L, B> = arrow.core.Ior.monad<L>(SL).run {
  this@selectM.selectM<A, B>(arg1) as arrow.core.Ior<L, B>
}

@JvmName("select")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.selectM(SL, arg1)",
    "arrow.core.selectM"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B> Kind<Kind<ForIor, L>, Either<A, B>>.select(
  SL: Semigroup<L>,
  arg1: Kind<Kind<ForIor, L>, Function1<A, B>>
): Ior<L, B> = arrow.core.Ior.monad<L>(SL).run {
  this@select.select<A, B>(arg1) as arrow.core.Ior<L, B>
}

/**
 *  [Monad] abstract over the ability to declare sequential computations that are dependent in the order or
 *  the results of previous computations.
 *
 *  Given a type constructor [F] with a value of [A] we can compose multiple operations of type
 *  `Kind<F, ?>` where `?` denotes a value being transformed.
 *
 *  This is true for all type constructors that can support the [Monad] type class including and not limited to [Option], [Either], [List] ...
 *
 *  [The Monad Tutorial](https://arrow-kt.io/docs/patterns/monads/)
 */
@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Monad typeclass is deprecated. Use concrete methods on Ior",
  level = DeprecationLevel.WARNING
)
inline fun <L> Companion.monad(SL: Semigroup<L>): IorMonad<L> = object :
  arrow.core.extensions.IorMonad<L> { override fun SL(): arrow.typeclasses.Semigroup<L> = SL }
