package arrow.meta.encoding.instances

import aballano.kotlinmemoization.memoize
import arrow.common.utils.ClassOrPackageDataWrapper
import arrow.common.utils.ProcessorUtils
import arrow.common.utils.knownError
import arrow.meta.ast.PackageName
import arrow.meta.ast.Type
import arrow.meta.encoding.EncodingError
import arrow.meta.encoding.EncodingResult
import arrow.meta.encoding.MetaEncoder
import arrow.meta.encoding.MetaProcessor
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import me.eugeniomarletti.kotlin.metadata.jvm.getJvmConstructorSignature
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf as proto
import me.eugeniomarletti.kotlin.metadata.visibility
import javax.lang.model.element.*
import javax.lang.model.util.ElementFilter
import arrow.meta.ast.*
import arrow.meta.ast.Modifier
import me.eugeniomarletti.kotlin.metadata.isPrimary

class DefaultMetaEncoder(
  private val packageEncoder: MetaEncoder<PackageName>,
  private val typeEncoder: MetaEncoder<Type>,
) {



  override fun encode(element: Element): EncodingResult<Tree> =
    when (element.kind) {
      ElementKind.PACKAGE -> packageEncoder.encode(element)
      ElementKind.CLASS -> typeEncoder.encode(element)
      ElementKind.INTERFACE -> typeEncoder.encode(element)
      else -> EncodingResult.Failure(EncodingError.UnsupportedElementType("Not supported: ${element.kind}", element))
    }

  fun proto.Visibility.asModifier(): Modifier? =
    when (this) {
      proto.Visibility.INTERNAL -> Modifier.Internal
      proto.Visibility.PRIVATE -> Modifier.Private
      proto.Visibility.PROTECTED -> Modifier.Protected
      proto.Visibility.PUBLIC -> Modifier.Public
      proto.Visibility.PRIVATE_TO_THIS -> Modifier.Private
      proto.Visibility.LOCAL -> null
    }

  fun proto.Modality.asModifier(): Modifier =
    when (this) {
      proto.Modality.FINAL -> Modifier.Final
      proto.Modality.OPEN -> Modifier.Open
      proto.Modality.ABSTRACT -> Modifier.Abstract
      proto.Modality.SEALED -> Modifier.Sealed
    }

  fun ClassOrPackageDataWrapper.Class.nameOf(id: Int): String =
    nameResolver.getString(id)

  fun VariableElement.asParameter(owner: ClassOrPackageDataWrapper.Class): Parameter =
    Parameter(
      name = simpleName.toString(),
      type = this.as
    )

  fun ExecutableElement.asConstructor(typeElement: TypeElement): Pair<Boolean, Fun>? =
    typeElement.meta.constructorList.find {
      it.getJvmConstructorSignature(typeElement.meta.nameResolver, typeElement.meta.classProto.typeTable) == this.jvmMethodSignature
    }?.let {
      it.isPrimary to Fun(
        name = "constructor",
        modifiers = listOfNotNull(it.visibility?.asModifier()),
        parameters = this.parameters.map { it.asParameter(typeElement.meta) },
        returnType = null
      )
    }





}