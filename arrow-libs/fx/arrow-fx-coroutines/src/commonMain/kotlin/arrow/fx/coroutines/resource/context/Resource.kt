package arrow.fx.coroutines.resource.context

import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.IODispatcher
import arrow.fx.coroutines.ResourceDSL
import arrow.fx.coroutines.ScopeDSL
import arrow.fx.coroutines.autoCloseable
import arrow.fx.coroutines.resourceScope
import kotlinx.coroutines.CoroutineDispatcher

public typealias Resource<A> = arrow.fx.coroutines.Resource<A>
public typealias ResourceScope = arrow.fx.coroutines.ResourceScope

public fun <A> resource(
  acquire: suspend () -> A,
  release: suspend (A, ExitCase) -> Unit,
): Resource<A> = arrow.fx.coroutines.resource {
  install({ acquire() }, release)
}

@ScopeDSL
public fun <A> resource(block: suspend context(ResourceScope) () -> A): Resource<A> = block

@ScopeDSL
public suspend inline fun <A> resourceScope(action: suspend context(ResourceScope) () -> A): A =
  resourceScope(action)

@ResourceDSL
context(resources: ResourceScope)
public suspend fun <A> Resource<A>.bind(): A = with(resources) {
  this@bind.bind()
}

@ResourceDSL
@OptIn(ExperimentalStdlibApi::class)  // 'AutoCloseable' in stdlib < 2.0
context(resources: ResourceScope)
public suspend fun <A : AutoCloseable> autoCloseable(
  closingDispatcher: CoroutineDispatcher = IODispatcher,
  autoCloseable: suspend () -> A,
): A = resources.autoCloseable(closingDispatcher, autoCloseable)

@ResourceDSL
context(resources: ResourceScope)
public suspend fun <A> install(
  acquire: suspend () -> A,
  release: suspend (A, ExitCase) -> Unit,
): A = resources.install({ acquire() }, release)
