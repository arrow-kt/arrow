package kategory.optics

import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

sealed class AnnotatedIso {
    data class Element(val type: TypeElement, val properties: Collection<VariableElement>) : AnnotatedIso()
    data class InvalidElement(val reason: String) : AnnotatedIso()
}