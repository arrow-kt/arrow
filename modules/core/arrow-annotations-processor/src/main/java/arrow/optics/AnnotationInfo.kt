package arrow.optics

import arrow.bounded
import arrow.isos
import arrow.lenses
import arrow.optionals
import arrow.prisms

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

val optionalsAnnotationKClass = optionals::class
val optionalsAnnotationClass = optionalsAnnotationKClass.java
val optionalsAnnotationName = "@" + optionalsAnnotationClass.simpleName
val optionalsAnnotationTarget = "data class"

val boundAnnotationKClass = bounded::class
val boundAnnotationClass = boundAnnotationKClass.java
val boundAnnotationName = "@" + boundAnnotationClass.simpleName
val boundAnnotationTarget = "data class"