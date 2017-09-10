package kategory.optics

import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

sealed class AnnotatedLens {
    data class Element(val type: TypeElement, val properties: Collection<VariableElement>) : AnnotatedLens()
    data class InvalidElement(val reason: String) : AnnotatedLens()
}