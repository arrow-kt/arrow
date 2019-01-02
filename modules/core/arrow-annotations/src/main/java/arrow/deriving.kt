package arrow

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
@Deprecated("@deriving will be removed from Arrow in favor of @extension")
annotation class deriving(vararg val typeclasses: KClass<*>)
