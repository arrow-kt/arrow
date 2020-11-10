package arrow.core.computations

import arrow.continuations.generic.DelimContScope
import arrow.continuations.generic.DelimitedScope
import arrow.core.computations.suspended.BindSyntax

@Suppress("ClassName")
object nullable {
  operator fun <A> invoke(func: suspend BindSyntax.() -> A?): A? =
    DelimContScope.reset { NullableBindSyntax(this).func() }

  private class NullableBindSyntax<R>(
    scope: DelimitedScope<R?>
  ) : BindSyntax, DelimitedScope<R?> by scope {
    override suspend fun <A> A?.invoke(): A =
      this ?: shift { null }
  }
}
