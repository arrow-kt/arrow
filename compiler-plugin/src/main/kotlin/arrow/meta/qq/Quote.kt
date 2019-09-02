package arrow.meta.qq

import arrow.meta.extensions.CompilerContext
import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaComponentRegistrar
import arrow.meta.utils.cli
import arrow.meta.utils.ide
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
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
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.kotlin.psi.synthetics.SyntheticClassOrObjectDescriptor
import org.jetbrains.kotlin.resolve.lazy.LazyClassContext
import org.jetbrains.kotlin.resolve.lazy.declarations.ClassMemberDeclarationProvider
import org.jetbrains.kotlin.resolve.lazy.declarations.DeclarationProvider
import org.jetbrains.kotlin.resolve.lazy.declarations.PackageMemberDeclarationProvider
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.KotlinTypeFactory
import org.jetbrains.kotlin.types.TypeProjectionImpl
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

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
  quote(Func.Companion, match, map)

fun MetaComponentRegistrar.classOrObject(
  match: KtClass.() -> Boolean,
  map: ClassOrObject.ClassScope.(KtClass) -> List<String>
): ExtensionPhase =
  quote(ClassOrObject.Companion, match, map)

@Suppress("UNCHECKED_CAST")
inline fun <P : KtElement, reified K : KtElement, S, Q : Quote<P, K, S>> MetaComponentRegistrar.quote(
  quoteFactory: Quote.Factory<P, K, S>,
  noinline match: K.() -> Boolean,
  noinline map: S.(K) -> List<String>
): ExtensionPhase =
  cli {
    analysys(
      doAnalysis = { _, _, _, files, _, _ ->
        files as ArrayList
        //files.clear()
        val fileMutations = processFiles(files, quoteFactory, match, map)
        updateFiles(files, fileMutations)
        null
      },
      analysisCompleted = { project, module, bindingTrace, files ->
        null
      }
    )
  } ?: ide {
    println("Register SYNTH RESOLVER IN IDEA")
    syntheticResolver(
//      addSyntheticSupertypes = { thisDescriptor, supertypes ->
//        println("syntheticResolver.addSyntheticSupertypes: $thisDescriptor")
//        val classPsi = thisDescriptor.findPsi()
//        if (classPsi != null && classPsi is KtClassOrObject && thisDescriptor is ClassDescriptorWithResolutionScopes) {
//          classPsi.containingFile.safeAs<KtFile>()?.let { ktFile ->
//            val (file, transformations) = processKtFile(ktFile, quoteFactory, match, map)
//            val transformedFile = transformFile(file, transformations)
//            transformedFile.findDescendantOfType<KtClass> { it.name == classPsi.name }?.let { ktClass ->
//              val syntheticEntries = (ktClass.superTypeListEntries + classPsi.superTypeListEntries).distinctBy { it.name }
//              val synthSuperTypes = syntheticEntries.mapNotNull { entry ->
//                entry.typeReference?.let { typeReference ->
//                  if (thisDescriptor is SyntheticClassOrObjectDescriptor) {
//                    thisDescriptor.initialize()
//                  }
//                  val typeName = entry.typeReference?.typeElement?.text
//                  println("Attempting to lookup descriptor for supertype: $typeName")
//                  val superTypeDescriptor = typeName?.let { module.findClassAcrossModuleDependencies(ClassId.topLevel(FqName(it))) }
//                  val superType = superTypeDescriptor?.defaultType
//                    ?: bindingTrace.get(BindingContext.TYPE, typeReference)
//                    // ?: thisDescriptor.createSuperType(typeReference) //TODO this fails because the inner memberScope for the descriptor is null
//                  superType
//                }
//              }
//              println("meta.syntheticResolver: added synth supertypes for descriptor ${thisDescriptor.name}: $synthSuperTypes ")
//              supertypes.addAll(synthSuperTypes)
//            }
//          }
//        }
//
//      },
      generateSyntheticClasses = { thisDescriptor, name, ctx, declarationProvider, result ->
        println("syntheticResolver.generateInnerSyntheticClasses: $thisDescriptor")
        val result = arrayListOf<SyntheticClassOrObjectDescriptor>()
        val classPsi = thisDescriptor.findPsi().safeAs<KtClassOrObject>()
        val ktFile = thisDescriptor.findPsi()?.containingFile.safeAs<KtFile>()
        if (classPsi != null && ktFile != null) {
          val (file, transformations) = processKtFile(ktFile, quoteFactory, match, map)
          val transformedFile = transformFile(file, transformations)
          transformedFile.findDescendantOfType<KtClass> { it.name == thisDescriptor.name.asString() }?.let { ktClass ->
            val generatedClasses = ktClass.declarations.filterIsInstance<KtClassOrObject>()
            val originalClasses = classPsi.declarations.filterIsInstance<KtClassOrObject>()
            val syntheticClasses = generatedClasses - originalClasses
            val synthDescriptors = syntheticClasses.map {
              ctx.syntheticDescriptor(
                thisDescriptor,
                declarationProvider,
                it,
                thisDescriptor.companionObjectDescriptor?.name?.asString() in it.companionObjects.map { it.name }
              )
            }
            println("meta.syntheticResolver: generateInnerSyntheticClasses ${thisDescriptor.name}: $synthDescriptors ")
            result.addAll(synthDescriptors)
          }
        }
      },
      generatePackageSyntheticClasses = { thisDescriptor, name, ctx, declarationProvider, result ->
        println("syntheticResolver.generatePackageSyntheticClasses: $thisDescriptor")
        val result = arrayListOf<SyntheticClassOrObjectDescriptor>()
        val ktFile = declarationProvider.getPackageFiles().firstOrNull { it.packageFqName == thisDescriptor.fqName }
        if (ktFile != null) {
          val (file, transformations) = processKtFile(ktFile, quoteFactory, match, map)
          val transformedFile = transformFile(file, transformations)
          val originalClasses = file.declarations.filterIsInstance<KtClassOrObject>()
          val transformedDeclarations = transformedFile.declarations.filterIsInstance<KtClassOrObject>()
          val newDeclarations = (originalClasses + transformedDeclarations).distinctBy { it.name }
          val synthDescriptors = newDeclarations.map {
            ctx.syntheticDescriptor(thisDescriptor, declarationProvider, it, false)
          }
          println("meta.syntheticResolver: generatePackageSyntheticClasses ${thisDescriptor.name}: $synthDescriptors ")
          result.addAll(synthDescriptors)
        }
      },
      getSyntheticCompanionObjectNameIfNeeded = { thisDescriptor ->
        println("syntheticResolver.getSyntheticCompanionObjectNameIfNeeded: $thisDescriptor")
        val classPsi = thisDescriptor.findPsi()
        var result: Name? = null
        if (classPsi != null && classPsi is KtClassOrObject) {
          classPsi.containingFile.safeAs<KtFile>()?.let { ktFile ->
            val (file, transformations) = processKtFile(ktFile, quoteFactory, match, map)
            val transformedFile = transformFile(file, transformations)
            transformedFile.findDescendantOfType<KtClass> { it.name == classPsi.name }?.let { ktClass ->
              val companionName = ktClass.companionObjects.firstOrNull()?.name
              println("meta.syntheticResolver: found companion object name for descriptor ${thisDescriptor.name}: $companionName ")
              companionName?.let { result = Name.identifier(it) }
            }
          }
        }
        result
      },
      getSyntheticNestedClassNames = { thisDescriptor ->
        println("syntheticResolver.getSyntheticNestedClassNames: $thisDescriptor")
        val result = arrayListOf<Name>()
        val classPsi = thisDescriptor.findPsi()
        if (classPsi != null && classPsi is KtClassOrObject) {
          classPsi.containingFile.safeAs<KtFile>()?.let { ktFile ->
            val (file, transformations) = processKtFile(ktFile, quoteFactory, match, map)
            val transformedFile = transformFile(file, transformations)
            transformedFile.findDescendantOfType<KtClass> { it.name == classPsi.name }?.let { ktClass ->
              val generatedNames = ktClass.declarations.filterIsInstance<KtClassOrObject>().mapNotNull { it.name }
              val originalNames = classPsi.declarations.filterIsInstance<KtClassOrObject>().mapNotNull { it.name }
              val syntheticNames = generatedNames - originalNames
              println("meta.syntheticResolver: found synthetic nested class names for descriptor ${thisDescriptor.name}: $syntheticNames ")
              result.addAll(syntheticNames.map(Name::identifier))
            }
          }
        }
        null
      },
      getSyntheticFunctionNames = { thisDescriptor ->
        val result = arrayListOf<Name>()
        println("syntheticResolver.getSyntheticFunctionNames: $thisDescriptor")
        val classPsi = thisDescriptor.findPsi()
        if (classPsi != null && classPsi is KtClassOrObject) {
          classPsi.containingFile.safeAs<KtFile>()?.let { ktFile ->
            val (file, transformations) = processKtFile(ktFile, quoteFactory, match, map)
            val transformedFile = transformFile(file, transformations)
            transformedFile.findDescendantOfType<KtClass> { it.name == classPsi.name }?.let { ktClass ->
              val generatedNames = ktClass.declarations.filterIsInstance<KtFunction>().mapNotNull { it.name }
              val originalNames = classPsi.declarations.filterIsInstance<KtFunction>().mapNotNull { it.name }
              val syntheticNames = generatedNames - originalNames
              println("meta.syntheticResolver: found synthetic function names for descriptor ${thisDescriptor.name}: $syntheticNames ")
              result.addAll(syntheticNames.map(Name::identifier))
            }
          }
        }
        result
      }
    )
  } ?: ExtensionPhase.Empty

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
    } else declarationScopeProvider.getResolutionScopeForDeclaration(ktClassOrObject) ,
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


fun ClassDescriptor.createSuperType(reference: KtTypeReference): KotlinType {
  val projectionType = Variance.INVARIANT
  val types = reference.typeElement?.typeArgumentsAsTypes?.map { argReference ->
    TypeProjectionImpl(projectionType, createSuperType(argReference))
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

inline fun <reified K : KtElement> CompilerContext.updateFiles(result: java.util.ArrayList<KtFile>, fileMutations: List<Pair<KtFile, java.util.ArrayList<QuoteTransformation<K>>>>) {
  fileMutations.forEach { (file, mutations) ->
    val newFile = updateFile(mutations, file)
    result.replaceFiles(file, newFile)
  }
}

inline fun <reified K : KtElement> CompilerContext.updateFile(mutations: java.util.ArrayList<QuoteTransformation<K>>, file: KtFile): KtFile =
  if (mutations.isNotEmpty()) {
    transformFile(file, mutations)
  } else file

inline fun <reified K : KtElement> CompilerContext.transformFile(ktFile: KtFile, mutations: java.util.ArrayList<QuoteTransformation<K>>): KtFile {
  val newSource = ktFile.sourceWithTransformations(mutations)
  val newFile = changeSource(ktFile, newSource)
  ktFile.printDiff(newSource)
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
    ktPsiElementFactory.createFile(newSource)
  }!!