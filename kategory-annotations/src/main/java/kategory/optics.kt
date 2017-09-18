package kategory

import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.CLASS

@Retention(SOURCE)
@Target(CLASS)
annotation class lenses

@Retention(SOURCE)
@Target(CLASS)
annotation class prisms

@Retention(SOURCE)
@Target(CLASS)
annotation class isos
