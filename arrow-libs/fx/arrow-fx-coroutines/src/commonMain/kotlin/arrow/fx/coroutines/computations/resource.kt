package arrow.fx.coroutines.computations

import arrow.fx.coroutines.Resource

public interface ResourceEffect {
  public suspend fun <A> Resource<A>.bind(): A
}

public fun <A> resource(f: suspend ResourceEffect.() -> A): Resource<A> =
  Resource.Dsl(f)
