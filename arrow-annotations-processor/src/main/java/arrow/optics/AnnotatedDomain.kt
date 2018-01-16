package arrow.optics

import arrow.common.utils.ClassOrPackageDataWrapper
import arrow.common.utils.fullName
import me.eugeniomarletti.kotlin.metadata.escapedClassName
import javax.lang.model.element.TypeElement

data class AnnotatedOptic(val type: TypeElement, val classData: ClassOrPackageDataWrapper.Class, val targets: List<Target>) {
    val sourceClassName = classData.fullName.escapedClassName
    val sourceName = type.simpleName.toString().decapitalize()
    val targetNames = targets.map(Target::fullName)
    val hasTupleFocus: Boolean = targets.size > 1
    val focusSize: Int = targets.size
}
data class Target(val fullName: String, val paramName: String)
