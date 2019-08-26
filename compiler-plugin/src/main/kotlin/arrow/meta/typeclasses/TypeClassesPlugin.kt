package arrow.meta.typeclasses

import arrow.meta.extensions.CompilerContext
import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaComponentRegistrar
import arrow.meta.ir.IrUtils
import arrow.meta.ir.irFunctionAccess
import arrow.meta.qq.func
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
import org.jetbrains.kotlin.ir.backend.js.kotlinLibrary
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.expressions.mapValueParameters
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.descriptorUtil.parents
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.isSubtypeOf

const val WithMarker = "`*`"
val ExtensionAnnotation = FqName("arrow.extension")

val MetaComponentRegistrar.typeClasses: List<ExtensionPhase>
  get() =
    meta(
      func(
        match = { hasExtensionValueParameters() },
        map = { func ->
          listOf(
            """
              |$modality $visibility fun <$typeParameters> $receiver.$name($valueParameters): $returnType =
              |  ${func.extensionValueParamNames().run(body)}
              |"""
          )
        }
      ),
      irFunctionAccess { mapValueParameterExtensions(it) }
    )

private fun List<String?>.run(body: Name): String =
  fold(body.asString()) { acc, scope -> "$scope.run { $acc }" }

private fun KtFunction.extensionValueParamNames() =
  valueParameters.filter { it.defaultValue?.text == WithMarker }.map { it.name }

private fun KtFunction.hasExtensionValueParameters(): Boolean =
  valueParameters.any { it.defaultValue?.text == WithMarker }

private fun IrUtils.mapValueParameterExtensions(expression: IrFunctionAccessExpression): IrFunctionAccessExpression? =
  if (expression.defaultValues().contains(WithMarker)) {
    expression.mapValueParameters { extensionCall(it) }
  } else null

private fun IrUtils.extensionCall(valueParameterDescriptor: ValueParameterDescriptor): IrFunctionAccessExpression? =
  when (val extension = findExtension(valueParameterDescriptor)) {
    is FunctionDescriptor -> extension.irCall()
    is ClassDescriptor -> extension.irConstructorCall()
    is PropertyDescriptor -> extension.irGetterCall()
    else -> null
  }

private fun IrUtils.findExtension(valueParameterDescriptor: ValueParameterDescriptor): DeclarationDescriptor? {
  val extensionType = valueParameterDescriptor.type
  val typeClass = extensionType.typeClassDescriptor()
  val dataType = extensionType.dataTypeDescriptor()
  val typeClassPackage = typeClass.packageFragmentDescriptor()
  val dataTypePackage = dataType.packageFragmentDescriptor()
  val internalPackages = modulePackages()
  val internalExtensions = internalPackages.extensions(extensionType)
  if (
    internalPackages.extensionsAreInternal(typeClassPackage, dataTypePackage, internalExtensions)) {
    compilerContext.reportNonInternalOrphanExtension(extensionType, internalExtensions[0])
  }
  val extensions = extensionType.resolveExtensions(internalExtensions, typeClassPackage, dataTypePackage)

  return when {
    extensions.isEmpty() -> {
      compilerContext.reportExtensionNotFound(extensionType)
      null
    }
    extensions.size > 1 -> {
      compilerContext.reportAmbiguousExtensions(extensionType, extensions)
      null
    }
    else -> extensions[0]
  }
}

private fun List<PackageFragmentDescriptor>.extensions(extensionType: KotlinType): List<DeclarationDescriptor> =
  flatMap {
    it.findExtensionProof(extensionType)
  }.distinctBy { it.name }

private fun IrUtils.modulePackages(): List<PackageFragmentDescriptor> =
  compilerContext.files.toSet().flatMap { compilerContext.module.getPackage(it.packageFqName).fragments }

private fun KotlinType.resolveExtensions(
  internalExtensions: List<DeclarationDescriptor>,
  typeClassPackage: PackageFragmentDescriptor,
  dataTypePackage: PackageFragmentDescriptor
): List<DeclarationDescriptor> =
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
  messageCollector.report(
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
  messageCollector.report(
    CompilerMessageSeverity.ERROR,
    "extension not found for $extensionType",
    CompilerMessageLocation.create(null)
  )
}

private fun CompilerContext.reportAmbiguousExtensions(
  extensionType: KotlinType,
  descriptors: List<DeclarationDescriptor>
): Unit {
  messageCollector.report(
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

private fun ClassifierDescriptor?.packageFragmentDescriptor(): PackageFragmentDescriptor =
  this?.parents?.first() as PackageFragmentDescriptor

private fun KotlinType.dataTypeDescriptor(): ClassifierDescriptor? =
  arguments[0].type.constructor.declarationDescriptor

private fun KotlinType.typeClassDescriptor(): ClassifierDescriptor? =
  constructor.declarationDescriptor

