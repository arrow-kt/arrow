package arrow.higherkinds

import arrow.common.utils.ClassOrPackageDataWrapper
import javax.lang.model.element.TypeElement

class AnnotatedHigherKind(
  val classElement: TypeElement,
  val classOrPackageProto: ClassOrPackageDataWrapper
)
