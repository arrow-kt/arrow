import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.identity
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

suspend fun <A> option(f: OptionEffect<A>.() -> A): Option<A> =
  cont<None, A> { f(OptionEffect(this)) }.fold(::identity) { Some(it) }

class OptionEffect<A>(private val cont: ContEffect<None>) : ContEffect<None> by cont {
  suspend fun <B> Option<B>.bind(): B =
    when (this) {
      None -> shift(None)
      is Some -> value
    }

  public suspend fun ensure(value: Boolean): Unit =
    if (value) Unit else shift(None)
}

@OptIn(ExperimentalContracts::class) // Contracts not available on open functions, so made it top-level.
public suspend fun <B : Any> ContEffect<None>.ensureNotNull(value: B?): B {
  contract { returns() implies (value != null) }
  return value ?: shift(None)
}
