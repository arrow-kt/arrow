package arrow

import kotlin.reflect.KClass

enum class RenzuType {
    TYPE_CLASS, INSTANCE
}

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class renzu(val target: KClass<*>, val type: RenzuType)
