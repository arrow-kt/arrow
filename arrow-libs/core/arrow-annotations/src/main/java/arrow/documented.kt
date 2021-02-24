package arrow

import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.CLASS

@Retention(SOURCE)
@Target(CLASS)
@Deprecated("documented was meant to document polymorphic interfaces with @extension implementations. This annotation is deprecated along with @extensions")
annotation class documented
