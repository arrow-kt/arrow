package arrow

interface Alternative<F> : Applicative<F>, MonoidK<F>, Typeclass

inline fun <reified F> alternative(): Alternative<F> = instance(InstanceParametrizedType(Alternative::class.java, listOf(F::class.java)))
