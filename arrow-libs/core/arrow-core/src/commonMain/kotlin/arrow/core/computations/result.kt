package arrow.core.computations

public object ResultEffect {
  public fun <A> Result<A>.bind(): A = getOrThrow()
}

@Suppress("ClassName")
public object result {
  public inline operator fun <A> invoke(c: ResultEffect.() -> A): Result<A> =
    kotlin.runCatching { c(ResultEffect) }
}
