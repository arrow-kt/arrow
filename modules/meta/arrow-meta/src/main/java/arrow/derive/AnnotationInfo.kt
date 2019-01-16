@file:Suppress("DEPRECATION")

package arrow.derive

import arrow.deriving

val derivingAnnotationKClass = deriving::class
val derivingAnnotationClass = derivingAnnotationKClass.java
val derivingAnnotationName = "@" + derivingAnnotationClass.simpleName