package kategory.instances

import kategory.common.utils.ClassOrPackageDataWrapper
import javax.lang.model.element.TypeElement

class AnnotatedInstance(
        val classElement: TypeElement,
        val classOrPackageProto: ClassOrPackageDataWrapper,
        val superTypes: List<ClassOrPackageDataWrapper>,
        val processor: InstanceProcessor)