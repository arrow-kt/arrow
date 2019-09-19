package arrow.meta.quotes

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.MetaComponentRegistrar
import arrow.meta.phases.analysis.MetaAnalyzer
import arrow.meta.phases.analysis.MetaFileViewProvider
import arrow.meta.dsl.platform.cli
import arrow.meta.dsl.platform.ide
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.com.intellij.openapi.editor.Document
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import org.jetbrains.kotlin.com.intellij.psi.SingleRootFileViewProvider
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PackageViewDescriptor
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.TypeParameterDescriptorImpl
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.kotlin.psi.synthetics.SyntheticClassOrObjectDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.lazy.LazyClassContext
import org.jetbrains.kotlin.resolve.lazy.declarations.ClassMemberDeclarationProvider
import org.jetbrains.kotlin.resolve.lazy.declarations.DeclarationProvider
import org.jetbrains.kotlin.resolve.lazy.declarations.PackageMemberDeclarationProvider
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.KotlinTypeFactory
import org.jetbrains.kotlin.types.TypeProjectionImpl
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * A declaration quasi quote matches tree in the synthetic resolution and gives
 * users the chance to transform them before they are processed by the Kotlin compiler.
 */
interface Quote<P : KtElement, K : KtElement, S> {

  val containingDeclaration: P

  /**
   * Provides access to compiler context services and factories including the binding trace
   */
  val quasiQuoteContext: QuasiQuoteContext

  /**
   * Turn a string template into a [KtElement]
   */
  fun parse(template: String): K

  /**
   * Returns a String representation of what a match for a tree may look like. For example:
   * ```
   * "fun <$typeArgs> $name($params): $returnType = $body"
   * ```
   */
  fun K.match(): Boolean

  /**
   * Given real matches of a [quotedTemplate] the user is then given a chance to transform it into a new tree
   * where also uses code as a template
   */
  fun S.map(quotedTemplate: K): List<String>

  interface Factory<P : KtElement, K : KtElement, S> {
    operator fun invoke(
      quasiQuoteContext: QuasiQuoteContext,
      containingDeclaration: P,
      match: K.() -> Boolean,
      map: S.(quotedTemplate: K) -> List<String>
    ): Quote<P, K, S>
  }

  fun transform(ktElement: K): S

  fun K.cleanUserQuote(quoteDeclaration: String): String = quoteDeclaration

  fun process(ktElement: K): QuoteTransformation<K>? {
    return if (ktElement.match()) {
      // a new scope is transformed
      val transformedScope = transform(ktElement)
      // the user transforms the expression into a new list of declarations
      val declarations = transformedScope.map(ktElement).map { quoteDeclaration ->
        val declaration =
          quasiQuoteContext.compilerContext.ktPsiElementFactory
            .createDeclaration<KtDeclaration>(ktElement.cleanUserQuote(quoteDeclaration))
        declaration
      }
      if (declarations.isEmpty()) null
      else QuoteTransformation(ktElement, declarations)
    } else null
  }

}

fun MetaComponentRegistrar.func(
  match: KtFunction.() -> Boolean,
  map: Func.FuncScope.(KtFunction) -> List<String>
): ExtensionPhase =
  quote(Func, match, map)

fun MetaComponentRegistrar.classOrObject(
  match: KtClass.() -> Boolean,
  map: ClassScope.(KtClass) -> List<String>
): ExtensionPhase =
  quote(ClassOrObject, match, map)

inline fun <P : KtElement, reified K : KtElement, S, Q : Quote<P, K, S>> MetaAnalyzer.runMetaCompilation(
  compilerContext: CompilerContext,
  project: Project,
  virtualFile: VirtualFile,
  document: Document,
  moduleInfo: ModuleInfo,
  quoteFactory: Quote.Factory<P, K, S>,
  noinline match: K.() -> Boolean,
  noinline map: S.(K) -> List<String>
): Pair<KtFile, AnalysisResult> = compilerContext.run {
  val originFakeFile = KtFile(SingleRootFileViewProvider(
    project.getComponent(PsiManager::class.java),
    virtualFile,
    false
  ), true)
  val analyzableFile = ktPsiElementFactory.createAnalyzableFile(virtualFile.name, document.text, originFakeFile)
  val (file, transformations) = processKtFile(analyzableFile, quoteFactory, match, map)
  val transformedFile = transformFile(file, transformations)
  transformedFile to transformedFile.metaAnalysys(moduleInfo)
}


@Suppress("UNCHECKED_CAST")
inline fun <P : KtElement, reified K : KtElement, S, Q : Quote<P, K, S>> MetaComponentRegistrar.quote(
  quoteFactory: Quote.Factory<P, K, S>,
  noinline match: K.() -> Boolean,
  noinline map: S.(K) -> List<String>
): ExtensionPhase =
  cli {
    analysys(
      doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
        files as ArrayList
        println("START quote.doAnalysis: $files")
        val fileMutations = processFiles(files, quoteFactory, match, map)
        updateFiles(files, fileMutations)
        println("END quote.doAnalysis: $files")
        files.forEach {
          val fileText = it.text
          if (fileText.contains("//metadebug")) {
            File(it.virtualFilePath + ".meta").writeText(it.text)
            println("""|
            |ktFile: $it
            |----
            |${it.text}
            |----
          """.trimMargin())
          }
        }
        null
      },
      analysisCompleted = { project, module, bindingTrace, files ->
        null
      }
    )
  } ?: ExtensionPhase.Empty
//
//  ide {
//    println("Register SYNTH RESOLVER IN IDEA")
//    ExtensionPhase.CompositePhase(
//      packageFragmentProvider { project, module, storageManager, trace, moduleInfo, lookupTracker ->
//        analyzer?.run {
//          moduleInfo?.let { info ->
//            subscribeToEditorHooks(project, quoteFactory, match, map) { virtualFile, document ->
//              runMetaCompilation(this@packageFragmentProvider, project, virtualFile, document, info, quoteFactory, match, map)
//            }
////
////            val (file, analysysResult) = runMetaCompilation(this@packageFragmentProvider, project, virtualFile, document, info, quoteFactory, match, map)
////            populateSyntheticCache(doc)
//          }
//        }
//        object : PackageFragmentProvider {
//          override fun getPackageFragments(fqName: FqName): List<PackageFragmentDescriptor> {
//            val result = analyzer?.run {
//              storageManager.createRecursionTolerantLazyValue({
//                metaPackageFragments(module, fqName)
//              }, emptyList()).invoke()
//            } ?: emptyList()
//            println("PackageFragmentProvider.getPackageFragments: $fqName $result")
//            return result
//          }
//
//          override fun getSubPackagesOf(fqName: FqName, nameFilter: (Name) -> Boolean): Collection<FqName> {
//            val result = analyzer?.run {
//              storageManager.createRecursionTolerantLazyValue({
//                metaSubPackagesOf(module, fqName, nameFilter)
//              }, emptyList()).invoke()
//            } ?: emptyList()
//            println("PackageFragmentProvider.metaSubPackagesOf: $fqName $result")
//            return result
//          }
//
//
//        }
//      },
//      syntheticResolver(
//        addSyntheticSupertypes = { thisDescriptor, supertypes ->
//          analyzer?.run {
//            val synthetic: List<KotlinType> = metaSyntheticSupertypes(thisDescriptor)
//            println("MetaSyntheticResolverExtension.addSyntheticSupertypes for $thisDescriptor name: [$synthetic]")
//            supertypes.addAll(synthetic)
//          }
//        },
//        getSyntheticCompanionObjectNameIfNeeded = { thisDescriptor ->
//          analyzer?.run {
//            val companionName: Name? = metaCompanionObjectNameIfNeeded(thisDescriptor)
//            println("MetaSyntheticResolverExtension.getSyntheticCompanionObjectNameIfNeeded for $thisDescriptor name: [$companionName]")
//            companionName
//          }
//        },
//        generateSyntheticClasses = { thisDescriptor, name, ctx, declarationProvider, result ->
//          analyzer?.run {
//            val syntheticClasses: List<ClassDescriptor> = metaSyntheticClasses(name, thisDescriptor, declarationProvider)
//            result.addAll(syntheticClasses)
//            println("MetaSyntheticResolverExtension.generateSyntheticClasses for $thisDescriptor [$name]: $result")
//            result
//          }
//        },
//        generatePackageSyntheticClasses = { thisDescriptor, name, ctx, declarationProvider, result ->
//          analyzer?.run {
//            val synthetic: List<ClassDescriptor> = metaSyntheticPackageClasses(name, thisDescriptor, declarationProvider)
//            result.addAll(synthetic)
//            println("MetaSyntheticResolverExtension.generatePackageSyntheticClasses for $thisDescriptor [$name]: $result")
//            result
//          }
//        },
//        generateSyntheticMethods = { thisDescriptor, name, bindingContext, fromSupertypes, result ->
//          analyzer?.run {
//            val synthetic: List<SimpleFunctionDescriptor> = metaSyntheticMethods(name, thisDescriptor)
//            result.addAll(synthetic)
//            println("MetaSyntheticResolverExtension.generateSyntheticMethods for $thisDescriptor [$name]: $result")
//            result
//          }
//        },
//        generateSyntheticProperties = { thisDescriptor, name, bindingContext, fromSupertypes, result ->
//          analyzer?.run {
//            val synthetic: List<PropertyDescriptor> = metaSyntheticProperties(name, thisDescriptor)
//            result.addAll(synthetic)
//            println("MetaSyntheticResolverExtension.generateSyntheticMethods for $thisDescriptor [$name]: $result")
//            result
//          }
//        },
//        getSyntheticFunctionNames = { thisDescriptor ->
//          analyzer?.run {
//            val result = metaSyntheticFunctionNames(thisDescriptor)
//            println("MetaSyntheticResolverExtension.getSyntheticFunctionNames: $thisDescriptor $result")
//            result
//          } ?: emptyList()
//        },
//        getSyntheticNestedClassNames = { thisDescriptor ->
//          analyzer?.run {
//            val result = metaSyntheticNestedClassNames(thisDescriptor)
//            println("MetaSyntheticResolverExtension.getSyntheticNestedClassNames: $thisDescriptor $result")
//            result
//          } ?: emptyList()
//        }
//      )
//    )

fun PackageViewDescriptor.declarations(): Collection<DeclarationDescriptor> =
  memberScope.getContributedDescriptors { true }

fun KtFile.declaredClassWithName(name: Name): KtClass? =
  findDescendantOfType<KtClass> { it.name == name.asString() }

fun KtClassOrObject.functions(): List<KtNamedFunction> = declarations.filterIsInstance<KtNamedFunction>()

fun DeclarationDescriptor.ktFile(): KtFile? =
  findPsi()?.containingFile.safeAs()

fun KtFile.classes(): List<KtClassOrObject> = declarations.filterIsInstance<KtClassOrObject>()

fun KtClassOrObject.nestedClasses(): List<KtClassOrObject> = declarations.filterIsInstance<KtClassOrObject>()

fun companionName(ktClass: KtClass): String? =
  ktClass.companionObjects.firstOrNull()?.name

fun PackageFragmentDescriptor.packageFiles(declarationProvider: PackageMemberDeclarationProvider) =
  declarationProvider.getPackageFiles().filter { it.packageFqName == fqName }

fun ClassDescriptor.ktClassOrObject(): KtClassOrObject? =
  findPsi() as? KtClassOrObject

fun KtClassOrObject.nestedClassNames(): List<String> =
  declarations.filterIsInstance<KtClassOrObject>().mapNotNull { it.name }

fun PsiElement.ktFile(): KtFile? =
  containingFile.safeAs()

inline fun <reified K : KtElement, P : KtElement, S> CompilerContext.processSources(
  ktFile: KtFile,
  quoteFactory: Quote.Factory<P, K, S>,
  noinline match: K.() -> Boolean,
  noinline map: S.(K) -> List<String>
): KtFile {
  val (file, transformations) = processKtFile(ktFile, quoteFactory, match, map)
  return transformFile(file, transformations)
}

fun KtFile.ktClassNamed(name: String?): KtClass? =
  name?.let {
    findDescendantOfType { d -> d.name == it }
  }

fun KtClassOrObject.functionNames() =
  declarations.filterIsInstance<KtFunction>().mapNotNull { it.name }.map(Name::identifier)

//fun CompilerContext.syntheticFunctionDescriptor2(
//  containingDeclaration: ClassDescriptorWithResolutionScopes,
//  ktNamedFunction: KtNamedFunction
//): SimpleFunctionDescriptor? {
//  containingDeclaration as LazyClassDescriptor
//  return synthFunctionResolver.resolveFunctionDescriptor(
//    containingDeclaration,
//    containingDeclaration.scopeForMemberDeclarationResolution,
//    ktNamedFunction,
//    bindingTrace,
//    DataFlowInfo.EMPTY
//  ).run {
//    copy(containingDeclaration, modality, visibility, CallableMemberDescriptor.Kind.SYNTHESIZED, true)
//  }
//}

fun ArrayList<Name>.contributeNames(syntheticDescriptors: List<Name>): Unit {
  val newDescriptors = this + syntheticDescriptors
  clear()
  addAll(newDescriptors.distinct())
}

fun <A : DeclarationDescriptor> MutableCollection<A>.contribute(syntheticDescriptors: List<A>): Unit {
  val newDescriptors = this + syntheticDescriptors
  clear()
  addAll(newDescriptors.distinctBy {
    when (it) {
      is ClassDescriptor -> it.fqNameSafe.asString()
      is FunctionDescriptor -> it.findPsi()?.text
      else -> it.name.asString()
    }
  })
}

//
//fun CompilerContext.syntheticFunctionDescriptor(
//  container: ClassDescriptorWithResolutionScopes,
//  ktNamedFunction: KtNamedFunction,
//  bindingContext: BindingContext,
//  descriptorResolver: DescriptorResolver,
//  typeResolver : TypeResolver
//): SimpleFunctionDescriptor {
//  val functionDescriptor = SimpleFunctionDescriptorImpl.create(
//    container,
//    Annotations.EMPTY,
//    ktNamedFunction.nameAsSafeName,
//    CallableMemberDescriptor.Kind.SYNTHESIZED,
//    container.toSourceElement
//  )
//  val headerScope = LexicalWritableScope(
//    container.scopeForMemberDeclarationResolution, functionDescriptor, true,
//    TraceBasedLocalRedeclarationChecker(bindingTrace, OverloadChecker(TypeSpecificityComparator.NONE)), LexicalScopeKind.FUNCTION_HEADER
//  )
//  val typeParameterDescriptors =
//    descriptorResolver.resolveTypeParametersForDescriptor(functionDescriptor, headerScope, headerScope, ktNamedFunction.typeParameters, bindingTrace)
//  descriptorResolver.resolveGenericBounds(ktNamedFunction, functionDescriptor, headerScope, typeParameterDescriptors, bindingTrace)
//
//  val receiverTypeRef = ktNamedFunction.receiverTypeReference
//  val receiverType =
//    if (receiverTypeRef != null) {
//      typeResolver.resolveType(headerScope, receiverTypeRef, bindingTrace, true)
//    } else {
//      if (ktNamedFunction is KtFunctionLiteral) expectedFunctionType.getReceiverType() else null
//    }
//
//
//  val valueParameterDescriptors =
//    createValueParameterDescriptors(function, functionDescriptor, headerScope, trace, expectedFunctionType)
//
//  headerScope.freeze()
//
//  val returnType = ktNamedFunction.typeReference?.let { typeResolver.resolveType(headerScope, it, trace, true) }
//
//  val visibility = ModifiersChecker.resolveVisibilityFromModifiers(ktNamedFunction, DescriptorResolver.getDefaultVisibility(ktNamedFunction, container))
//  val modality = ModifiersChecker.resolveMemberModalityFromModifiers(
//    ktNamedFunction, DescriptorResolver.getDefaultModality(container, visibility, ktNamedFunction.hasBody()),
//    bindingContext, container
//  )
//
//  val contractProvider = getContractProvider(functionDescriptor, trace, scope, dataFlowInfo, function)
//  val userData = mutableMapOf<CallableDescriptor.UserDataKey<*>, Any>().apply {
//    if (contractProvider != null) {
//      put(ContractProviderKey, contractProvider)
//    }
//
//    if (receiverType != null && expectedFunctionType.functionTypeExpected() && !expectedFunctionType.annotations.isEmpty()) {
//      put(DslMarkerUtils.FunctionTypeAnnotationsKey, expectedFunctionType.annotations)
//    }
//  }
//
//  val extensionReceiver = receiverType?.let {
//    val splitter = AnnotationSplitter(storageManager, receiverType.annotations, EnumSet.of(AnnotationUseSiteTarget.RECEIVER))
//    DescriptorFactory.createExtensionReceiverParameterForCallable(
//      functionDescriptor, it, splitter.getAnnotationsForTarget(AnnotationUseSiteTarget.RECEIVER)
//    )
//  }
//
//  functionDescriptor.initialize(
//    extensionReceiver,
//    DescriptorUtils.getDispatchReceiverParameterIfNeeded(container),
//    typeParameterDescriptors,
//    valueParameterDescriptors,
//    returnType,
//    modality,
//    visibility,
//    userData.takeIf { it.isNotEmpty() }
//  )
//
//  functionDescriptor.isOperator = ktNamedFunction.hasModifier(KtTokens.OPERATOR_KEYWORD)
//  functionDescriptor.isInfix = ktNamedFunction.hasModifier(KtTokens.INFIX_KEYWORD)
//  functionDescriptor.isExternal = ktNamedFunction.hasModifier(KtTokens.EXTERNAL_KEYWORD)
//  functionDescriptor.isInline = ktNamedFunction.hasModifier(KtTokens.INLINE_KEYWORD)
//  functionDescriptor.isTailrec = ktNamedFunction.hasModifier(KtTokens.TAILREC_KEYWORD)
//  functionDescriptor.isSuspend = ktNamedFunction.hasModifier(KtTokens.SUSPEND_KEYWORD)
//  functionDescriptor.isExpect = container is PackageFragmentDescriptor && ktNamedFunction.hasExpectModifier() ||
//    container is ClassDescriptor && container.isExpect
//  functionDescriptor.isActual = ktNamedFunction.hasActualModifier()
//
//  receiverType?.let { ForceResolveUtil.forceResolveAllContents(it.annotations) }
//  for (valueParameterDescriptor in valueParameterDescriptors) {
//    ForceResolveUtil.forceResolveAllContents(valueParameterDescriptor.type.annotations)
//  }
//  return functionDescriptor
//}

fun LazyClassContext.syntheticDescriptor(
  containingDeclaration: DeclarationDescriptor,
  declarationProvider: DeclarationProvider,
  ktClassOrObject: KtClassOrObject,
  isCompanionObject: Boolean
): SyntheticClassOrObjectDescriptor =
  SyntheticClassOrObjectDescriptor(
    this,
    /* parentClassOrObject= */ ktClassOrObject,
    containingDeclaration,
    ktClassOrObject.nameAsSafeName,
    SourceElement.NO_SOURCE,
    if (declarationProvider is ClassMemberDeclarationProvider) {
      /* outerScope= */ declarationScopeProvider.getResolutionScopeForDeclaration(declarationProvider.ownerInfo!!.scopeAnchor)
    } else declarationScopeProvider.getResolutionScopeForDeclaration(ktClassOrObject),
    Modality.FINAL,
    Visibilities.PUBLIC,
    Annotations.EMPTY,
    Visibilities.PRIVATE,
    if (ktClassOrObject is KtObjectDeclaration) ClassKind.OBJECT
    else if (ktClassOrObject is KtClass && ktClassOrObject.isInterface()) ClassKind.INTERFACE
    else ClassKind.CLASS,
    isCompanionObject
  ).also {
    val typeParameters: List<TypeParameterDescriptor> =
      ktClassOrObject.typeParameters.mapIndexed { index, param ->
        TypeParameterDescriptorImpl.createWithDefaultBound(
          it,
          Annotations.EMPTY,
          false,
          Variance.INVARIANT,
          param.nameAsSafeName,
          index
        )
      }
    it.initialize(typeParameters)
  }


fun ClassDescriptor.createType(reference: KtTypeReference): KotlinType {
  val projectionType = Variance.INVARIANT
  val types = reference.typeElement?.typeArgumentsAsTypes?.map { argReference ->
    TypeProjectionImpl(projectionType, createType(argReference))
  } ?: emptyList()
  return KotlinTypeFactory.simpleNotNullType(Annotations.EMPTY, this, types)
}


@Suppress("UNCHECKED_CAST")
inline fun <reified K : KtElement, P : KtElement, S> CompilerContext.processFiles(
  files: Collection<KtFile>,
  quoteFactory: Quote.Factory<P, K, S>,
  noinline match: K.() -> Boolean,
  noinline map: S.(K) -> List<String>
): List<Pair<KtFile, ArrayList<QuoteTransformation<K>>>> {
  return files.map { file ->
    processKtFile(file, quoteFactory, match, map)
  }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified K : KtElement, P : KtElement, S> CompilerContext.processKtFile(
  file: KtFile,
  quoteFactory: Quote.Factory<P, K, S>,
  noinline match: K.() -> Boolean,
  noinline map: S.(K) -> List<String>
): Pair<KtFile, ArrayList<QuoteTransformation<K>>> {
  val mutatingDocument = file.viewProvider.document
  val mutations = arrayListOf<QuoteTransformation<K>>()
  if (mutatingDocument != null) {
    file.accept(object : MetaTreeVisitor() {
      override fun visitKtElement(element: KtElement) {
        if (element.javaClass == K::class.java) {
          val transformation = quoteFactory(
            quasiQuoteContext = QuasiQuoteContext(this@processKtFile),
            containingDeclaration = element.psiOrParent as P,
            match = match,
            map = map
          ).process(element as K)
          transformation?.let { mutations.add(it) }
        }
        return super.visitKtElement(element)
      }
    })
  }
  return file to mutations
}

inline fun <reified K : KtElement> CompilerContext.updateFiles(
  result: java.util.ArrayList<KtFile>,
  fileMutations: List<Pair<KtFile, java.util.ArrayList<QuoteTransformation<K>>>>
) {
  fileMutations.forEach { (file, mutations) ->
    val newFile = updateFile(mutations, file)
    result.replaceFiles(file, newFile)
  }
}

inline fun <reified K : KtElement> CompilerContext.updateFile(
  mutations: java.util.ArrayList<QuoteTransformation<K>>,
  file: KtFile
): KtFile =
  if (mutations.isNotEmpty()) {
    transformFile(file, mutations)
  } else file

inline fun <reified K : KtElement> CompilerContext.transformFile(
  ktFile: KtFile,
  mutations: java.util.ArrayList<QuoteTransformation<K>>
): KtFile {
  val newSource = ktFile.sourceWithTransformations(mutations)
  val newFile = changeSource(ktFile, newSource)
  //println("Transformed file: $ktFile. New contents: \n$newSource")
  return newFile
}

fun <K : KtElement> KtFile.sourceWithTransformations(mutations: ArrayList<QuoteTransformation<K>>): String =
  mutations.fold(text) { acc, transformation ->
    val originalSource = transformation.oldDescriptor.text
    val newSource = transformation.newDeclarations.joinToString("\n\n") { it.text }
    acc.replace(originalSource, newSource)
  }

fun KtFile.printDiff(newSource: String) {
  println("""
              |
              |----------------------------------
              |*Tree Mutation*
              |----------------------------------
              |Old 
              |---
              |$text
              |---
              |New
              |---
              |$newSource
              |---
              |----------------------------------
              |
              |""".trimMargin())
}

fun java.util.ArrayList<KtFile>.replaceFiles(file: KtFile, newFile: KtFile) {
  val fileIndex = indexOf(file)
  removeAt(fileIndex)
  add(fileIndex, newFile)
}

fun CompilerContext.changeSource(file: KtFile, newSource: String): KtFile =
  cli {
    KtFile(
      viewProvider = MetaFileViewProvider(file.manager, file.virtualFile) {
        it?.also {
          it.setText(newSource)
        }
      },
      isCompiled = false
    )
  } ?: ide {
    ktPsiElementFactory.createAnalyzableFile("_meta_${file.name}", newSource, file)
  }!!

@Suppress("UNCHECKED_CAST")
inline operator fun <reified A, B> A.get(field: String): B {
  val clazz = A::class.java
  return try {
    clazz.getDeclaredField(field).also { it.isAccessible = true }.get(this) as B
  } catch (e: Exception) {
    clazz.getField(field).also { it.isAccessible = true }.get(this) as B
  }
}


fun KtFile.isMetaFile(): Boolean =
  name.startsWith("_meta_")
