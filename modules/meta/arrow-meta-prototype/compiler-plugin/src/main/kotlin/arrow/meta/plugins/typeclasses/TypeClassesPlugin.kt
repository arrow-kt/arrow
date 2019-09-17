package arrow.meta.plugins.typeclasses

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.MetaComponentRegistrar
import arrow.meta.phases.codegen.ir.IrUtils
import arrow.meta.quotes.func
import arrow.meta.quotes.get
import arrow.meta.quotes.ktFile
import arrow.meta.dsl.platform.ide
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptorWithVisibility
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.ReceiverParameterDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.AnonymousFunctionDescriptor
import org.jetbrains.kotlin.descriptors.impl.ReceiverParameterDescriptorImpl
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.expressions.mapValueParameters
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtDeclarationWithBody
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.psiUtil.blockExpressionsOrSingle
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.FunctionDescriptorUtil
import org.jetbrains.kotlin.resolve.OverloadChecker
import org.jetbrains.kotlin.resolve.StatementFilter
import org.jetbrains.kotlin.resolve.calls.results.TypeSpecificityComparator
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowInfo
import org.jetbrains.kotlin.resolve.calls.smartcasts.ImplicitSmartCasts
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.lazy.ResolveSession
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyClassDescriptor
import org.jetbrains.kotlin.resolve.lazy.descriptors.findPackageFragmentForFile
import org.jetbrains.kotlin.resolve.scopes.LexicalScope
import org.jetbrains.kotlin.resolve.scopes.LexicalScopeImpl
import org.jetbrains.kotlin.resolve.scopes.LexicalScopeKind
import org.jetbrains.kotlin.resolve.scopes.receivers.ExpressionReceiver
import org.jetbrains.kotlin.resolve.scopes.receivers.ExtensionReceiver
import org.jetbrains.kotlin.types.IntersectionTypeConstructor
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.KotlinTypeFactory
import org.jetbrains.kotlin.types.SimpleType
import org.jetbrains.kotlin.types.typeUtil.isSubtypeOf
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

const val WithMarker = "`*`"
val ExtensionAnnotation = FqName("arrow.extension")

val MetaComponentRegistrar.typeClasses: Pair<Name, List<ExtensionPhase>>
  get() =
    Name.identifier("typeClasses") to
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
        ideSyntheticBodyResolution(),
        irFunctionAccess { mapValueParameterExtensions(it) }
      )

private fun MetaComponentRegistrar.ideSyntheticBodyResolution(): ExtensionPhase = ide {
  syntheticResolver(
    generateSyntheticMethods = { thisDescriptor, name, bindingContext, fromSupertypes, result ->
      thisDescriptor.safeAs<LazyClassDescriptor>()?.let { lazyDescriptor ->
        val lazyClassContext: Any = lazyDescriptor["c"]
        lazyClassContext.safeAs<ResolveSession>()?.let { session ->
          result.firstOrNull { it.name == name }?.let { function ->
            resolveBodyWithExtensionsScope(session, function)
          }
          println("typeclasses.syntheticResolver.generateSyntheticMethods: $thisDescriptor, $name, $fromSupertypes, $result")
        }
      }
    }
  )
} ?: ExtensionPhase.Empty


private fun CompilerContext.resolveBodyWithExtensionsScope(session: ResolveSession, function: SimpleFunctionDescriptor): Unit {
  function.findPsi().safeAs<KtDeclarationWithBody>()?.let { ktCallable ->
    val functionScope = session.declarationScopeProvider.getResolutionScopeForDeclaration(ktCallable)
    val innerScope = FunctionDescriptorUtil.getFunctionInnerScope(functionScope, function, session.trace, OverloadChecker(TypeSpecificityComparator.NONE))
    val bodyResolver = analyzer?.createBodyResolver(
      session, session.trace, ktCallable.containingKtFile, StatementFilter.NONE
    )
    function.extensionReceiverParameter?.let { originalReceiver ->
      val intersectedTypes = function.valueParameters.filter { it.shouldEnhanceScope() }.map { it.type }
      val modifiedScope = function.valueParameters.scopeForExtensionParameters(innerScope)
      val typeIntersection =
        if (intersectedTypes.isNotEmpty()) {
          val constructor = IntersectionTypeConstructor(intersectedTypes)
          KotlinTypeFactory.simpleTypeWithNonTrivialMemberScope(
            Annotations.EMPTY,
            constructor,
            emptyList(),
            false,
            constructor.createScopeForKotlinType()
          )
        } else null
      typeIntersection?.let { uberExtendedType ->
        //function.applySmartCast(originalReceiver, uberExtendedType, ktCallable, session)
        bodyResolver?.resolveFunctionBody(DataFlowInfo.EMPTY, session.trace, ktCallable, function, modifiedScope)
      }
    }
  }
}

private fun SimpleFunctionDescriptor.applySmartCast(
  originalReceiver: ReceiverParameterDescriptor,
  uberExtendedType: SimpleType,
  ktCallable: KtDeclarationWithBody,
  session: ResolveSession
) {
  val extensionReceiver = ExtensionReceiver(this, originalReceiver.type, null)
  val smartCast = ImplicitSmartCasts(extensionReceiver, uberExtendedType)
  ktCallable.blockExpressionsOrSingle().filterIsInstance<KtExpression>().firstOrNull()?.let { expression ->
    session.trace.record(BindingContext.IMPLICIT_RECEIVER_SMARTCAST, expression, smartCast)
    ExpressionReceiver.create(expression, uberExtendedType, session.trace.bindingContext)
  }
}

private fun List<ValueParameterDescriptor>.scopeForExtensionParameters(innerScope: LexicalScope): LexicalScope =
  fold(innerScope) { currentScope, valueParameterDescriptor ->
    val ownerDescriptor = AnonymousFunctionDescriptor(valueParameterDescriptor, valueParameterDescriptor.annotations, CallableMemberDescriptor.Kind.DECLARATION, valueParameterDescriptor.source, false)
    val extensionReceiver = ExtensionReceiver(ownerDescriptor, valueParameterDescriptor.type, null)
    val extensionReceiverParamDescriptor = ReceiverParameterDescriptorImpl(ownerDescriptor, extensionReceiver, ownerDescriptor.annotations)
    ownerDescriptor.initialize(extensionReceiverParamDescriptor, null, valueParameterDescriptor.typeParameters, valueParameterDescriptor.valueParameters, valueParameterDescriptor.returnType, Modality.FINAL, valueParameterDescriptor.visibility)
    LexicalScopeImpl(currentScope, ownerDescriptor, true, extensionReceiverParamDescriptor, LexicalScopeKind.FUNCTION_INNER_SCOPE)
  }

private fun ValueParameterDescriptor.shouldEnhanceScope(): Boolean {
  val ktParameter = findPsi().safeAs<KtParameter>()
  return ktParameter?.hasExtensionDefaultValue() == true
}

private fun List<String?>.run(body: Name): String =
  fold(body.asString()) { acc, scope -> "$scope.run { $acc }" }

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
  this?.ktFile()?.let { file -> this.module.findPackageFragmentForFile(file) }

private fun KotlinType.dataTypeDescriptor(): ClassifierDescriptor? =
  arguments[0].type.constructor.declarationDescriptor

private fun KotlinType.typeClassDescriptor(): ClassifierDescriptor? =
  constructor.declarationDescriptor

