package arrow.meta.extensions

import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.backend.common.BackendContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.codegen.ClassBuilderFactory
import org.jetbrains.kotlin.codegen.ImplementationBodyCodegen
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.psi.JavaPsiFacade
import org.jetbrains.kotlin.com.intellij.psi.PsiElementFactory
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.container.ComponentProvider
import org.jetbrains.kotlin.context.ProjectContext
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentProvider
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.DiagnosticSink
import org.jetbrains.kotlin.incremental.components.LookupTracker
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtModifierListOwner
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.lazy.LazyClassContext
import org.jetbrains.kotlin.resolve.lazy.declarations.ClassMemberDeclarationProvider
import org.jetbrains.kotlin.resolve.lazy.declarations.PackageMemberDeclarationProvider
import org.jetbrains.kotlin.resolve.scopes.SyntheticScope
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.synthetic.JavaSyntheticPropertiesScope
import org.jetbrains.kotlin.types.KotlinType
import java.util.*
import java.util.concurrent.ConcurrentHashMap

interface Config

interface ExtensionPhase {

  interface Config : ExtensionPhase {
    fun CompilerContext.updateConfiguration(configuration: CompilerConfiguration): Unit
  }

  interface PackageProvider : ExtensionPhase {
    fun CompilerContext.getPackageFragmentProvider(
      project: Project,
      module: ModuleDescriptor,
      storageManager: StorageManager,
      trace: BindingTrace,
      moduleInfo: ModuleInfo?,
      lookupTracker: LookupTracker
    ): PackageFragmentProvider?
  }

  interface AnalysisHandler : ExtensionPhase {
    fun CompilerContext.doAnalysis(
      project: Project,
      module: ModuleDescriptor,
      projectContext: ProjectContext,
      files: Collection<KtFile>,
      bindingTrace: BindingTrace,
      componentProvider: ComponentProvider
    ): AnalysisResult?

    fun CompilerContext.analysisCompleted(
      project: Project,
      module: ModuleDescriptor,
      bindingTrace: BindingTrace,
      files: Collection<KtFile>
    ): AnalysisResult?
  }


  interface SyntheticResolver : ExtensionPhase {
    fun CompilerContext.addSyntheticSupertypes(
      thisDescriptor: ClassDescriptor,
      supertypes: MutableList<KotlinType>
    ): Unit

    fun CompilerContext.generateSyntheticClasses(
      thisDescriptor: ClassDescriptor,
      name: Name,
      ctx: LazyClassContext,
      declarationProvider: ClassMemberDeclarationProvider,
      result: MutableSet<ClassDescriptor>
    ): Unit

    fun CompilerContext.generatePackageSyntheticClasses(
      thisDescriptor: PackageFragmentDescriptor,
      name: Name,
      ctx: LazyClassContext,
      declarationProvider: PackageMemberDeclarationProvider,
      result: MutableSet<ClassDescriptor>
    ): Unit

    fun CompilerContext.generateSyntheticMethods(
      thisDescriptor: ClassDescriptor,
      name: Name,
      bindingContext: BindingContext,
      fromSupertypes: List<SimpleFunctionDescriptor>,
      result: MutableCollection<SimpleFunctionDescriptor>
    ): Unit

    fun CompilerContext.generateSyntheticProperties(
      thisDescriptor: ClassDescriptor,
      name: Name,
      bindingContext: BindingContext,
      fromSupertypes: ArrayList<PropertyDescriptor>,
      result: MutableSet<PropertyDescriptor>
    ): Unit

    fun CompilerContext.getSyntheticCompanionObjectNameIfNeeded(thisDescriptor: ClassDescriptor): Name?

    fun CompilerContext.getSyntheticFunctionNames(thisDescriptor: ClassDescriptor): List<Name>

    fun CompilerContext.getSyntheticNestedClassNames(thisDescriptor: ClassDescriptor): List<Name>
  }

  interface DeclarationAttributeAlterer : ExtensionPhase {
    fun CompilerContext.refineDeclarationModality(
      modifierListOwner: KtModifierListOwner,
      declaration: DeclarationDescriptor?,
      containingDeclaration: DeclarationDescriptor?,
      currentModality: Modality,
      bindingContext: BindingContext,
      isImplicitModality: Boolean
    ): Modality?
  }

  interface StorageComponentContainer : ExtensionPhase {
    fun CompilerContext.check(
      declaration: KtDeclaration,
      descriptor: DeclarationDescriptor,
      context: DeclarationCheckerContext
    ): Unit
  }

  interface ClassBuilder : ExtensionPhase {
    fun CompilerContext.interceptClassBuilder(
      interceptedFactory: ClassBuilderFactory,
      bindingContext: BindingContext,
      diagnostics: DiagnosticSink
    ): ClassBuilderFactory
  }

  interface Codegen : ExtensionPhase {
    fun CompilerContext.applyFunction(
      receiver: StackValue,
      resolvedCall: ResolvedCall<*>,
      c: ExpressionCodegenExtension.Context
    ): StackValue?

    fun CompilerContext.applyProperty(
      receiver: StackValue,
      resolvedCall: ResolvedCall<*>,
      c: ExpressionCodegenExtension.Context
    ): StackValue?

    fun CompilerContext.generateClassSyntheticParts(codegen: ImplementationBodyCodegen): Unit
  }

  interface IRGeneration : ExtensionPhase {

    fun CompilerContext.generate(
      file: IrFile,
      backendContext: BackendContext,
      bindingContext: BindingContext
    )

  }

  interface SyntheticScopeProvider: ExtensionPhase {
    fun CompilerContext.getSyntheticScopes(
      moduleDescriptor: ModuleDescriptor,
      javaSyntheticPropertiesScope: JavaSyntheticPropertiesScope
    ): List<SyntheticScope>
  }

  interface DiagnosticsSuppressor: ExtensionPhase {
    fun CompilerContext.isSuppressed(diagnostic: Diagnostic): Boolean
  }
}

class CompilerContext(
  val project: MockProject,
  val messageCollector: MessageCollector,
  val elementFactory: PsiElementFactory = JavaPsiFacade.getInstance(project).elementFactory
) {
  val ctx: CompilerContext = this
  lateinit var module: ModuleDescriptor
  lateinit var projectContext: ProjectContext
  lateinit var files: Collection<KtFile>
  lateinit var bindingTrace: BindingTrace
  lateinit var componentProvider: ComponentProvider

  private val descriptorPhaseState = ConcurrentHashMap<FqName, ClassDescriptor>()

  fun storeDescriptor(descriptor: ClassDescriptor): Unit {
    descriptorPhaseState[descriptor.fqNameSafe] = descriptor
  }

  fun getStoredDescriptor(fqName: FqName): ClassDescriptor? =
    descriptorPhaseState[fqName]

  fun storedDescriptors(): List<ClassDescriptor> =
    descriptorPhaseState.values.toList()
}
