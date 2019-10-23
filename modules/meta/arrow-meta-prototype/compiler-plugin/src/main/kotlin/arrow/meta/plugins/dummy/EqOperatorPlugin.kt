package arrow.meta.plugins.dummy

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.codegen.ir.IrUtils
import arrow.meta.plugins.typeclasses.ExtensionAnnotation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptorWithVisibility
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.resolveClassByFqName
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.descriptors.IrSimpleBuiltinOperatorDescriptorImpl
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.referenceFunction
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi2ir.findFirstFunction
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.lazy.descriptors.findPackageFragmentForFile
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.replace
import org.jetbrains.kotlin.types.typeUtil.asTypeProjection
import org.jetbrains.kotlin.types.typeUtil.isSubtypeOf
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

const val EQEQ = "EQEQ"

val Meta.eq: Plugin
  get() =
    "Eq" {
      meta(
        /**
         * Need to create a new kotlintype that is of Eq with type arguments A for whatever A is in your comparison with  ==
         *
         * If the member descriptor fqNameSafe matches EQEQ, then:
         * 1. Find the type of the descriptor's arguments to use in new IrSimpleType
         * 2. Create IrSimpleType for Arrow.Eq
         * 3. Get the EQ descriptor from the external symbol table. Create a new descriptor with new IrType "Eq<A>"
         *    ^^ Wait, why is the EQ descriptor needed
         * 4. Replace descriptor of the current IrCall with new descriptor
         */
        irFunctionAccess {
          if (it.descriptor.name.asString().contains(EQEQ)) {
            it.descriptor.safeAs<IrSimpleBuiltinOperatorDescriptorImpl>()?.let { operator ->
              val first = operator.valueParameters[0]
              val eqTypeArg = first.type
              val module = it.descriptor.module
              val eqClassDescriptor = module.resolveClassByFqName(FqName("arrow.typeclasses.Eq"), NoLookupLocation.FROM_BACKEND)
              val extensionType = eqClassDescriptor?.defaultType?.replace(newArguments = listOf(eqTypeArg.asTypeProjection()))
              val typeClassFactory: DeclarationDescriptor? = extensionType?.let { type ->
                findExtension(type)
              } // <-- locates the Eq<Int> along with the type coming back
              // create the IR call
              val eqvIrCall: IrCall? = when (val extension = typeClassFactory) {
                is FunctionDescriptor -> extension.irCall()
                is ClassDescriptor -> {
                  val eqvIr = extension.unsubstitutedMemberScope
                    .findFirstFunction("eqv") { callableMemberDescriptor ->
                      // Most use cases in the Kotlin compiler seems to check for obvious things
                      // for the predicate parameter of ::findFirstFunction
                      callableMemberDescriptor.valueParameters.size == 2
                    }.irCall()
                  println(eqvIr.dump())
                  eqvIr
                }
                is PropertyDescriptor -> extension.irGetterCall()
                else -> null
              }
              println("call: $eqvIrCall")
            }
            println("Found EQEQ: ${it.descriptor.fqNameSafe}")
          }
          it
        }
      )
    }

fun CompilerContext.findExtension(extensionType: KotlinType): DeclarationDescriptor? {
  val typeClass = extensionType.typeClassDescriptor() // info about Eq<A>
  val dataType = extensionType.dataTypeDescriptor() // info about Int (the data type)
  val typeClassPackage = typeClass.packageFragmentDescriptor() // info about the package of Eq (the container)
  val dataTypePackage = dataType.packageFragmentDescriptor() // info about the package to indicate what's in the package
  // ^ tells you what will be used
  val internalPackages = modulePackages() // look in the local package, grabs the object to allow us to look
  val internalExtensions = internalPackages.extensions(extensionType)
  return if (dataTypePackage != null &&
    typeClassPackage != null) {
    if (
    // look for instances of those calls in the packages - for reporting possible internal conflicts
    // internalPackage extensions help keeps extensions from being exported; LOOKUP Set Coherence Problem
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

// implements the algorithm
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

// TODO find a way to structure this in a way that we can share code with some override behavior for this particular function
// the algorithm; you have the package, the data package, the type class etc.
// in this case, it matches the type and checks for the subtype
// create system of proofs
private fun PackageFragmentDescriptor.findExtensionProof(extensionType: KotlinType): List<DeclarationDescriptor> =
  getMemberScope() // in memory collection of objects
    .getContributedDescriptors()
    .filter {
      it is FunctionDescriptor && it.returnType != module.builtIns.nothingType ||
        it is ClassDescriptor && it.typeConstructor.parameters[0].typeConstructor == extensionType && it.typeConstructor.parameters[0].typeConstructor == extensionType ||
        it is PropertyDescriptor && it.type != module.builtIns.nothingType
    }

private fun ClassifierDescriptor?.packageFragmentDescriptor(): PackageFragmentDescriptor? =
  this?.findPsi()?.containingFile.safeAs<KtFile>()?.let { file -> this?.module?.findPackageFragmentForFile(file) }

private fun KotlinType.dataTypeDescriptor(): ClassifierDescriptor? =
  arguments[0].type.constructor.declarationDescriptor

private fun KotlinType.typeClassDescriptor(): ClassifierDescriptor? =
  constructor.declarationDescriptor


fun IrUtils.irCall(oldEqDescriptor: FunctionDescriptor, newEqDescriptor: FunctionDescriptor): IrCall {
  val irFunctionSymbol: IrFunctionSymbol = backendContext.ir.symbols.externalSymbolTable.referenceFunction(oldEqDescriptor)
  return IrCallImpl(
    startOffset = UNDEFINED_OFFSET,
    endOffset = UNDEFINED_OFFSET,
    type = irFunctionSymbol.owner.returnType,
    symbol = irFunctionSymbol,
    descriptor = newEqDescriptor,
    typeArgumentsCount = irFunctionSymbol.owner.descriptor.typeParameters.size,
    valueArgumentsCount = irFunctionSymbol.owner.descriptor.valueParameters.size
  )
}


