package arrow.tc

import arrow.common.utils.ClassOrPackageDataWrapper
import javax.lang.model.element.TypeElement

class AnnotatedTypeclass(
        val classElement: TypeElement,
        val classOrPackageProto: ClassOrPackageDataWrapper)