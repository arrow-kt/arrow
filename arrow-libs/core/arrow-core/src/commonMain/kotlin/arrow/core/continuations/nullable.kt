package arrow.core.continuations

@Suppress("ClassName")
public object nullable {
  public inline fun <A> eager(crossinline f: suspend OptionEagerEffectScope.() -> A): A? =
    option.eager(f).orNull()

  public suspend inline operator fun <A> invoke(crossinline f: suspend OptionEffectScope.() -> A): A? =
    option(f).orNull()
}
