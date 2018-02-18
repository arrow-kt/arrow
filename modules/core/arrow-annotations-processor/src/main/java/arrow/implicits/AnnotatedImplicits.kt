package arrow.implicits

import arrow.common.utils.ClassOrPackageDataWrapper
import org.jetbrains.kotlin.serialization.ProtoBuf
import javax.lang.model.element.TypeElement

sealed class AnnotatedImplicits {
    abstract val classElement: TypeElement
    abstract val classOrPackageProto: ClassOrPackageDataWrapper

    sealed class Consumer : AnnotatedImplicits() {
        data class ValueParameter(
            override val classElement: TypeElement,
            override val classOrPackageProto: ClassOrPackageDataWrapper,
            val functionProto: ProtoBuf.Function,
            val valueParameterProto: ProtoBuf.ValueParameter
        ) : Consumer()
    }

    sealed class Provider : AnnotatedImplicits() {
        data class Function(
            override val classElement: TypeElement,
            override val classOrPackageProto: ClassOrPackageDataWrapper,
            val functionProto: ProtoBuf.Function
        ) : Provider()

        data class Property(
            override val classElement: TypeElement,
            override val classOrPackageProto: ClassOrPackageDataWrapper,
            val propertyProto: ProtoBuf.Property
        ) : Provider()
    }
}
