@file:Suppress("UNCHECKED_CAST")

package arrow.generics.shallow.functions

public sealed interface Function<out T> {
  public fun f(vararg args: Any?): T
}

public fun interface Function1<in A, out T> : Function<T>{
  public fun invoke(a: A): T
  override fun f(args: Array<out Any?>): T =
    invoke(args[0] as A)
}

public fun interface Function2<in A, in B, out T> : Function<T>{
  public fun invoke(a: A, b: B): T
  override fun f(args: Array<out Any?>): T =
    invoke(args[0] as A, args[1] as B)
}
