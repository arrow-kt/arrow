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

sealed class Target {

    companion object {
        operator fun invoke(fullName: String, paramName: String): Target = when {
            fullName.endsWith("?") -> NullableTarget(fullName, paramName)
            fullName.startsWith("`arrow`.`core`.`Option`") -> OptionTarget(fullName, paramName)
            else -> NonNullTarget(fullName, paramName)
        }
    }

    abstract val fullName: String
    abstract val paramName: String

    data class NullableTarget(override val fullName: String, override val paramName: String) : Target() {
        val nonNullFullName = fullName.dropLast(1)
    }

    data class OptionTarget(override val fullName: String, override val paramName: String) : Target() {
        val nestedFullName = Regex("`arrow`.`core`.`Option`<(.*)>$").matchEntire(fullName)!!.groupValues[1]
    }

    data class NonNullTarget(override val fullName: String, override val paramName: String) : Target()

}
