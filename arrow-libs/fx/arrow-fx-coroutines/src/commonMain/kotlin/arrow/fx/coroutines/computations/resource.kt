package arrow.fx.coroutines.computations

import arrow.fx.coroutines.Resource

/**
 * Computation block for the [Resource] type.
 * The [Resource] allows us to describe resources as immutable values,
 * and compose them together in simple ways.
 * This way you can split the logic of what a `Resource` is and how it should be closed from how you use them.
 *
 *  * # Using and composing Resource
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.computations.resource
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
public interface ResourceEffect {
  public suspend fun <A> Resource<A>.bind(): A
}

public fun <A> resource(f: suspend ResourceEffect.() -> A): Resource<A> =
  Resource.Dsl(f)
