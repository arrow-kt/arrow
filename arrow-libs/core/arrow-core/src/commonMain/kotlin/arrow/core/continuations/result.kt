package arrow.core.continuations

import arrow.core.identity
import kotlin.jvm.JvmInline

public suspend fun <A> Effect<Throwable, A>.toResult(): Result<A> =
  fold({ Result.failure(it) }, { Result.success(it) })

public fun <A> EagerEffect<Throwable, A>.toResult(): Result<A> =
  fold({ Result.failure(it) }, { Result.success(it) })

@JvmInline
public value class ResultEffectScope(private val cont: EffectScope<Throwable>) : EffectScope<Throwable> {
  override suspend fun <B> shift(r: Throwable): B =
    cont.shift(r)

  public suspend fun <B> Result<B>.bind(): B =
    fold(::identity) { shift(it) }
}

@JvmInline
public value class ResultEagerEffectScope(private val cont: EagerEffectScope<Throwable>) : EagerEffectScope<Throwable> {
  @Suppress("ILLEGAL_RESTRICTED_SUSPENDING_FUNCTION_CALL")
  override suspend fun <B> shift(r: Throwable): B =
    cont.shift(r)

  public suspend fun <B> Result<B>.bind(): B =
    fold(::identity) { shift(it) }
}

@Deprecated(resultDSLDeprecation, ReplaceWith("result", "arrow.core.raise.result"))
@Suppress("ClassName")
public object result {
  @Deprecated(
    resultDSLDeprecation,
    ReplaceWith("result(f)", "arrow.core.raise.result")
  )
  public inline fun <A> eager(crossinline f: suspend ResultEagerEffectScope.() -> A): Result<A> =
    eagerEffect<Throwable, A> {
      @Suppress("ILLEGAL_RESTRICTED_SUSPENDING_FUNCTION_CALL")
      f(ResultEagerEffectScope(this))
    }.toResult()
  
  @Deprecated(
    resultDSLDeprecation,
    ReplaceWith("result(f)", "arrow.core.raise.result")
  )
  public suspend inline operator fun <A> invoke(crossinline f: suspend ResultEffectScope.() -> A): Result<A> =
    effect<Throwable, A> { f(ResultEffectScope(this)) }.toResult()
}

@PublishedApi internal const val resultDSLDeprecation: String =
  "The result DSL has been moved to arrow.core.raise.result.\n" +
    "Replace import arrow.core.computations.* with arrow.core.raise.*"
