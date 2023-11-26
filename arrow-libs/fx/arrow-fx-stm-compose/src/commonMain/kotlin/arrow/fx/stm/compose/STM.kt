package arrow.fx.stm.compose

public inline fun <A> stm(noinline f: STM.() -> A): STM.() -> A = f

public interface STM {
  public fun retry(): Nothing
  public infix fun <A> (STM.() -> A).orElse(other: STM.() -> A): A
  public fun <A> catch(f: STM.() -> A, onError: STM.(Throwable) -> A): A

  public fun check(condition: Boolean) {
    if (!condition) retry()
  }

  public fun <A> TQueue<A>.peek(): A = if (list.isEmpty()) retry() else list.first()
  public fun <A> TQueue<A>.read(): A = if (list.isEmpty()) retry() else list.removeFirst()
}

public class BlockedIndefinitely : Throwable("Transaction blocked indefinitely")

