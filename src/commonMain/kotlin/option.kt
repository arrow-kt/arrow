import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.identity
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.jvm.JvmInline

suspend fun <A> option(f: OptionEffect.() -> A): Option<A> =
  cont<None, A> { f(OptionEffect(this)) }.toOption()

suspend fun <A> Cont<None, A>.toOption(): Option<A> =
  fold(::identity) { Some(it) }

@JvmInline
value class OptionEffect(private val cont: ContEffect<None>) : ContEffect<None> {
  suspend fun <B> Option<B>.bind(): B =
    bind { None }

  public suspend fun ensure(value: Boolean): Unit =
    if (value) Unit else shift(None)

  override suspend fun <B> shift(r: None): B =
    cont.shift(r)

  override suspend fun <B> catch(f: suspend () -> B, g: suspend (None) -> B): B =
    cont.catch(f, g)
}

@OptIn(ExperimentalContracts::class) // Contracts not available on open functions, so made it top-level.
public suspend fun <B : Any> ContEffect<None>.ensureNotNull(value: B?): B {
  contract { returns() implies (value != null) }
  return value ?: shift(None)
}
