package arrow.fx.stm.internal

public actual object RetryException : Throwable("Arrow STM Retry. This should always be caught by arrow internally. Please report this as a bug if that is not the case!") {
  override fun fillInStackTrace(): Throwable { return this }
}
