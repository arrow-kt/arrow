package arrow.fx.coroutines

import arrow.core.NonEmptyList
import kotlin.jvm.JvmName

internal const val ArrowExceptionMessage =
  "Arrow-kt internal error. Please let us know and create a ticket at https://github.com/arrow-kt/arrow/issues/new/choose"

internal class ArrowInternalException(override val message: String = ArrowExceptionMessage) : RuntimeException(message)

public object Platform {

  public fun composeErrors(first: Throwable, res: Result<Any?>): Throwable {
    res.fold({ first }, { e -> first.addSuppressed(e) })
    return first
  }

  /**
   * Composes multiple errors together, meant for those cases in which error suppression, due to a second error being
   * triggered, is not acceptable.
   *
   * On top of the JVM this function uses Throwable#addSuppressed, available since Java 7. On top of JavaScript the
   * function would return a CompositeException.
   */
  public fun composeErrors(first: Throwable, vararg rest: Throwable): Throwable {
    rest.forEach { if (it != first) first.addSuppressed(it) }
    return first
  }

  /**
   * Composes multiple errors together, meant for those cases in which error suppression, due to a second error being
   * triggered, is not acceptable.
   *
   * On top of the JVM this function uses Throwable#addSuppressed, available since Java 7. On top of JavaScript the
   * function would return a CompositeException.
   */
  public fun composeErrors(first: Throwable, rest: List<Throwable>): Throwable {
    rest.forEach { if (it != first) first.addSuppressed(it) }
    return first
  }

  /**
   * Composes multiple errors together, meant for those cases in which error suppression, due to a second error being
   * triggered, is not acceptable.
   *
   * On top of the JVM this function uses Throwable#addSuppressed, available since Java 7. On top of JavaScript the
   * function would return a CompositeException.
   */
  @JvmName("composeErrorsNullable")
  public fun composeErrors(first: Throwable?, other: Throwable?): Throwable? =
    first?.let { a ->
      other?.let { b ->
        a.apply { addSuppressed(b) }
      } ?: a
    } ?: other

  /**
   * Composes multiple errors together, meant for those cases in which error suppression, due to a second error being
   * triggered, is not acceptable.
   *
   * On top of the JVM this function uses Throwable#addSuppressed, available since Java 7. On top of JavaScript the
   * function would return a CompositeException.
   */
  public fun composeErrors(first: Throwable, other: Throwable?): Throwable =
    other?.let { a ->
      a.apply { addSuppressed(first) }
    } ?: first

  /**
   * Composes multiple errors together, meant for those cases in which error suppression, due to a second error being
   * triggered, is not acceptable.
   *
   * On top of the JVM this function uses Throwable#addSuppressed, available since Java 7. On top of JavaScript the
   * function would return a CompositeException.
   */
  public fun composeErrors(all: List<Throwable>): Throwable? =
    all.firstOrNull()?.let { first ->
      composeErrors(first, all.drop(1))
    }

  public fun composeErrors(all: NonEmptyList<Throwable>): Throwable =
      composeErrors(all.head, all.tail)
}
