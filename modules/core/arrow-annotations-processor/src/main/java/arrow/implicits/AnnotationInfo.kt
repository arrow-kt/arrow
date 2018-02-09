package arrow.implicits

import arrow.implicit

val implicitAnnotationKClass = implicit::class
val implicitAnnotationClass = implicitAnnotationKClass.java
val implicitAnnotationName = "@" + implicitAnnotationClass.simpleName
