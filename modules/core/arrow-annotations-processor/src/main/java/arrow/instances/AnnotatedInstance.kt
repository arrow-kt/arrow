package arrow.instances

import arrow.common.utils.AbstractProcessor
import arrow.common.utils.ClassOrPackageDataWrapper
import javax.lang.model.element.TypeElement

class AnnotatedInstance(
  val instance: TypeElement,
  val dataTypeInstance: ClassOrPackageDataWrapper.Class,
  val superTypes: List<ClassOrPackageDataWrapper.Class>,
  val processor: AbstractProcessor,
  val dataType: ClassOrPackageDataWrapper.Class,
  val typeClass: ClassOrPackageDataWrapper.Class)