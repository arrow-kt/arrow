package kategory.optics

import kategory.isos
import kategory.lenses
import kategory.prisms

val lensesAnnotationKClass = lenses::class
val lensesAnnotationClass = lensesAnnotationKClass.java
val lensesAnnotationName = "@" + lensesAnnotationClass.simpleName
val lensesAnnotationTarget = "data class"

val prismsAnnotationKClass = prisms::class
val prismsAnnotationClass = prismsAnnotationKClass.java
val prismsAnnotationName = "@" + prismsAnnotationClass.simpleName
val prismsAnnotationTarget = "sealed class"

val isosAnnotationKClass = isos::class
val isosAnnotationClass = isosAnnotationKClass.java
val isosAnnotationName = "@" + isosAnnotationClass.simpleName
val isosAnnotationTarget = "data class"
