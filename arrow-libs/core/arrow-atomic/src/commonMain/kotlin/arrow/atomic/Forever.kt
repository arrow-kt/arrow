package arrow.atomic

/**
 * while (true) as an expression.
 */
@PublishedApi
internal inline fun forever(block: () -> Unit): Nothing {
  while (true) block()
}
