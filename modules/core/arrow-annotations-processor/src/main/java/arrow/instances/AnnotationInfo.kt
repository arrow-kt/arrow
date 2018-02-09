package arrow.instances

import arrow.instance

val instanceAnnotationKClass = instance::class
val instanceAnnotationClass = instanceAnnotationKClass.java
val instanceAnnotationName = "@" + instanceAnnotationClass.simpleName
