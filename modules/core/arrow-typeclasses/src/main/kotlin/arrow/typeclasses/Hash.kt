package arrow.typeclasses

interface Hash<in F> : Eq<F> {

  fun F.hash(): Int

  companion object {

    inline operator fun <F> invoke(crossinline hashF: (F) -> Int): Hash<F> = object : Hash<F> {
      override fun F.hash(): Int = hashF(this)

      // either this.hash == hash, or this == b or supply an eq function, dunno what is best
      override fun F.eqv(b: F): Boolean = hashF(b) == hashF(this)
    }

    fun any(): Hash<Any> = HashAny

    private object HashAny : Hash<Any> {
      override fun Any.hash(): Int = hashCode()

      override fun Any.eqv(b: Any): Boolean = Eq.any().run { eqv(b) }
    }
  }
}