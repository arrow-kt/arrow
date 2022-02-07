package arrow.core.continuations

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.identity

public suspend fun <A> Effect<None, A>.toOption(): Option<A> =
  fold(::identity) { Some(it) }

public fun <A> EagerEffect<None, A>.toOption(): Option<A> =
  fold(::identity) { Some(it) }

public interface OptionEffectScope : EffectScope<None> {
  public suspend fun <B> Option<B>.bind(): B =
    bind { None }

  public suspend fun <B> B?.bind(): B =
    this ?: shift(None)

  public suspend fun ensure(value: Boolean): Unit =
    ensure(value) { None }

  public suspend fun <B> ensureNotNull(value: B?): B =
    ensureNotNull(value) { None }
}

public fun optionEffectScope(cont: EffectScope<None>): OptionEffectScope =
  object : OptionEffectScope {
    override suspend fun <B> shift(r: None): B = cont.shift(r)
  }

@Suppress("ClassName")
public object option {
  public inline fun <A> eager(crossinline f: suspend EagerEffectScope<None>.() -> A): Option<A> =
    eagerEffect(f).toOption()

  public suspend inline operator fun <A> invoke(crossinline f: suspend OptionEffectScope.() -> A): Option<A> =
    effect<None, A> { f(optionEffectScope(this)) }.toOption()
}
