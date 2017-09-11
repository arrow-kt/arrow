package kategory.optics

import kategory.common.utils.ClassOrPackageDataWrapper
import javax.lang.model.element.TypeElement

data class AnnotatedLens(val type: TypeElement, val classData: ClassOrPackageDataWrapper.Class, val properties: List<Variable>)
data class AnnotatedPrism(val type: TypeElement, val subTypes: List<TypeElement>)
data class AnnotatedIso(val type: TypeElement, val classData: ClassOrPackageDataWrapper.Class, val properties: List<Variable>)
data class Variable(val fullName: String, val paramName: String)
