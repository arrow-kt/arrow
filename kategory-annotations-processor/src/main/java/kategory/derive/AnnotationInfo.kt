package kategory.derive

import kategory.deriving

val derivingAnnotationKClass = deriving::class
val derivingAnnotationClass = derivingAnnotationKClass.java
val derivingAnnotationName = "@" + derivingAnnotationClass.simpleName