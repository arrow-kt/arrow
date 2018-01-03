package arrow

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class typeclass()

/**
 * Marker trait that all Functional typeclasses such as Monad, Functor, etc... must implement to be considered
 * candidates to pair with global instances
 */
interface TC