package arrow.match

public actual class DoesNotMatch: Throwable() {
  // disable stacktrace creation
  override fun fillInStackTrace(): Throwable = this
}
