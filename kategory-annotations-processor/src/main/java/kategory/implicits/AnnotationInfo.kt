package kategory.implicits

import kategory.implicit

val implicitAnnotationKClass = implicit::class
val implicitAnnotationClass = implicitAnnotationKClass.java
val implicitAnnotationName = "@" + implicitAnnotationClass.simpleName
