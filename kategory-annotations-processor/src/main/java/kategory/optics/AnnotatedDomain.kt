package kategory.optics

import kategory.common.utils.ClassOrPackageDataWrapper
import javax.lang.model.element.TypeElement

data class AnnotatedElement(val type: TypeElement, val classData: ClassOrPackageDataWrapper.Class, val targets: List<Target>)
data class Target(val fullName: String, val paramName: String)
