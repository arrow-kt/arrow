package arrow.fx.extensions.exitcase.eq

import arrow.fx.IODeprecation
import arrow.fx.extensions.ExitCaseEq
import arrow.fx.typeclasses.ExitCase
import arrow.fx.typeclasses.ExitCase.Companion
import arrow.typeclasses.Eq
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("neqv")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <E> ExitCase<E>.neqv(EQE: Eq<E>, arg1: ExitCase<E>): Boolean =
  arrow.fx.typeclasses.ExitCase.eq<E>(EQE).run {
    this@neqv.neqv(arg1) as kotlin.Boolean
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun <E> Companion.eq(EQE: Eq<E>): ExitCaseEq<E> = object : arrow.fx.extensions.ExitCaseEq<E>
{ override fun EQE(): arrow.typeclasses.Eq<E> = EQE }
