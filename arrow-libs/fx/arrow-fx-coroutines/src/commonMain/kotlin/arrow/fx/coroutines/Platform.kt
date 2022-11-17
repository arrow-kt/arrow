package arrow.fx.coroutines

import arrow.core.NonEmptyList
import kotlin.jvm.JvmName

@Deprecated("Unused, will be removed from binary in 2.x.x")
internal const val ArrowExceptionMessage =
  "Arrow-kt internal error. Please let us know and create a ticket at https://github.com/arrow-kt/arrow/issues/new/choose"

@Deprecated("Unused, will be removed from binary in 2.x.x")
internal class ArrowInternalException(override val message: String = ArrowExceptionMessage) : RuntimeException(message)

@Deprecated(NicheApi)
public object Platform {
  
  @Deprecated(NicheApi, ReplaceWith("res.fold({ first }, { e -> first.apply { addSuppressed(e) } })"))
  public fun composeErrors(first: Throwable, res: Result<Any?>): Throwable =
    res.fold({ first }, { e -> first.apply { addSuppressed(e) } })

  /**
   * Composes multiple errors together, meant for those cases in which error suppression, due to a second error being
   * triggered, is not acceptable.
   *
   * On top of the JVM this function uses Throwable#addSuppressed, available since Java 7. On top of JavaScript the
   * function would return a CompositeException.
   */
  @Deprecated(NicheApi, ReplaceWith("first.apply { rest.forEach(::addSuppressed) }"))
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
  @Deprecated(NicheApi, ReplaceWith("first.apply { rest.forEach(::addSuppressed) }"))
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
  @Deprecated(NicheApi, ReplaceWith("first?.apply { other?.let(::addSuppressed) } ?: other"))
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
  @Deprecated(NicheApi, ReplaceWith("other?.apply { addSuppressed(first) } ?: first"))
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
  @Deprecated(NicheApi, ReplaceWith("all.head.apply { all.tail.forEach(::addSuppressed) }"))
  public fun composeErrors(all: List<Throwable>): Throwable? =
    all.firstOrNull()?.let { first ->
      composeErrors(first, all.drop(1))
    }

  @Deprecated(NicheApi, ReplaceWith("all.head.apply { all.tail.forEach(::addSuppressed) }"))
  public fun composeErrors(all: NonEmptyList<Throwable>): Throwable =
    composeErrors(all.head, all.tail)
}

private const val  NicheApi: String =
  "Niche use-case, prefer using higher level operators or addSuppressed from Kotlin Std"
