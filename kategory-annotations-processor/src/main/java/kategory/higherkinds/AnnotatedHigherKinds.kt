package kategory.higherkinds

import kategory.common.utils.ClassOrPackageDataWrapper
import javax.lang.model.element.TypeElement

class AnnotatedHigherKind(
        val classElement: TypeElement,
        val classOrPackageProto: ClassOrPackageDataWrapper)