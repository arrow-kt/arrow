package arrow.fold

import arrow.common.utils.ClassOrPackageDataWrapper
import javax.lang.model.element.TypeElement

data class AnnotatedFold(
  val type: TypeElement,
  val typeParams: List<String>,
  val classData: ClassOrPackageDataWrapper.Class,
  val targets: List<Variant>
)

data class Variant(val fullName: String, val typeParams: List<String>, val simpleName: String)
