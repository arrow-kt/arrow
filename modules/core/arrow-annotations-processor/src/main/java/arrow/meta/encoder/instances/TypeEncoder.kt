package arrow.meta.encoder.instances

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.meta.ast.PackageName
import arrow.meta.ast.Type
import arrow.meta.ast.TypeName
import arrow.meta.encoder.EncodingError
import arrow.meta.encoder.MetaEncoder
import arrow.meta.processor.MetaProcessorUtils
import com.squareup.kotlinpoet.asTypeName
import me.eugeniomarletti.kotlin.metadata.jvm.getJvmConstructorSignature
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.type.NoType
import javax.lang.model.util.ElementFilter

interface TypeEncoder : MetaEncoder<Type>, MetaProcessorUtils {

  override fun encode(element: Element): Either<EncodingError, Type> {
    val encodingResult: Either<EncodingError, Type> =
      elementUtils.getPackageOf(element).let { pckg ->
        when (element.kind) {
          ElementKind.INTERFACE -> Type(PackageName(pckg.qualifiedName.toString()), element.asType().asTypeName().toMeta(), Type.Kind.Interface).right()
          ElementKind.CLASS -> {
            val typeElement = element as TypeElement
            val classBuilder = Type(PackageName(pckg.qualifiedName.toString()), element.asType().asTypeName().toMeta(), Type.Kind.Class)
            val declaredConstructorSignatures = element.meta.constructorList.map { it.getJvmConstructorSignature(element.meta.nameResolver, element.meta.classProto.typeTable) }
            val constructors = ElementFilter.constructorsIn(elementUtils.getAllMembers(element)).filter {
              declaredConstructorSignatures.contains(it.jvmMethodSignature)
            }.mapNotNull { it.asConstructor(element) }
            classBuilder.copy(
              primaryConstructor = constructors.find { it.first }?.second,
              superclass = if (typeElement.superclass is NoType) null else typeElement.superclass.asTypeName().toMeta()
            ).right()
          }
          else -> EncodingError.UnsupportedElementType("Unsupported ${this}, as (${element.kind}) to Type", element).left()
        }
      }
    return encodingResult.map {
      val typeElement = element as TypeElement
      it.copy(
        annotations = typeElement.annotations(),
        modifiers = typeElement.modifiers(),
        typeVariables = typeElement.typeVariables(),
        superInterfaces = typeElement.superInterfaces(),
        allFunctions = typeElement.allFunctions(),
        declaredFunctions = typeElement.declaredFunctions(),
        properties = typeElement.properties(),
        types = typeElement.sealedSubClassNames().mapNotNull {
          if(it is TypeName.Classy && it.simpleName == "Companion") null else it.asType(this)
        }
      )
    }
  }

}