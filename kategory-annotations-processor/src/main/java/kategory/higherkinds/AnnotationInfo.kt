package kategory.higherkinds

import kategory.higherkind

val higherKindsAnnotationKClass = higherkind::class
val higherKindsAnnotationClass = higherKindsAnnotationKClass.java
val higherKindsAnnotationName = "@" + higherKindsAnnotationClass.simpleName
