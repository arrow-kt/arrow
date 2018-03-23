package arrow.tc

import arrow.common.utils.ClassOrPackageDataWrapper
import javax.lang.model.element.TypeElement

class AnnotatedTypeclass(
        val classElement: TypeElement,
        val classOrPackageProto: ClassOrPackageDataWrapper,
        val superTypes: List<ClassOrPackageDataWrapper.Class>,
        val diamondTypes: List<ClassOrPackageDataWrapper.Class>,
        val syntax : Boolean)