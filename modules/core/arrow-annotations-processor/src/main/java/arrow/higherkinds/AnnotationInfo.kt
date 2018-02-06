package arrow.higherkinds

import arrow.higherkind

val higherKindsAnnotationKClass = higherkind::class
val higherKindsAnnotationClass = higherKindsAnnotationKClass.java
val higherKindsAnnotationName = "@" + higherKindsAnnotationClass.simpleName
