package arrow.fx.coroutines.continuations

import arrow.AutoCloseScope
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.ExitCase.Companion.ExitCase
import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.releaseCase

@DslMarker
public annotation class ScopeDSL

@DslMarker
public annotation class ResourceDSL

/**
 * This Marker exists to prevent being able to call `bind` from `install`, and its derived methods.
 * This is done to ensure correct usage of [ResourceScope].
 */
@ResourceDSL
public object AcquireStep

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
 * //sampleStart
 * val userProcessor = resource {
 *   UserProcessor().also(UserProcessor::start)
 * } release UserProcessor::shutdown
 *
 * val dataSource = resource {
 *   DataSource().also { it.connect() }
 * } release DataSource::close
 *
 * suspend fun main(): Unit {
 *   resource<Service> {
 *     Service(dataSource.bind(), userProcessor.bind())
 *   }.use { service -> service.processData() }
 * }
 * //sampleEnd
 * ```
 * <!--- KNIT example-resource-computations-01.kt -->
 */
@ResourceDSL
public interface ResourceScope : AutoCloseScope {

  @ResourceDSL
  public suspend fun <A> Resource<A>.bind(): A

  /**
   * Install [A] into the [ResourceScope].
   * It's [release] function will be called with the appropriate [ExitCase] if this [ResourceScope] finishes.
   * It results either in [ExitCase.Completed], [ExitCase.Cancelled] or [ExitCase.Failure] depending on the terminal state of [Resource] lambda.
   */
  @ResourceDSL
  public suspend fun <A> install(
    acquire: suspend AcquireStep.() -> A,
    release: suspend (A, ExitCase) -> Unit,
  ): A

  /** Composes a [release] action to a [Resource] value before binding. */
  @ResourceDSL
  public suspend infix fun <A> Resource<A>.release(release: suspend (A) -> Unit): A {
    val a = bind()
    return install({ a }) { a, _ -> release(a) }
  }

  /** Composes a [releaseCase] action to a [Resource] value before binding. */
  @ResourceDSL
  public suspend infix fun <A> Resource<A>.releaseCase(release: suspend (A, ExitCase) -> Unit): A {
    val a = bind()
    return install({ a }, release)
  }

  public suspend infix fun onRelease(release: suspend (ExitCase) -> Unit): Unit =
    install({ }) { _, exitCase -> release(exitCase) }
}

@ScopeDSL
public fun <A> resource(f: suspend ResourceScope.() -> A): Resource<A> =
  Resource.Dsl(f)
