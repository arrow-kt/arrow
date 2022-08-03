package arrow.fx.coroutines.continuations

import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.resource
import arrow.fx.coroutines.releaseCase

// TODO Should we just have EffectDSL as a DslMarker in Arrow Core?
@DslMarker
public annotation class ResourceDSL

/**
 * Computation block for the [Resource] type.
 * The [Resource] allows us to describe resources as immutable values,
 * and compose them together in simple ways.
 * This way you can split the logic of what a `Resource` is and how it should be closed from how you use them.
 *
 *  * # Using and composing Resource
 *
 * ```kotlin
 * import arrow.fx.coroutines.continuations.resource
 * import arrow.fx.coroutines.release
 *
 * class UserProcessor {
 *   fun start(): Unit = println("Creating UserProcessor")
 *   fun shutdown(): Unit = println("Shutting down UserProcessor")
 *   fun process(ds: DataSource): List<String> =
 *    ds.users().map { "Processed $it" }
 * }
 *
 * class DataSource {
 *   fun connect(): Unit = println("Connecting dataSource")
 *   fun users(): List<String> = listOf("User-1", "User-2", "User-3")
 *   fun close(): Unit = println("Closed dataSource")
 * }
 *
 * class Service(val db: DataSource, val userProcessor: UserProcessor) {
 *   suspend fun processData(): List<String> = userProcessor.process(db)
 * }
 *
 * suspend fun main(): Unit {
 *   resource<Service> {
 *     val dataSource = resource {
 *       DataSource().also { it.connect() }
 *     } release DataSource::close
 *
 *     val userProcessor = resource {
 *       UserProcessor().also(UserProcessor::start)
 *     } release UserProcessor::shutdown
 *
 *     Service(dataSource, userProcessor)
 *   }.use { service -> service.processData() }
 * }
 * ```
 * <!--- KNIT example-resource-computations-01.kt -->
 */
public interface ResourceScope {
  public suspend fun <A> Resource<A>.bind(): A
  
  @ResourceDSL
  public suspend fun <A> resource(
    acquire: suspend () -> A,
    release: suspend (A, ExitCase) -> Unit,
  ): A = arrow.fx.coroutines.resource(acquire, release).bind()
  
  @ResourceDSL
  public suspend fun <A> resource(
    acquire: suspend () -> A,
    release: suspend (A) -> Unit,
  ): A = arrow.fx.coroutines.resource(acquire, release).bind()
  
  /**
   * Composes a [release] action to a [Resource.use] action creating a [Resource].
   */
  @ResourceDSL
  public suspend infix fun <A> Resource<A>.release(release: suspend (A) -> Unit): A =
    resource({ bind() }, release)
  
  /**
   * Composes a [releaseCase] action to a [Resource.use] action creating a [Resource].
   */
  @ResourceDSL
  public suspend infix fun <A> Resource<A>.releaseCase(release: suspend (A, ExitCase) -> Unit): A =
    resource({ bind() }, { a, ex -> release(a, ex) })
}

public fun <A> resource(f: suspend ResourceScope.() -> A): Resource<A> =
  Resource.Dsl(f)
