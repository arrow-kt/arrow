package arrow

public fun <A : AutoCloseable> AutoCloseScope.install(autoCloseable: A): A =
  autoClose({ autoCloseable }) { a, errorOrNull ->
    a.close()
    errorOrNull?.let { throw it }
  }
