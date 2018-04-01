package arrow.instances

import arrow.common.utils.ClassOrPackageDataWrapper
import javax.lang.model.element.TypeElement

class AnnotatedInstance(
  val classElement: TypeElement,
  val classOrPackageProto: ClassOrPackageDataWrapper.Class,
  val superTypes: List<ClassOrPackageDataWrapper.Class>,
  val processor: InstanceProcessor,
  val dataType: ClassOrPackageDataWrapper.Class)