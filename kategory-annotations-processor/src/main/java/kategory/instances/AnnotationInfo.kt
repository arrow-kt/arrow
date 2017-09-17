package kategory.instances

import kategory.instance

val instanceAnnotationKClass = instance::class
val instanceAnnotationClass = instanceAnnotationKClass.java
val instanceAnnotationName = "@" + instanceAnnotationClass.simpleName
