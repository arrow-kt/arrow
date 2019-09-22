package arrow

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS

//metadebug

@Retention(RUNTIME)
@Target(CLASS)
@MustBeDocumented
annotation class higherkind
