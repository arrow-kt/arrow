package arrow.meta.typeclasses

import arrow.meta.extensions.CompilerContext
import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaComponentRegistrar
import arrow.meta.ir.functionAccess
import arrow.meta.qq.func
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.ir.expressions.mapValueParameters
import org.jetbrains.kotlin.ir.util.render
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtTypeElement
import org.jetbrains.kotlin.psi.psiUtil.getSuperNames
import org.jetbrains.kotlin.resolve.descriptorUtil.parents
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.isSubtypeOf

const val WithMarker = "`*`"
const val ExtensionAnnotation = "@extension"

val MetaComponentRegistrar.typeClasses: List<ExtensionPhase>
  get() =
    meta(
      func(
        match = {
          println("Considering function to replace: $text")
          valueParameters.any { it.defaultValue?.text == WithMarker }
        },
        map = { func ->
          println("Found function to replace: ${func.name}")
          val scopeNames = func.valueParameters.filter { it.defaultValue?.text == "`*`" }.map { it.name }
          listOf(
            """
              |$modality $visibility fun <$typeParameters> $receiver.$name($valueParameters): $returnType =
              |  ${scopeNames.fold(body.asString()) { acc, scope -> "$scope.run { $acc }" }}
              |"""
          )
        }
      ),
      functionAccess { expression ->
        if (expression.defaultValues().contains(WithMarker)) { // with marker should be replaced by implicit injection
          println("visitFunctionAccess: ${expression.render()}\n${expression.descriptor.findPsi()?.text}")
          expression.mapValueParameters { valueParameterDescriptor ->
            val extensionType = valueParameterDescriptor.type
            val typeClass = extensionType.typeClassDescriptor()
            val dataType = extensionType.dataTypeDescriptor()
            val typeClassPackage = typeClass.packageFragmentDescriptor()
            val extension = typeClassPackage.findExtensionProof(extensionType)
            extension ?: compilerContext.reportExtensionNotFound(extensionType)
            when (extension) {
              is FunctionDescriptor -> extension.irCall()
              is ClassDescriptor -> extension.irConstructorCall()
              is PropertyDescriptor -> extension.irGetterCall()
              else -> null
            }
          }
        } else null
      }
    )

private fun CompilerContext.reportExtensionNotFound(extensionType: KotlinType): Unit {
  messageCollector.report(
    CompilerMessageSeverity.ERROR,
    "extension not found for $extensionType",
    CompilerMessageLocation.create(null)
  )
}

private fun PackageFragmentDescriptor.findExtensionProof(extensionType: KotlinType): DeclarationDescriptor? =
  getMemberScope()
    .getContributedDescriptors()
    .find {
      val result = it is FunctionDescriptor && it.returnType?.isSubtypeOf(extensionType) == true ||
        it is ClassDescriptor && it.typeConstructor.supertypes.contains(extensionType) ||
        it is PropertyDescriptor && it.type.isSubtypeOf(extensionType)
      println("Considering: ${it.javaClass}, ${it.name}: $result")
      result
    }

private fun ClassifierDescriptor?.packageFragmentDescriptor(): PackageFragmentDescriptor =
  this?.parents?.first() as PackageFragmentDescriptor

private fun KotlinType.dataTypeDescriptor(): ClassifierDescriptor? =
  arguments[0].type.constructor.declarationDescriptor

private fun KotlinType.typeClassDescriptor(): ClassifierDescriptor? =
  constructor.declarationDescriptor

private fun KtClass.typeArgumentNames(): List<String> =
  typeClassTypeElement()?.typeArgumentsAsTypes?.map { it.text }.orEmpty()

private fun KtClass.typeClassTypeElement(): KtTypeElement? =
  getSuperTypeList()?.entries?.get(0)?.typeReference?.typeElement

private fun KtClass.typeClassName(): String =
  getSuperNames()[0]

private fun KtAnnotated.isExtension(): Boolean =
  annotationEntries.any { it.text == ExtensionAnnotation }

