package arrow.fx.reactor.extensions.monok.bracket

import arrow.Kind
import arrow.fx.reactor.DeprecateReactor
import arrow.fx.reactor.ForMonoK
import arrow.fx.reactor.MonoK
import arrow.fx.reactor.MonoK.Companion
import arrow.fx.reactor.extensions.MonoKBracket
import arrow.fx.typeclasses.ExitCase
import kotlin.Deprecated
import kotlin.Function1
import kotlin.Function2
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Throwable
import kotlin.Unit
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val bracket_singleton: MonoKBracket = object : arrow.fx.reactor.extensions.MonoKBracket {}

/**
 *  A way to safely acquire a resource and release in the face of errors and cancellation.
 *  It uses [ExitCase] to distinguish between different exit cases when releasing the acquired resource.
 *
 *  @param use is the action to consume the resource and produce an [F] with the result.
 *  Once the resulting [F] terminates, either successfully, error or cancelled.
 *
 *  @param release the allocated resource after the resulting [F] of [use] is terminates.
 *
 *  ```kotlin:ank:playground
 *  import arrow.fx.reactor.*
 * import arrow.fx.reactor.extensions.monok.bracket.*
 * import arrow.core.*
 *
 *
 *  import arrow.fx.reactor.extensions.monok.monadDefer.defer
 * import arrow.fx.reactor.extensions.monok.monadDefer.later
 *
 *  class File(url: String) {
 *   fun open(): File = this
 *   fun close(): Unit {}
 *   override fun toString(): String = "This file contains some interesting content!"
 *  }
 *
 *  fun openFile(uri: String): Kind<F, File> = later({ File(uri).open() })
 *  fun closeFile(file: File): Kind<F, Unit> = later({ file.close() })
 *  fun fileToString(file: File): Kind<F, String> = later({ file.toString() })
 *
 *  fun main(args: Array<String>) {
 *   //sampleStart
 *   val release: (File, ExitCase<Throwable>) -> Kind<F, Unit> = { file, exitCase ->
 *       when (exitCase) {
 * do something * / }
 * do something * / }
 * do something * / }
 *       }
 *       closeFile(file)
 *   }
 *
 *   val use: (File) -> Kind<F, String> = { file: File -> fileToString(file) }
 *
 *   val safeComputation = openFile("data.json").bracketCase(release, use)
 *   //sampleEnd
 *   println(safeComputation)
 *  }
 *  ```
 */
@JvmName("bracketCase")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForMonoK, A>.bracketCase(
  arg1: Function2<A, ExitCase<Throwable>, Kind<ForMonoK,
    Unit>>,
  arg2: Function1<A, Kind<ForMonoK, B>>
): MonoK<B> =
  arrow.fx.reactor.MonoK.bracket().run {
    this@bracketCase.bracketCase<A, B>(arg1, arg2) as arrow.fx.reactor.MonoK<B>
  }

/**
 *  Meant for specifying tasks with safe resource acquisition and release in the face of errors and interruption.
 *  It would be the the equivalent of `try/catch/finally` statements in mainstream imperative languages for resource
 *  acquisition and release.
 *
 *  @param release is the action that's supposed to release the allocated resource after `use` is done, irregardless
 *  of its exit condition.
 *
 *  ```kotlin:ank:playground
 *  import arrow.fx.reactor.*
 * import arrow.fx.reactor.extensions.monok.bracket.*
 * import arrow.core.*
 *
 *
 *  import arrow.fx.reactor.extensions.monok.monadDefer.defer
 * import arrow.fx.reactor.extensions.monok.monadDefer.later
 *
 *  class File(url: String) {
 *   fun open(): File = this
 *   fun close(): Unit {}
 *   override fun toString(): String = "This file contains some interesting content!"
 *  }
 *
 *  fun openFile(uri: String): Kind<F, File> = later({ File(uri).open() })
 *  fun closeFile(file: File): Kind<F, Unit> = later({ file.close() })
 *  fun fileToString(file: File): Kind<F, String> = later({ file.toString() })
 *
 *  fun main(args: Array<String>) {
 *   //sampleStart
 *   val safeComputation = openFile("data.json").bracket({ file: File -> closeFile(file) }, { file -> fileToString(file) })
 *   //sampleEnd
 *   println(safeComputation)
 *  }
 *  ```
 */
@JvmName("bracket")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForMonoK, A>.bracket(
  arg1: Function1<A, Kind<ForMonoK, Unit>>,
  arg2: Function1<A,
    Kind<ForMonoK, B>>
): MonoK<B> = arrow.fx.reactor.MonoK.bracket().run {
  this@bracket.bracket<A, B>(arg1, arg2) as arrow.fx.reactor.MonoK<B>
}

/**
 *  Meant for ensuring a given task continues execution even when interrupted.
 */
@JvmName("uncancellable")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Kind<ForMonoK, A>.uncancellable(): MonoK<A> = arrow.fx.reactor.MonoK.bracket().run {
  this@uncancellable.uncancellable<A>() as arrow.fx.reactor.MonoK<A>
}

@JvmName("uncancelable")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Kind<ForMonoK, A>.uncancelable(): MonoK<A> = arrow.fx.reactor.MonoK.bracket().run {
  this@uncancelable.uncancelable<A>() as arrow.fx.reactor.MonoK<A>
}

/**
 *  Executes the given `finalizer` when the source is finished, either in success or in error, or if cancelled.
 *
 *  As best practice, it's not a good idea to release resources via `guaranteeCase` in polymorphic code.
 *  Prefer [bracket] for the acquisition and release of resources.
 *
 *  @see [guaranteeCase] for the version that can discriminate between termination conditions
 *
 *  @see [bracket] for the more general operation
 */
@JvmName("guarantee")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Kind<ForMonoK, A>.guarantee(arg1: Kind<ForMonoK, Unit>): MonoK<A> =
  arrow.fx.reactor.MonoK.bracket().run {
    this@guarantee.guarantee<A>(arg1) as arrow.fx.reactor.MonoK<A>
  }

/**
 *  Executes the given `finalizer` when the source is finished, either in success or in error, or if cancelled, allowing
 *  for differentiating between exit conditions. That's thanks to the [ExitCase] argument of the finalizer.
 *
 *  As best practice, it's not a good idea to release resources via `guaranteeCase` in polymorphic code.
 *  Prefer [bracketCase] for the acquisition and release of resources.
 *
 *  @see [guarantee] for the simpler version
 *
 *  @see [bracketCase] for the more general operation
 */
@JvmName("guaranteeCase")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Kind<ForMonoK, A>.guaranteeCase(arg1: Function1<ExitCase<Throwable>, Kind<ForMonoK, Unit>>):
  MonoK<A> = arrow.fx.reactor.MonoK.bracket().run {
  this@guaranteeCase.guaranteeCase<A>(arg1) as arrow.fx.reactor.MonoK<A>
}

/**
 *  Executes the given [finalizer] when the source is cancelled, allowing registering a cancellation token.
 *
 *  Useful for wiring cancellation tokens between fibers, building inter-op with other effect systems or testing.
 */
@JvmName("onCancel")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Kind<ForMonoK, A>.onCancel(arg1: Kind<ForMonoK, Unit>): MonoK<A> =
  arrow.fx.reactor.MonoK.bracket().run {
    this@onCancel.onCancel<A>(arg1) as arrow.fx.reactor.MonoK<A>
  }

/**
 *  Executes the given `finalizer` with the given error when the source is finished in error.
 */
@JvmName("onError")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Kind<ForMonoK, A>.onError(arg1: Function1<Throwable, Kind<ForMonoK, Unit>>): MonoK<A> =
  arrow.fx.reactor.MonoK.bracket().run {
    this@onError.onError<A>(arg1) as arrow.fx.reactor.MonoK<A>
  }

/**
 *  Extension of MonadError exposing the [bracket] operation, a generalized abstracted pattern of safe resource
 *  acquisition and release in the face of errors or interruption.
 *
 *  @define The functions receiver here (Kind<F, A>) would stand for the "acquireParam", and stands for an action that
 *  "acquires" some expensive resource, that needs to be used and then discarded.
 *
 *  @define use is the action that uses the newly allocated resource and that will provide the final result.
 */
@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateReactor)
inline fun Companion.bracket(): MonoKBracket = bracket_singleton
