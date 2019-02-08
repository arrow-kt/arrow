package arrow.typeclasses.suspended

interface Predef {
  suspend fun <A> effectIdentity(a: A): A = a
}

