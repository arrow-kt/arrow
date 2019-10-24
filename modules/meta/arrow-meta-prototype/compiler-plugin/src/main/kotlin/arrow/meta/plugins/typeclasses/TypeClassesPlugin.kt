package arrow.meta.plugins.typeclasses

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.codegen.ir.IrUtils
import arrow.meta.quotes.FunctionBodyScope
import arrow.meta.quotes.Transform
import arrow.meta.quotes.func
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptorWithVisibility
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.expressions.mapValueParameters
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.lazy.descriptors.findPackageFragmentForFile
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.isSubtypeOf
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

const val WithMarker = "given"
val ExtensionAnnotation = FqName("arrow.extension")

val Meta.typeClasses: Plugin
  get() =
    "typeClasses" {
      meta(
        enableIr(),
        func(
          match = { hasExtensionValueParameters() },
          map = { func ->
            println("intercepting function for typeclass: ${func.text}")
            val result =
              """
              |$modality $visibility fun $`(typeParameters)` $receiver $name $`(valueParameters)` $returnType =
              |  ${func.extensionValueParamNames().run(body)}
              |""".function
            println("result: $result")
            Transform.replace(func,
                result
            )
          }
        ),
        irFunctionAccess { mapValueParameterExtensions(it) }
      )
    }

private fun List<String?>.run(body: FunctionBodyScope?): String =
  if (body != null)
    fold(body.toString()) { acc, scope -> "$scope.run { $acc }" }
  else ""


private fun KtCallableDeclaration.extensionValueParamNames() =
  valueParameters.filter { it.defaultValue?.text == WithMarker }.map { it.name }

private fun KtCallableDeclaration.hasExtensionValueParameters(): Boolean =
  valueParameters.any { it.hasExtensionDefaultValue() }

fun KtParameter.hasExtensionDefaultValue(): Boolean = defaultValue?.text == WithMarker

private fun IrUtils.mapValueParameterExtensions(expression: IrFunctionAccessExpression): IrFunctionAccessExpression? =
  if (expression.defaultValues().contains(WithMarker)) {
    expression.mapValueParameters { extensionCall(it) }
  } else null

private fun IrUtils.extensionCall(valueParameterDescriptor: ValueParameterDescriptor): IrFunctionAccessExpression? =
  when (val extension = compilerContext.findExtension(valueParameterDescriptor)) {
    is FunctionDescriptor -> extension.irCall()
    is ClassDescriptor -> extension.irConstructorCall()
    is PropertyDescriptor -> extension.irGetterCall()
    else -> null
  }

private fun CompilerContext.findExtension(valueParameterDescriptor: ValueParameterDescriptor): DeclarationDescriptor? {
  val extensionType = valueParameterDescriptor.type
  val typeClass = extensionType.typeClassDescriptor()
  val dataType = extensionType.dataTypeDescriptor()
  val typeClassPackage = typeClass.packageFragmentDescriptor()
  val dataTypePackage = dataType.packageFragmentDescriptor()
  val internalPackages = modulePackages()
  val internalExtensions = internalPackages.extensions(extensionType)
  return if (dataTypePackage != null &&
    typeClassPackage != null) {
    if (
      internalPackages.extensionsAreInternal(typeClassPackage, dataTypePackage, internalExtensions)) {
      reportNonInternalOrphanExtension(extensionType, internalExtensions[0])
    }
    val extensions = extensionType.resolveExtensions(internalExtensions, typeClassPackage, dataTypePackage)
    when {
      extensions.isEmpty() -> {
        reportExtensionNotFound(extensionType)
        null
      }
      extensions.size > 1 -> {
        reportAmbiguousExtensions(extensionType, extensions)
        null
      }
      else -> extensions[0]
    }
  } else null
}

private fun List<PackageFragmentDescriptor>.extensions(extensionType: KotlinType): List<DeclarationDescriptor> =
  flatMap {
    it.findExtensionProof(extensionType)
  }.distinctBy { it.name }

private fun CompilerContext.modulePackages(): List<PackageFragmentDescriptor> =
  files.toSet().flatMap { module.getPackage(it.packageFqName).fragments }

private fun KotlinType.resolveExtensions(
  internalExtensions: List<DeclarationDescriptor>,
  typeClassPackage: PackageFragmentDescriptor,
  dataTypePackage: PackageFragmentDescriptor
) =
  if (internalExtensions.isNotEmpty()) internalExtensions else {
    val typeClassExtensions = typeClassPackage.findExtensionProof(this)
    if (typeClassExtensions.isNotEmpty()) typeClassExtensions
    else dataTypePackage.findExtensionProof(this)
  }

private fun List<PackageFragmentDescriptor>.extensionsAreInternal(
  typeClassPackage: PackageFragmentDescriptor,
  dataTypePackage: PackageFragmentDescriptor,
  internalExtensions: List<DeclarationDescriptor>
): Boolean = (all { it != typeClassPackage && it != dataTypePackage } && //not the type class or data type module
  internalExtensions.isNotEmpty()
  && !internalExtensions.markedInternal())

private fun List<DeclarationDescriptor>.markedInternal(): Boolean =
  isNotEmpty() && when (val extension = this[0]) {
    is DeclarationDescriptorWithVisibility -> extension.visibility == Visibilities.INTERNAL
    else -> false
  }

private fun CompilerContext.reportNonInternalOrphanExtension(
  extensionType: KotlinType,
  extension: DeclarationDescriptor
) {
  messageCollector?.report(
    CompilerMessageSeverity.ERROR,
    """Orphan Extension $extension for $extensionType must be `internal`:
      |```kotlin
      |${extension.findPsi()?.text?.replaceFirst("@extension", "@extension internal")}
      |```
    """.trimMargin(),
    CompilerMessageLocation.create(null)
  )
}

private fun CompilerContext.reportExtensionNotFound(extensionType: KotlinType): Unit {
  messageCollector?.report(
    CompilerMessageSeverity.ERROR,
    "extension not found for $extensionType",
    CompilerMessageLocation.create(null)
  )
}

private fun CompilerContext.reportAmbiguousExtensions(
  extensionType: KotlinType,
  descriptors: List<DeclarationDescriptor>
): Unit {
  messageCollector?.report(
    CompilerMessageSeverity.ERROR,
    """Expected a single @extension for [$extensionType] but found the following conflicting extensions: ${descriptors.joinToString() { it.fqNameSafe.asString() }}""".trimMargin(),
    CompilerMessageLocation.create(null)
  )
}

private fun PackageFragmentDescriptor.findExtensionProof(extensionType: KotlinType): List<DeclarationDescriptor> =
  getMemberScope()
    .getContributedDescriptors()
    .filter {
      it.annotations.findAnnotation(ExtensionAnnotation) != null &&
        it is FunctionDescriptor && it.returnType != module.builtIns.nothingType && it.returnType?.isSubtypeOf(extensionType) == true ||
        it is ClassDescriptor && it.typeConstructor.supertypes.contains(extensionType) ||
        it is PropertyDescriptor && it.type != module.builtIns.nothingType && it.type.isSubtypeOf(extensionType)
    }

private fun ClassifierDescriptor?.packageFragmentDescriptor(): PackageFragmentDescriptor? =
  this?.findPsi()?.containingFile.safeAs<KtFile>()?.let { file -> this?.module?.findPackageFragmentForFile(file) }

private fun KotlinType.dataTypeDescriptor(): ClassifierDescriptor? =
  arguments[0].type.constructor.declarationDescriptor

private fun KotlinType.typeClassDescriptor(): ClassifierDescriptor? =
  constructor.declarationDescriptor

