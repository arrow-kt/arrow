package kategory.derive

import kategory.common.utils.ClassOrPackageDataWrapper
import javax.lang.model.element.TypeElement

class AnnotatedDeriving(
        val classElement: TypeElement,
        val classOrPackageProto: ClassOrPackageDataWrapper,
        val companionClassProto: ClassOrPackageDataWrapper,
        val derivingTypeclasses: List<ClassOrPackageDataWrapper>,
        val typeclassSuperTypes: Map<ClassOrPackageDataWrapper.Class, List<ClassOrPackageDataWrapper>>)