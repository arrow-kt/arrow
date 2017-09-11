package kategory.optics

import kategory.common.utils.ClassOrPackageDataWrapper
import javax.lang.model.element.TypeElement

sealed class AnnotatedLens {
    data class Element(val type: TypeElement, val properties: List<Variable>) : AnnotatedLens()
    data class InvalidElement(val reason: String) : AnnotatedLens()
}

sealed class AnnotatedPrism {
    data class Element(val type: TypeElement, val subTypes: List<TypeElement>) : AnnotatedPrism()
    data class InvalidElement(val reason: String) : AnnotatedPrism()
}

sealed class AnnotatedIso {
    data class Element(val type: TypeElement, val classData: ClassOrPackageDataWrapper.Class, val properties: List<Variable>) : AnnotatedIso()
    data class InvalidElement(val reason: String) : AnnotatedIso()
}

data class Variable(val fullName: String, val paramName: String)