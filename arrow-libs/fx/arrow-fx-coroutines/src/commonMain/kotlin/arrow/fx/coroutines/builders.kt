package arrow.fx.coroutines

import kotlinx.coroutines.awaitCancellation

@Deprecated(
  "Prefer using awaitCancellation from KotlinX",
  ReplaceWith("awaitCancellation()", "kotlinx.coroutines.awaitCancellation")
)
public suspend fun <A> never(): A =
  awaitCancellation()

@Deprecated(
  "Prefer simply using Unit, or empty lambda when needed",
  ReplaceWith("Unit")
)
public suspend fun unit(): Unit = Unit
