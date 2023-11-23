package arrow.fx.stm.compose

public inline fun <A> stm(noinline f: STM.() -> A): STM.() -> A = f

public interface STM {
  public fun retry(): Nothing
  public infix fun <A> (STM.() -> A).orElse(other: STM.() -> A): A
  public fun <A> catch(f: STM.() -> A, onError: STM.(Throwable) -> A): A

  public fun check(condition: Boolean) {
    if (!condition) retry()
  }

  public fun <A> TVar<A>.read(): A = variable.value
  public fun <A> TVar<A>.write(value: A) {
    variable.value = value
  }
  public fun <A> TVar<A>.modify(f: (A) -> A) {
    variable.value = f(variable.value)
  }
}

public class BlockedIndefinitely : Throwable("Transaction blocked indefinitely")

