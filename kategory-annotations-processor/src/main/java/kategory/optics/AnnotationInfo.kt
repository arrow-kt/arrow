package kategory.optics

val lensesAnnotationKClass = lenses::class
val lensesAnnotationClass = lensesAnnotationKClass.java
val lensesAnnotationName = "@" + lensesAnnotationClass.simpleName
val lensesAnnotationTarget = "data class"

val prismsAnnotationKClass = prisms::class
val prismsAnnotationClass = prismsAnnotationKClass.java
val prismsAnnotationName = "@" + prismsAnnotationClass.simpleName
val prismsAnnotationTarget = "sealed class"