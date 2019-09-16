package arrow.meta

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.analysis.AnalysisHandler
import arrow.meta.phases.analysis.CollectAdditionalSources
import arrow.meta.phases.analysis.ExtraImports
import arrow.meta.phases.analysis.PreprocessedVirtualFileFactory
import arrow.meta.phases.codegen.asm.Codegen
import arrow.meta.phases.codegen.ir.IRGeneration
import arrow.meta.phases.config.Config
import arrow.meta.phases.resolve.DeclarationAttributeAlterer
import arrow.meta.phases.resolve.diagnostics.DiagnosticsSuppressor
import arrow.meta.phases.resolve.PackageProvider
import arrow.meta.phases.resolve.synthetics.SyntheticResolver
import arrow.meta.phases.resolve.synthetics.SyntheticScopeProvider
import arrow.meta.plugins.higherkind.KindAwareTypeChecker
import arrow.meta.utils.Noop
import arrow.meta.utils.cli
import arrow.meta.utils.ide
import arrow.meta.utils.setFinalStatic
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.backend.common.BackendContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.codegen.ClassBuilder
import org.jetbrains.kotlin.codegen.ClassBuilderFactory
import org.jetbrains.kotlin.codegen.DelegatingClassBuilder
import org.jetbrains.kotlin.codegen.ImplementationBodyCodegen
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.codegen.extensions.ClassBuilderInterceptorExtension
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.com.intellij.openapi.extensions.Extensions
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.container.ComponentProvider
import org.jetbrains.kotlin.container.StorageComponentContainer
import org.jetbrains.kotlin.container.useInstance
import org.jetbrains.kotlin.context.ProjectContext
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentProvider
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.DiagnosticSink
import org.jetbrains.kotlin.extensions.CollectAdditionalSourcesExtension
import org.jetbrains.kotlin.extensions.CompilerConfigurationExtension
import org.jetbrains.kotlin.extensions.DeclarationAttributeAltererExtension
import org.jetbrains.kotlin.extensions.PreprocessedVirtualFileFactoryExtension
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.incremental.components.LookupTracker
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportInfo
import org.jetbrains.kotlin.psi.KtModifierListOwner
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.checkers.DeclarationChecker
import org.jetbrains.kotlin.resolve.checkers.DeclarationCheckerContext
import org.jetbrains.kotlin.resolve.diagnostics.DiagnosticSuppressor
import org.jetbrains.kotlin.resolve.extensions.ExtraImportsProviderExtension
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension
import org.jetbrains.kotlin.resolve.jvm.extensions.PackageFragmentProviderExtension
import org.jetbrains.kotlin.resolve.lazy.LazyClassContext
import org.jetbrains.kotlin.resolve.lazy.declarations.ClassMemberDeclarationProvider
import org.jetbrains.kotlin.resolve.lazy.declarations.PackageMemberDeclarationProvider
import org.jetbrains.kotlin.resolve.scopes.ResolutionScope
import org.jetbrains.kotlin.resolve.scopes.SyntheticScope
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.synthetic.JavaSyntheticPropertiesScope
import org.jetbrains.kotlin.synthetic.SyntheticScopeProviderExtension
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker
import org.jetbrains.org.objectweb.asm.AnnotationVisitor
import org.jetbrains.org.objectweb.asm.FieldVisitor
import org.jetbrains.org.objectweb.asm.MethodVisitor
import java.util.*

interface MetaComponentRegistrar : ComponentRegistrar {

  fun intercept(): List<Pair<Name, List<ExtensionPhase>>>

  fun meta(vararg phases: ExtensionPhase): List<ExtensionPhase> =
    phases.toList()

  fun updateConfig(updateConfiguration: CompilerContext.(configuration: CompilerConfiguration) -> Unit): Config =
    object : Config {
      override fun CompilerContext.updateConfiguration(configuration: CompilerConfiguration) {
        updateConfiguration(configuration)
      }
    }

  fun additionalSources(
    collectAdditionalSourcesAndUpdateConfiguration: CompilerContext.(
      knownSources: Collection<KtFile>,
      configuration: CompilerConfiguration,
      project: Project
    ) -> Collection<KtFile>
  ): CollectAdditionalSources =
    object : CollectAdditionalSources {
      override fun CompilerContext.collectAdditionalSourcesAndUpdateConfiguration(knownSources: Collection<KtFile>, configuration: CompilerConfiguration, project: Project): Collection<KtFile> =
        collectAdditionalSourcesAndUpdateConfiguration(knownSources, configuration, project)
    }

  fun analysys(
    doAnalysis: CompilerContext.(
      project: Project,
      module: ModuleDescriptor,
      projectContext: ProjectContext,
      files: Collection<KtFile>,
      bindingTrace: BindingTrace,
      componentProvider: ComponentProvider
    ) -> AnalysisResult?,
    analysisCompleted: CompilerContext.(
      project: Project,
      module: ModuleDescriptor,
      bindingTrace: BindingTrace,
      files: Collection<KtFile>
    ) -> AnalysisResult?
  ): AnalysisHandler =
    object : AnalysisHandler {
      override fun CompilerContext.doAnalysis(
        project: Project,
        module: ModuleDescriptor,
        projectContext: ProjectContext,
        files: Collection<KtFile>,
        bindingTrace: BindingTrace,
        componentProvider: ComponentProvider
      ): AnalysisResult? {
        ctx.module = module
        ctx.files = files
        ctx.componentProvider = componentProvider
        return doAnalysis(project, module, projectContext, files, bindingTrace, componentProvider)
      }

      override fun CompilerContext.analysisCompleted(
        project: Project,
        module: ModuleDescriptor,
        bindingTrace: BindingTrace,
        files: Collection<KtFile>
      ): AnalysisResult? =
        analysisCompleted(project, module, bindingTrace, files)
    }

  fun storageComponent(
    registerModuleComponents: CompilerContext.(container: StorageComponentContainer, moduleDescriptor: ModuleDescriptor) -> Unit,
    check: CompilerContext.(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext) -> Unit
  ): arrow.meta.phases.analysis.StorageComponentContainer =
    object : arrow.meta.phases.analysis.StorageComponentContainer {
      override fun CompilerContext.check(
        declaration: KtDeclaration,
        descriptor: DeclarationDescriptor,
        context: DeclarationCheckerContext
      ) {
        check(declaration, descriptor, context)
      }

      override fun CompilerContext.registerModuleComponents(container: StorageComponentContainer, moduleDescriptor: ModuleDescriptor) {
        registerModuleComponents(container, moduleDescriptor)
      }
    }

  fun codegen(
    applyFunction: CompilerContext.(receiver: StackValue, resolvedCall: ResolvedCall<*>, c: ExpressionCodegenExtension.Context) -> StackValue?,
    applyProperty: CompilerContext.(receiver: StackValue, resolvedCall: ResolvedCall<*>, c: ExpressionCodegenExtension.Context) -> StackValue?,
    generateClassSyntheticParts: CompilerContext.(codegen: ImplementationBodyCodegen) -> Unit
  ): Codegen =
    object : Codegen {
      override fun CompilerContext.applyFunction(
        receiver: StackValue,
        resolvedCall: ResolvedCall<*>,
        c: ExpressionCodegenExtension.Context
      ): StackValue? =
        applyFunction(receiver, resolvedCall, c)

      override fun CompilerContext.applyProperty(
        receiver: StackValue,
        resolvedCall: ResolvedCall<*>,
        c: ExpressionCodegenExtension.Context
      ): StackValue? =
        applyProperty(receiver, resolvedCall, c)

      override fun CompilerContext.generateClassSyntheticParts(codegen: ImplementationBodyCodegen) =
        generateClassSyntheticParts(codegen)
    }

  fun IrGeneration(generate: (compilerContext: CompilerContext, file: IrFile, backendContext: BackendContext, bindingContext: BindingContext) -> Unit): IRGeneration =
    object : IRGeneration {
      override fun CompilerContext.generate(
        file: IrFile,
        backendContext: BackendContext,
        bindingContext: BindingContext
      ) {
        generate(this, file, backendContext, bindingContext)
      }
    }

  fun declarationAttributeAlterer(
    refineDeclarationModality: CompilerContext.(
      modifierListOwner: KtModifierListOwner,
      declaration: DeclarationDescriptor?,
      containingDeclaration: DeclarationDescriptor?,
      currentModality: Modality,
      bindingContext: BindingContext,
      isImplicitModality: Boolean
    ) -> Modality?
  ): DeclarationAttributeAlterer =
    object : DeclarationAttributeAlterer {
      override fun CompilerContext.refineDeclarationModality(
        modifierListOwner: KtModifierListOwner,
        declaration: DeclarationDescriptor?,
        containingDeclaration: DeclarationDescriptor?,
        currentModality: Modality,
        bindingContext: BindingContext,
        isImplicitModality: Boolean
      ): Modality? =
        refineDeclarationModality(
          modifierListOwner,
          declaration,
          containingDeclaration,
          currentModality,
          bindingContext,
          isImplicitModality
        )
    }

  fun extraImports(extraImports: (ktFile: KtFile) -> Collection<KtImportInfo>): ExtraImports =
    object : ExtraImports {
      override fun CompilerContext.extraImports(ktFile: KtFile): Collection<KtImportInfo> =
        extraImports(ktFile)
    }

  fun packageFragmentProvider(getPackageFragmentProvider: CompilerContext.(project: Project, module: ModuleDescriptor, storageManager: StorageManager, trace: BindingTrace, moduleInfo: ModuleInfo?, lookupTracker: LookupTracker) -> PackageFragmentProvider?): PackageProvider =
    object : PackageProvider {
      override fun CompilerContext.getPackageFragmentProvider(
        project: Project,
        module: ModuleDescriptor,
        storageManager: StorageManager,
        trace: BindingTrace,
        moduleInfo: ModuleInfo?,
        lookupTracker: LookupTracker
      ): PackageFragmentProvider? =
        getPackageFragmentProvider(
          project,
          module,
          storageManager,
          trace,
          moduleInfo,
          lookupTracker
        )
    }

  fun syntheticScopes(
    syntheticConstructor: CompilerContext.(constructor: ConstructorDescriptor) -> ConstructorDescriptor? = Noop.nullable2(),
    syntheticConstructors: CompilerContext.(scope: ResolutionScope) -> Collection<FunctionDescriptor> = Noop.emptyCollection2(),
    syntheticConstructorsForName: CompilerContext.(scope: ResolutionScope, name: Name, location: LookupLocation) -> Collection<FunctionDescriptor> = Noop.emptyCollection4(),
    syntheticExtensionProperties: CompilerContext.(receiverTypes: Collection<KotlinType>, location: LookupLocation) -> Collection<PropertyDescriptor> = Noop.emptyCollection3(),
    syntheticExtensionPropertiesForName: CompilerContext.(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation) -> Collection<PropertyDescriptor> = Noop.emptyCollection4(),
    syntheticMemberFunctions: CompilerContext.(receiverTypes: Collection<KotlinType>) -> Collection<FunctionDescriptor> = Noop.emptyCollection2(),
    syntheticMemberFunctionsForName: CompilerContext.(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation) -> Collection<FunctionDescriptor> = Noop.emptyCollection4(),
    syntheticStaticFunctions: CompilerContext.(scope: ResolutionScope) -> Collection<FunctionDescriptor> = Noop.emptyCollection2(),
    syntheticStaticFunctionsForName: CompilerContext.(scope: ResolutionScope, name: Name, location: LookupLocation) -> Collection<FunctionDescriptor> = Noop.emptyCollection4()
  ): ExtensionPhase =
    ide {
      object : SyntheticScopeProvider {
        override fun CompilerContext.syntheticConstructor(constructor: ConstructorDescriptor): ConstructorDescriptor? =
          syntheticConstructor(constructor)

        override fun CompilerContext.syntheticConstructors(scope: ResolutionScope): Collection<FunctionDescriptor> =
          syntheticConstructors(scope)

        override fun CompilerContext.syntheticConstructors(scope: ResolutionScope, name: Name, location: LookupLocation): Collection<FunctionDescriptor> =
          syntheticConstructorsForName(scope, name, location)

        override fun CompilerContext.syntheticExtensionProperties(receiverTypes: Collection<KotlinType>, location: LookupLocation): Collection<PropertyDescriptor> =
          syntheticExtensionProperties(receiverTypes, location)

        override fun CompilerContext.syntheticExtensionProperties(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation): Collection<PropertyDescriptor> =
          syntheticExtensionPropertiesForName(receiverTypes, name, location)

        override fun CompilerContext.syntheticMemberFunctions(receiverTypes: Collection<KotlinType>): Collection<FunctionDescriptor> =
          syntheticMemberFunctions(receiverTypes)

        override fun CompilerContext.syntheticMemberFunctions(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation): Collection<FunctionDescriptor> =
          syntheticMemberFunctionsForName(receiverTypes, name, location)

        override fun CompilerContext.syntheticStaticFunctions(scope: ResolutionScope): Collection<FunctionDescriptor> =
          syntheticStaticFunctions(scope)

        override fun CompilerContext.syntheticStaticFunctions(scope: ResolutionScope, name: Name, location: LookupLocation): Collection<FunctionDescriptor> =
          syntheticStaticFunctionsForName(scope, name, location)
      }
    } ?: ExtensionPhase.Empty

  fun preprocessedVirtualFileFactory(
    createPreprocessedFile: CompilerContext.(file: VirtualFile?) -> VirtualFile?,
    createPreprocessedLightFile: CompilerContext.(file: LightVirtualFile?) -> LightVirtualFile? = Noop.nullable2()
  ): PreprocessedVirtualFileFactory =
    object : PreprocessedVirtualFileFactory {
      override fun CompilerContext.isPassThrough(): Boolean = false

      override fun CompilerContext.createPreprocessedFile(file: VirtualFile?): VirtualFile? =
        createPreprocessedFile(file)

      override fun CompilerContext.createPreprocessedLightFile(file: LightVirtualFile?): LightVirtualFile? =
        createPreprocessedLightFile(file)
    }

  fun diagnosticsSuppressor(isSuppressed: CompilerContext.(diagnostic: Diagnostic) -> Boolean): DiagnosticsSuppressor =
    object : DiagnosticsSuppressor {
      override fun CompilerContext.isSuppressed(diagnostic: Diagnostic): Boolean =
        isSuppressed(diagnostic)
    }

  private fun registerKindAwareTypeChecker(): arrow.meta.phases.analysis.StorageComponentContainer =
    storageComponent(
      registerModuleComponents = { container, moduleDescriptor ->
        val defaultTypeChecker = KotlinTypeChecker.DEFAULT
        if (defaultTypeChecker !is KindAwareTypeChecker) { //nasty hack ahead to circumvent the ability to replace the Kotlin type checker
          val defaultTypeCheckerField = KotlinTypeChecker::class.java.getDeclaredField("DEFAULT")
          setFinalStatic(defaultTypeCheckerField, KindAwareTypeChecker(defaultTypeChecker))
        }
      },
      check = { _, _, _ ->
      }
    )

  fun compilerContextService(): arrow.meta.phases.analysis.StorageComponentContainer =
    storageComponent(
      registerModuleComponents = { container, moduleDescriptor ->
        container.useInstance(this)
      },
      check = { declaration, descriptor, context ->
      }
    )

  fun CompilerContext.registerIdeExclusivePhase(currentPhase: ExtensionPhase): Unit {}

  fun syntheticResolver(
    addSyntheticSupertypes: CompilerContext.(
      thisDescriptor: ClassDescriptor,
      supertypes: MutableList<KotlinType>
    ) -> Unit = Noop.effect3,
    /**
     * For a given package fragment it iterates over all the package declaration
     * allowing the user to contribute new synthetic declarations.
     * The result mutable set includes the descriptors as seen from the Kotlin compiler
     * initial analysis and allows us to mutate it to add new descriptor or change the existing ones
     */
    generatePackageSyntheticClasses: CompilerContext.(
      thisDescriptor: PackageFragmentDescriptor,
      name: Name,
      ctx: LazyClassContext,
      declarationProvider: PackageMemberDeclarationProvider,
      result: MutableSet<ClassDescriptor>
    ) -> Unit = Noop.effect6,
    generateSyntheticClasses: CompilerContext.(
      thisDescriptor: ClassDescriptor,
      name: Name,
      ctx: LazyClassContext,
      declarationProvider: ClassMemberDeclarationProvider,
      result: MutableSet<ClassDescriptor>
    ) -> Unit = Noop.effect6,
    generateSyntheticMethods: CompilerContext.(
      thisDescriptor: ClassDescriptor,
      name: Name,
      bindingContext: BindingContext,
      fromSupertypes: List<SimpleFunctionDescriptor>,
      result: MutableCollection<SimpleFunctionDescriptor>
    ) -> Unit = Noop.effect6,
    generateSyntheticProperties: CompilerContext.(
      thisDescriptor: ClassDescriptor,
      name: Name,
      bindingContext: BindingContext,
      fromSupertypes: ArrayList<PropertyDescriptor>,
      result: MutableSet<PropertyDescriptor>
    ) -> Unit = Noop.effect6,
    getSyntheticCompanionObjectNameIfNeeded: CompilerContext.(
      thisDescriptor: ClassDescriptor
    ) -> Name? = Noop.nullable2(),
    getSyntheticFunctionNames: CompilerContext.(
      thisDescriptor: ClassDescriptor
    ) -> List<Name>? = Noop.nullable2(),
    getSyntheticNestedClassNames: CompilerContext.(
      thisDescriptor: ClassDescriptor
    ) -> List<Name>? = Noop.nullable2()
  ): SyntheticResolver =
    object : SyntheticResolver {
      override fun CompilerContext.addSyntheticSupertypes(
        thisDescriptor: ClassDescriptor,
        supertypes: MutableList<KotlinType>
      ) {
        addSyntheticSupertypes(thisDescriptor, supertypes)
      }

      override fun CompilerContext.generateSyntheticClasses(
        thisDescriptor: ClassDescriptor,
        name: Name,
        ctx: LazyClassContext,
        declarationProvider: ClassMemberDeclarationProvider,
        result: MutableSet<ClassDescriptor>
      ) {
        generateSyntheticClasses(thisDescriptor, name, ctx, declarationProvider, result)
      }

      override fun CompilerContext.generatePackageSyntheticClasses(
        thisDescriptor: PackageFragmentDescriptor,
        name: Name,
        ctx: LazyClassContext,
        declarationProvider: PackageMemberDeclarationProvider,
        result: MutableSet<ClassDescriptor>
      ) {
        generatePackageSyntheticClasses(thisDescriptor, name, ctx, declarationProvider, result)
      }

      override fun CompilerContext.generateSyntheticMethods(
        thisDescriptor: ClassDescriptor,
        name: Name,
        bindingContext: BindingContext,
        fromSupertypes: List<SimpleFunctionDescriptor>,
        result: MutableCollection<SimpleFunctionDescriptor>
      ) {
        generateSyntheticMethods(thisDescriptor, name, bindingContext, fromSupertypes, result)
      }

      override fun CompilerContext.generateSyntheticProperties(
        thisDescriptor: ClassDescriptor,
        name: Name,
        bindingContext: BindingContext,
        fromSupertypes: ArrayList<PropertyDescriptor>,
        result: MutableSet<PropertyDescriptor>
      ) {
        generateSyntheticProperties(thisDescriptor, name, bindingContext, fromSupertypes, result)
      }

      override fun CompilerContext.getSyntheticCompanionObjectNameIfNeeded(thisDescriptor: ClassDescriptor): Name? =
        getSyntheticCompanionObjectNameIfNeeded(thisDescriptor)

      override fun CompilerContext.getSyntheticFunctionNames(thisDescriptor: ClassDescriptor): List<Name> =
        getSyntheticFunctionNames(thisDescriptor) ?: emptyList()

      override fun CompilerContext.getSyntheticNestedClassNames(thisDescriptor: ClassDescriptor): List<Name> =
        getSyntheticNestedClassNames(thisDescriptor) ?: emptyList()
    }

  fun enableIr(): ExtensionPhase =
    cli {
      updateConfig { configuration ->
        configuration.put(JVMConfigurationKeys.IR, true)
      }
    } ?: ExtensionPhase.Empty

  override fun registerProjectComponents(
    project: MockProject,
    configuration: CompilerConfiguration
  ) {
    ide {
      println("registerProjectComponents!!!! CALLED in IDEA!!!! something is wrong.")
    }
    registerMetaComponents(project, configuration)
  }

  fun registerMetaComponents(
    project: Project,
    configuration: CompilerConfiguration
  ) {
    println("Project allowed extensions: ${Extensions.getArea(project).extensionPoints.toList().joinToString("\n")}")
    cli {
      println("it's the CLI plugin")
    }
    ide {
      println("it's the IDEA plugin")
    }
    val messageCollector: MessageCollector? =
      cli { configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE) }

    val ctx = CompilerContext(project, messageCollector)
    registerPostAnalysisContextEnrichment(project, ctx)

    val initialPhases = listOf(Name.identifier("Initial setup") to meta(
      enableIr(),
      compilerContextService(),
      registerKindAwareTypeChecker(),
      registerMetaAnalyzer()
    ))
    (initialPhases + intercept()).forEach { (plugin, phases) ->
      println("Registering plugin: $plugin extensions: $phases")
      phases.forEach { currentPhase ->
        fun registerPhase(phase: ExtensionPhase): Unit {
          if (phase is ExtraImports) registerExtraImports(project, phase, ctx)
          if (phase is PreprocessedVirtualFileFactory) registerPreprocessedVirtualFileFactory(project, phase, ctx)
          if (phase is Config) registerCompilerConfiguration(project, phase, ctx)
          if (phase is arrow.meta.phases.analysis.StorageComponentContainer) registerStorageComponentContainer(project, phase, ctx)
          if (phase is CollectAdditionalSources) registerCollectAdditionalSources(project, phase, ctx)
          if (phase is AnalysisHandler) registerAnalysisHandler(project, phase, ctx)
          if (phase is arrow.meta.phases.codegen.asm.ClassBuilder) registerClassBuilder(project, phase, ctx)
          if (phase is Codegen) registerCodegen(project, phase, ctx)
          if (phase is DeclarationAttributeAlterer) registerDeclarationAttributeAlterer(project, phase, ctx)
          if (phase is PackageProvider) packageFragmentProvider(project, phase, ctx)
          if (phase is SyntheticResolver) registerSyntheticResolver(project, phase, ctx)
          if (phase is IRGeneration) registerIRGeneration(project, phase, ctx)
          if (phase is SyntheticScopeProvider) registerSyntheticScopeProvider(project, phase, ctx)
          if (phase is DiagnosticsSuppressor) registerDiagnosticSuppressor(project, phase, ctx)
          if (phase is Composite) phase.phases.map(::registerPhase)
        }
        registerPhase(currentPhase)
        ctx.registerIdeExclusivePhase(currentPhase)
      }
    }
  }

  fun registerMetaAnalyzer(): ExtensionPhase = ExtensionPhase.Empty

  fun registerDiagnosticSuppressor(
    project: Project,
    phase: DiagnosticsSuppressor,
    ctx: CompilerContext
  ) {
    Extensions.getArea(project).getExtensionPoint(DiagnosticSuppressor.EP_NAME)
      .registerExtension(object : DiagnosticSuppressor {
        override fun isSuppressed(diagnostic: Diagnostic): Boolean =
          phase.run { ctx.isSuppressed(diagnostic) }
      })
  }

  fun registerExtraImports(project: Project, phase: ExtraImports, ctx: CompilerContext) {
    ExtraImportsProviderExtension.registerExtension(project, object : ExtraImportsProviderExtension {
      override fun getExtraImports(ktFile: KtFile): Collection<KtImportInfo> =
        phase.run { ctx.extraImports(ktFile) }

    })
  }

  fun registerPreprocessedVirtualFileFactory(project: Project, phase: PreprocessedVirtualFileFactory, ctx: CompilerContext) {
    PreprocessedVirtualFileFactoryExtension.registerExtension(project, object : PreprocessedVirtualFileFactoryExtension {
      override fun createPreprocessedFile(file: VirtualFile?): VirtualFile? =
        phase.run { ctx.createPreprocessedFile(file) }

      override fun createPreprocessedLightFile(file: LightVirtualFile?): LightVirtualFile? =
        phase.run { ctx.createPreprocessedLightFile(file) }

      override fun isPassThrough(): Boolean =
        phase.run { ctx.isPassThrough() }
    })
  }

  fun registerSyntheticScopeProvider(project: Project, phase: SyntheticScopeProvider, ctx: CompilerContext) {
    SyntheticScopeProviderExtension.registerExtension(project, object : SyntheticScopeProviderExtension {
      override fun getScopes(moduleDescriptor: ModuleDescriptor, javaSyntheticPropertiesScope: JavaSyntheticPropertiesScope): List<SyntheticScope> =
        phase.run {
          listOf(
            object : SyntheticScope.Default() {
              override fun getSyntheticConstructor(constructor: ConstructorDescriptor): ConstructorDescriptor? =
                phase.run { ctx.syntheticConstructor(constructor) }

              override fun getSyntheticConstructors(scope: ResolutionScope): Collection<FunctionDescriptor> =
                phase.run { ctx.syntheticConstructors(scope) }

              override fun getSyntheticConstructors(scope: ResolutionScope, name: Name, location: LookupLocation): Collection<FunctionDescriptor> =
                phase.run { ctx.syntheticConstructors(scope, name, location) }

              override fun getSyntheticExtensionProperties(receiverTypes: Collection<KotlinType>, location: LookupLocation): Collection<PropertyDescriptor> =
                phase.run { ctx.syntheticExtensionProperties(receiverTypes, location) }

              override fun getSyntheticExtensionProperties(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation): Collection<PropertyDescriptor> =
                phase.run { ctx.syntheticExtensionProperties(receiverTypes, name, location) }

              override fun getSyntheticMemberFunctions(receiverTypes: Collection<KotlinType>): Collection<FunctionDescriptor> =
                phase.run { ctx.syntheticMemberFunctions(receiverTypes) }

              override fun getSyntheticMemberFunctions(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation): Collection<FunctionDescriptor> =
                phase.run { ctx.syntheticMemberFunctions(receiverTypes, name, location) }

              override fun getSyntheticStaticFunctions(scope: ResolutionScope): Collection<FunctionDescriptor> =
                phase.run { ctx.syntheticStaticFunctions(scope) }

              override fun getSyntheticStaticFunctions(scope: ResolutionScope, name: Name, location: LookupLocation): Collection<FunctionDescriptor> =
                phase.run { ctx.syntheticStaticFunctions(scope, name, location) }
            }
          )
        }
    })
  }

  fun registerIRGeneration(
    project: Project,
    phase: IRGeneration,
    compilerContext: CompilerContext
  ) {
    IrGenerationExtension.registerExtension(project, object : IrGenerationExtension {
      override fun generate(
        file: IrFile,
        backendContext: BackendContext,
        bindingContext: BindingContext
      ) {
        phase.run { compilerContext.generate(file, backendContext, bindingContext) }
      }
    })
  }

  fun registerSyntheticResolver(
    project: Project,
    phase: SyntheticResolver,
    compilerContext: CompilerContext
  ) {
    SyntheticResolveExtension.registerExtension(project, object : SyntheticResolveExtension {
      override fun addSyntheticSupertypes(thisDescriptor: ClassDescriptor, supertypes: MutableList<KotlinType>) {
        phase.run { compilerContext.addSyntheticSupertypes(thisDescriptor, supertypes) }
      }

      override fun generateSyntheticClasses(
        thisDescriptor: ClassDescriptor,
        name: Name,
        ctx: LazyClassContext,
        declarationProvider: ClassMemberDeclarationProvider,
        result: MutableSet<ClassDescriptor>
      ) {
        phase.run {
          compilerContext.generateSyntheticClasses(
            thisDescriptor,
            name,
            ctx,
            declarationProvider,
            result
          )
        }
      }

      override fun generateSyntheticClasses(
        thisDescriptor: PackageFragmentDescriptor,
        name: Name,
        ctx: LazyClassContext,
        declarationProvider: PackageMemberDeclarationProvider,
        result: MutableSet<ClassDescriptor>
      ) {
        phase.run {
          compilerContext.generatePackageSyntheticClasses(
            thisDescriptor,
            name,
            ctx,
            declarationProvider,
            result
          )
        }
      }

      override fun generateSyntheticMethods(
        thisDescriptor: ClassDescriptor,
        name: Name,
        bindingContext: BindingContext,
        fromSupertypes: List<SimpleFunctionDescriptor>,
        result: MutableCollection<SimpleFunctionDescriptor>
      ) {
        phase.run {
          compilerContext.generateSyntheticMethods(
            thisDescriptor,
            name,
            bindingContext,
            fromSupertypes,
            result
          )
        }
      }

      override fun generateSyntheticProperties(
        thisDescriptor: ClassDescriptor,
        name: Name,
        bindingContext: BindingContext,
        fromSupertypes: ArrayList<PropertyDescriptor>,
        result: MutableSet<PropertyDescriptor>
      ) {
        phase.run {
          compilerContext.generateSyntheticProperties(
            thisDescriptor,
            name,
            bindingContext,
            fromSupertypes,
            result
          )
        }
      }

      override fun getSyntheticCompanionObjectNameIfNeeded(thisDescriptor: ClassDescriptor): Name? {
        return phase.run { compilerContext.getSyntheticCompanionObjectNameIfNeeded(thisDescriptor) }
      }

      override fun getSyntheticFunctionNames(thisDescriptor: ClassDescriptor): List<Name> {
        return phase.run { compilerContext.getSyntheticFunctionNames(thisDescriptor) }
      }

      override fun getSyntheticNestedClassNames(thisDescriptor: ClassDescriptor): List<Name> {
        return phase.run { compilerContext.getSyntheticNestedClassNames(thisDescriptor) }
      }
    })
  }

  fun packageFragmentProvider(
    project: Project,
    phase: PackageProvider,
    ctx: CompilerContext
  ) {
    PackageFragmentProviderExtension.registerExtension(
      project,
      object : PackageFragmentProviderExtension {
        override fun getPackageFragmentProvider(
          project: Project,
          module: ModuleDescriptor,
          storageManager: StorageManager,
          trace: BindingTrace,
          moduleInfo: ModuleInfo?,
          lookupTracker: LookupTracker
        ): PackageFragmentProvider? {
          return phase.run {
            ctx.getPackageFragmentProvider(
              project,
              module,
              storageManager,
              trace,
              moduleInfo,
              lookupTracker
            )
          }
        }
      })
  }

  private fun registerPostAnalysisContextEnrichment(project: Project, ctx: CompilerContext) {
    cli {
      AnalysisHandlerExtension.registerExtension(project, object : AnalysisHandlerExtension {
        override fun doAnalysis(
          project: Project,
          module: ModuleDescriptor,
          projectContext: ProjectContext,
          files: Collection<KtFile>,
          bindingTrace: BindingTrace,
          componentProvider: ComponentProvider
        ): AnalysisResult? {
          ctx.module = module
          ctx.files = files
          ctx.componentProvider = componentProvider
          return null
        }
      })
    }
    ide {
      PackageFragmentProviderExtension.registerExtension(project, object : PackageFragmentProviderExtension {
        override fun getPackageFragmentProvider(project: Project, module: ModuleDescriptor, storageManager: StorageManager, trace: BindingTrace, moduleInfo: ModuleInfo?, lookupTracker: LookupTracker): PackageFragmentProvider? {
          println("getPackageFragmentProvider")
          return null
        }
      })
      StorageComponentContainerContributor.registerExtension(
        project,
        object : StorageComponentContainerContributor {
          override fun registerModuleComponents(
            container: StorageComponentContainer,
            platform: TargetPlatform,
            moduleDescriptor: ModuleDescriptor
          ) {
            ctx.module = moduleDescriptor
            ctx.componentProvider = container
            super.registerModuleComponents(container, platform, moduleDescriptor)
          }
        }
      )
    }
  }

  fun registerDeclarationAttributeAlterer(
    project: Project,
    phase: DeclarationAttributeAlterer,
    ctx: CompilerContext
  ) {
    DeclarationAttributeAltererExtension.registerExtension(
      project,
      object : DeclarationAttributeAltererExtension {
        override fun refineDeclarationModality(
          modifierListOwner: KtModifierListOwner,
          declaration: DeclarationDescriptor?,
          containingDeclaration: DeclarationDescriptor?,
          currentModality: Modality,
          bindingContext: BindingContext,
          isImplicitModality: Boolean
        ): Modality? {
          return phase.run {
            ctx.refineDeclarationModality(
              modifierListOwner,
              declaration,
              containingDeclaration,
              currentModality,
              bindingContext,
              isImplicitModality
            )
          }
        }
      })
  }

  fun registerCodegen(project: Project, phase: Codegen, ctx: CompilerContext) {
    ExpressionCodegenExtension.registerExtension(project, object : ExpressionCodegenExtension {
      override fun applyFunction(
        receiver: StackValue,
        resolvedCall: ResolvedCall<*>,
        c: ExpressionCodegenExtension.Context
      ): StackValue? {
        return phase.run { ctx.applyFunction(receiver, resolvedCall, c) }
      }

      override fun applyProperty(
        receiver: StackValue,
        resolvedCall: ResolvedCall<*>,
        c: ExpressionCodegenExtension.Context
      ): StackValue? {
        return phase.run { ctx.applyProperty(receiver, resolvedCall, c) }
      }

      override fun generateClassSyntheticParts(codegen: ImplementationBodyCodegen) {
        phase.run { ctx.generateClassSyntheticParts(codegen) }
      }
    })
  }

  class DelegatingContributorChecker(val phase: arrow.meta.phases.analysis.StorageComponentContainer, val ctx: CompilerContext) : StorageComponentContainerContributor, DeclarationChecker {

    override fun registerModuleComponents(container: StorageComponentContainer, platform: TargetPlatform, moduleDescriptor: ModuleDescriptor) {
      phase.run { ctx.registerModuleComponents(container, moduleDescriptor) }
    }

    override fun check(declaration: KtDeclaration, descriptor: DeclarationDescriptor, context: DeclarationCheckerContext) {
      phase.run { ctx.check(declaration, descriptor, context) }
    }
  }

  fun registerStorageComponentContainer(
    project: Project,
    phase: arrow.meta.phases.analysis.StorageComponentContainer,
    ctx: CompilerContext
  ) {
    StorageComponentContainerContributor.registerExtension(
      project,
      DelegatingContributorChecker(phase, ctx)
    )
  }

  fun registerCollectAdditionalSources(
    project: Project,
    phase: CollectAdditionalSources,
    ctx: CompilerContext
  ) {
    cli {
      CollectAdditionalSourcesExtension.registerExtension(
        project,
        object : CollectAdditionalSourcesExtension {
          override fun collectAdditionalSourcesAndUpdateConfiguration(
            knownSources: Collection<KtFile>,
            configuration: CompilerConfiguration,
            project: Project
          ): Collection<KtFile> = phase.run {
            ctx.collectAdditionalSourcesAndUpdateConfiguration(knownSources, configuration, project)
          }
        }
      )
    }
  }

  fun registerAnalysisHandler(
    project: Project,
    phase: AnalysisHandler,
    ctx: CompilerContext
  ) {
    cli {
      AnalysisHandlerExtension.registerExtension(project, object : AnalysisHandlerExtension {
        override fun analysisCompleted(
          project: Project,
          module: ModuleDescriptor,
          bindingTrace: BindingTrace,
          files: Collection<KtFile>
        ): AnalysisResult? = phase.run { ctx.analysisCompleted(project, module, bindingTrace, files) }

        override fun doAnalysis(
          project: Project,
          module: ModuleDescriptor,
          projectContext: ProjectContext,
          files: Collection<KtFile>,
          bindingTrace: BindingTrace,
          componentProvider: ComponentProvider
        ): AnalysisResult? {
          return phase.run {
            ctx.doAnalysis(
              project,
              module,
              projectContext,
              files,
              bindingTrace,
              componentProvider
            )
          }
        }
      })
    }
  }

  fun registerClassBuilder(
    project: Project,
    phase: arrow.meta.phases.codegen.asm.ClassBuilder,
    ctx: CompilerContext
  ) {
    ClassBuilderInterceptorExtension.registerExtension(
      project,
      object : ClassBuilderInterceptorExtension {
        override fun interceptClassBuilderFactory(
          interceptedFactory: ClassBuilderFactory,
          bindingContext: BindingContext,
          diagnostics: DiagnosticSink
        ): ClassBuilderFactory =
          phase.run {
            ctx.interceptClassBuilder(interceptedFactory, bindingContext, diagnostics)
          }
      })
  }

  fun registerCompilerConfiguration(
    project: Project,
    phase: Config,
    ctx: CompilerContext
  ) {
    CompilerConfigurationExtension.registerExtension(
      project,
      object : CompilerConfigurationExtension {
        override fun updateConfiguration(configuration: CompilerConfiguration) {
          phase.run { ctx.updateConfiguration(configuration) }
        }
      })
  }
}


internal class NewMethodClassBuilder(
  private val builder: ClassBuilder,
  val f: (origin: JvmDeclarationOrigin, access: Int, name: String, desc: String, signature: String?, exceptions: Array<out String>?) -> Unit
) : DelegatingClassBuilder() {
  override fun getDelegate(): ClassBuilder = builder

  override fun newMethod(
    origin: JvmDeclarationOrigin,
    access: Int,
    name: String,
    desc: String,
    signature: String?,
    exceptions: Array<out String>?
  ): MethodVisitor {
    //delegate to the parent method visitor for construction
    val original: MethodVisitor = super.newMethod(origin, access, name, desc, signature, exceptions)
    f(origin, access, name, desc, signature, exceptions)
    return original
  }

}

internal class NewFieldClassBuilder(
  private val builder: ClassBuilder,
  val f: (origin: JvmDeclarationOrigin, access: Int, name: String, desc: String, signature: String?, value: Any?) -> Unit
) : DelegatingClassBuilder() {
  override fun getDelegate(): ClassBuilder = builder

  override fun newField(
    origin: JvmDeclarationOrigin,
    access: Int,
    name: String,
    desc: String,
    signature: String?,
    value: Any?
  ): FieldVisitor {
    //delegate to the parent method visitor for construction
    val original: FieldVisitor = super.newField(origin, access, name, desc, signature, value)
    f(origin, access, name, desc, signature, value)
    return original
  }

}

internal class NewAnnotationClassBuilder(
  private val builder: ClassBuilder,
  val f: (desc: String, visible: Boolean) -> Unit
) : DelegatingClassBuilder() {
  override fun getDelegate(): ClassBuilder = builder

  override fun newAnnotation(desc: String, visible: Boolean): AnnotationVisitor {
    val original: AnnotationVisitor = super.newAnnotation(desc, visible)
    f(desc, visible)
    return original
  }

}

internal class DefineClassBuilder(
  private val builder: ClassBuilder,
  private val bindingContext: BindingContext,
  private val diagnostics: DiagnosticSink,
  val f: (bindingContext: BindingContext, diagnostics: DiagnosticSink, classDef: ClassDefinition) -> ClassDefinition
) : DelegatingClassBuilder() {
  override fun getDelegate(): ClassBuilder = builder

  override fun defineClass(
    origin: PsiElement?,
    version: Int,
    access: Int,
    name: String,
    signature: String?,
    superName: String,
    interfaces: Array<out String>
  ) {
    val classDef = f(
      bindingContext, diagnostics,
      ClassDefinition(
        origin,
        version,
        access,
        name,
        signature,
        superName,
        interfaces.toList()
      )
    )
    super.defineClass(
      classDef.origin,
      classDef.version,
      classDef.access,
      classDef.name,
      classDef.signature,
      classDef.superName,
      classDef.interfaces.toTypedArray()
    )
  }
}

data class ClassDefinition(
  val origin: PsiElement?,
  val version: Int,
  val access: Int,
  val name: String,
  val signature: String?,
  val superName: String,
  val interfaces: List<String>
)