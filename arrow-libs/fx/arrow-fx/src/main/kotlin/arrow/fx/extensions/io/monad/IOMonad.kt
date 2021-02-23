package arrow.fx.extensions.io.monad

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Tuple2
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.IO.Companion
import arrow.fx.IODeprecation
import arrow.fx.extensions.IOMonad
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Function0
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monad_singleton: IOMonad = object : arrow.fx.extensions.IOMonad {}

@JvmName("flatMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> Kind<ForIO, A>.flatMap(arg1: Function1<A, Kind<ForIO, B>>): IO<B> =
  arrow.fx.IO.monad().run {
    this@flatMap.flatMap<A, B>(arg1) as arrow.fx.IO<B>
  }

@JvmName("tailRecM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> tailRecM(arg0: A, arg1: Function1<A, Kind<ForIO, Either<A, B>>>): IO<B> = arrow.fx.IO
  .monad()
  .tailRecM<A, B>(arg0, arg1) as arrow.fx.IO<B>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> Kind<ForIO, A>.map(arg1: Function1<A, B>): IO<B> = arrow.fx.IO.monad().run {
  this@map.map<A, B>(arg1) as arrow.fx.IO<B>
}

@JvmName("ap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> Kind<ForIO, A>.ap(arg1: Kind<ForIO, Function1<A, B>>): IO<B> = arrow.fx.IO.monad().run {
  this@ap.ap<A, B>(arg1) as arrow.fx.IO<B>
}

@JvmName("flatten")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> Kind<ForIO, Kind<ForIO, A>>.flatten(): IO<A> = arrow.fx.IO.monad().run {
  this@flatten.flatten<A>() as arrow.fx.IO<A>
}

@JvmName("followedBy")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> Kind<ForIO, A>.followedBy(arg1: Kind<ForIO, B>): IO<B> = arrow.fx.IO.monad().run {
  this@followedBy.followedBy<A, B>(arg1) as arrow.fx.IO<B>
}

@JvmName("apTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> Kind<ForIO, A>.apTap(arg1: Kind<ForIO, B>): IO<A> = arrow.fx.IO.monad().run {
  this@apTap.apTap<A, B>(arg1) as arrow.fx.IO<A>
}

@JvmName("followedByEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> Kind<ForIO, A>.followedByEval(arg1: Eval<Kind<ForIO, B>>): IO<B> =
  arrow.fx.IO.monad().run {
    this@followedByEval.followedByEval<A, B>(arg1) as arrow.fx.IO<B>
  }

@JvmName("effectM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> Kind<ForIO, A>.effectM(arg1: Function1<A, Kind<ForIO, B>>): IO<A> =
  arrow.fx.IO.monad().run {
    this@effectM.effectM<A, B>(arg1) as arrow.fx.IO<A>
  }

@JvmName("flatTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> Kind<ForIO, A>.flatTap(arg1: Function1<A, Kind<ForIO, B>>): IO<A> =
  arrow.fx.IO.monad().run {
    this@flatTap.flatTap<A, B>(arg1) as arrow.fx.IO<A>
  }

@JvmName("productL")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> Kind<ForIO, A>.productL(arg1: Kind<ForIO, B>): IO<A> = arrow.fx.IO.monad().run {
  this@productL.productL<A, B>(arg1) as arrow.fx.IO<A>
}

@JvmName("forEffect")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> Kind<ForIO, A>.forEffect(arg1: Kind<ForIO, B>): IO<A> = arrow.fx.IO.monad().run {
  this@forEffect.forEffect<A, B>(arg1) as arrow.fx.IO<A>
}

@JvmName("productLEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> Kind<ForIO, A>.productLEval(arg1: Eval<Kind<ForIO, B>>): IO<A> =
  arrow.fx.IO.monad().run {
    this@productLEval.productLEval<A, B>(arg1) as arrow.fx.IO<A>
  }

@JvmName("forEffectEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> Kind<ForIO, A>.forEffectEval(arg1: Eval<Kind<ForIO, B>>): IO<A> =
  arrow.fx.IO.monad().run {
    this@forEffectEval.forEffectEval<A, B>(arg1) as arrow.fx.IO<A>
  }

@JvmName("mproduct")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> Kind<ForIO, A>.mproduct(arg1: Function1<A, Kind<ForIO, B>>): IO<Tuple2<A, B>> =
  arrow.fx.IO.monad().run {
    this@mproduct.mproduct<A, B>(arg1) as arrow.fx.IO<arrow.core.Tuple2<A, B>>
  }

@JvmName("ifM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <B> Kind<ForIO, Boolean>.ifM(arg1: Function0<Kind<ForIO, B>>, arg2: Function0<Kind<ForIO, B>>):
  IO<B> = arrow.fx.IO.monad().run {
    this@ifM.ifM<B>(arg1, arg2) as arrow.fx.IO<B>
  }

@JvmName("selectM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> Kind<ForIO, Either<A, B>>.selectM(arg1: Kind<ForIO, Function1<A, B>>): IO<B> =
  arrow.fx.IO.monad().run {
    this@selectM.selectM<A, B>(arg1) as arrow.fx.IO<B>
  }

@JvmName("select")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> Kind<ForIO, Either<A, B>>.select(arg1: Kind<ForIO, Function1<A, B>>): IO<B> =
  arrow.fx.IO.monad().run {
    this@select.select<A, B>(arg1) as arrow.fx.IO<B>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun Companion.monad(): IOMonad = monad_singleton
