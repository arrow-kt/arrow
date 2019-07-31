package arrow.meta.qq

import arrow.meta.extensions.CompilerContext
import org.jetbrains.kotlin.cli.jvm.compiler.NoScopeRecordCliBindingTrace
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.context.GlobalContext
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.Visibility
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.ClassConstructorDescriptorImpl
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtModifierList
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtPureClassOrObject
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.KtSuperTypeListEntry
import org.jetbrains.kotlin.psi.synthetics.SyntheticClassOrObjectDescriptor
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.DescriptorFactory
import org.jetbrains.kotlin.resolve.descriptorUtil.parents
import org.jetbrains.kotlin.resolve.lazy.AbsentDescriptorHandler
import org.jetbrains.kotlin.resolve.lazy.LazyClassContext
import org.jetbrains.kotlin.resolve.lazy.LazyDeclarationResolver
import org.jetbrains.kotlin.resolve.lazy.ResolveSession
import org.jetbrains.kotlin.resolve.lazy.TopLevelDescriptorProvider
import org.jetbrains.kotlin.resolve.lazy.data.KtClassInfoUtil
import org.jetbrains.kotlin.resolve.lazy.data.KtClassOrObjectInfo
import org.jetbrains.kotlin.resolve.lazy.declarations.PsiBasedClassMemberDeclarationProvider
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyClassDescriptor
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyClassMemberScope
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyPackageDescriptor
import org.jetbrains.kotlin.resolve.scopes.LexicalScopeImpl
import org.jetbrains.kotlin.resolve.scopes.LexicalScopeKind
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.resolve.source.KotlinSourceElement
import org.jetbrains.kotlin.storage.LockBasedLazyResolveStorageManager
import org.jetbrains.kotlin.util.slicedMap.SlicedMapImpl

class MetaReplacedClassDescriptor(
  val compilerContext: CompilerContext,
  val lazyClassContext: LazyClassContext,
  containingDeclaration: DeclarationDescriptor,
  val classOrObject: KtClassOrObject
) : LazyClassDescriptor(
  lazyClassContext,
  containingDeclaration,
  classOrObject.nameAsSafeName,
  KtClassInfoUtil.createClassOrObjectInfo(classOrObject),
  false
) {

  val classInfo: KtClassOrObjectInfo<out KtClassOrObject> = KtClassInfoUtil.createClassOrObjectInfo(classOrObject)

  val memberScope: LazyClassMemberScope = LazyClassMemberScope(
    lazyClassContext,
    PsiBasedClassMemberDeclarationProvider(lazyClassContext.storageManager, classInfo),
    this,
    lazyClassContext.trace
  )

  val constructors: List<ClassConstructorDescriptorImpl> = listOf(DescriptorFactory.createPrimaryConstructorForObject(this, SourceElement.NO_SOURCE))

  override fun getVisibility(): Visibility = Visibilities.PUBLIC
  override fun getUnsubstitutedMemberScope(): MemberScope = memberScope
  override fun getConstructors(): Collection<ClassConstructorDescriptor> = constructors

}

class MetaLazyDeclarationResolver(
  val compilerContext: CompilerContext,
  val globalContext: GlobalContext,
  val delegationTrace: BindingTrace,
  val lazyClassContext: LazyClassContext,
  val topLevelDescriptorProvider: TopLevelDescriptorProvider,
  private val absentDescriptorHandler: AbsentDescriptorHandler) : LazyDeclarationResolver(globalContext, delegationTrace, topLevelDescriptorProvider, absentDescriptorHandler) {

//  private fun packageSyntheticDeclaration(
//    packageDescriptor: LazyPackageDescriptor,
//    thisDescriptor: LazyClassDescriptor
//  ): KtPureClassOrObject =
//    object : KtPureClassOrObject {
//      override fun getName(): String? = thisDescriptor.name.asString()
//      override fun isLocal(): Boolean = false
//
//      override fun getDeclarations(): List<KtDeclaration> = emptyList()
//      override fun getSuperTypeListEntries(): List<KtSuperTypeListEntry> = emptyList()
//      override fun getCompanionObjects(): List<KtObjectDeclaration> = emptyList()
//
//      override fun hasExplicitPrimaryConstructor(): Boolean = false
//      override fun hasPrimaryConstructor(): Boolean = false
//      override fun getPrimaryConstructor(): KtPrimaryConstructor? = null
//      override fun getPrimaryConstructorModifierList(): KtModifierList? = null
//      override fun getPrimaryConstructorParameters(): List<KtParameter> = emptyList()
//      override fun getSecondaryConstructors(): List<KtSecondaryConstructor> = emptyList()
//
//      override fun getPsiOrParent(): KtElement = (packageDescriptor.findPsi() ?: parent!!) as KtElement
//      override fun getParent(): PsiElement? = packageDescriptor.parents.first().findPsi()
//      @Suppress("USELESS_ELVIS")
//      override fun getContainingKtFile(): KtFile =
//        // in theory `containingKtFile` is `@NotNull` but in practice EA-114080
//        psiOrParent.containingKtFile ?: throw IllegalStateException("containingKtFile was null for $parent of ${parent?.javaClass}")
//
//      override fun getBody(): KtClassBody? = null
//    }
//
//  fun LazyClassDescriptor.syntheticClassOrObjectDescriptor(transformation: KtClassOrObject, packageDescriptor: LazyPackageDescriptor) =
//        SyntheticClassOrObjectDescriptor(
//        c = lazyClassContext,
//        parentClassOrObject = packageSyntheticDeclaration(packageDescriptor, this),
//        containingDeclaration = packageDescriptor,
//        name = transformation.nameAsSafeName,
//        source = KotlinSourceElement(transformation),
//        outerScope = LexicalScopeImpl(
//          parent = scopeForClassHeaderResolution,
//          ownerDescriptor = packageDescriptor,
//          isOwnerDescriptorAccessibleByLabel = false,
//          implicitReceiver = null,
//          kind = LexicalScopeKind.SYNTHETIC,
//          redeclarationChecker = MetaLocalRedeclarationChecker,
//          initialize = {
//          }
//        ),  //TODO replace with transformation values
//        modality = Modality.FINAL, //TODO replace with transformation values
//        visibility = Visibilities.PUBLIC, //TODO replace with transformation values
//        annotations = Annotations.EMPTY, //TODO replace with transformation values
//        constructorVisibility = Visibilities.PUBLIC, //TODO replace with transformation values
//        kind = ClassKind.CLASS, //TODO replace with transformation values
//        isCompanionObject = false //TODO replace with transformation values
//      ).also {
//        it.initialize()
//      }

  val newClass: KtClass = compilerContext.ktPsiElementFactory.createClass("""
            package consumer
            class FooClass { 
              fun foo(): Unit = println("BOOM replaced!")
            }
            """.trimIndent())

  override fun getClassDescriptor(classOrObject: KtClassOrObject, location: LookupLocation): ClassDescriptor {
    println("MetaLazyDeclarationResolver.getClassDescriptor: ${classOrObject.name}")
    return if (classOrObject.name == "FooClass") {
      val foundDescriptor = lazyClassContext.trace.get(BindingContext.CLASS, newClass)
      if (foundDescriptor == null) {
        val lazyPackageDescriptor = topLevelDescriptorProvider.getPackageFragment(FqName("consumer"))!!
        val descriptor = MetaReplacedClassDescriptor(
          compilerContext,
          lazyClassContext,
          lazyPackageDescriptor,
          newClass
        )//.syntheticClassOrObjectDescriptor(classOrObject, lazyPackageDescriptor)
        lazyClassContext.trace.record(BindingContext.CLASS, newClass, descriptor)
        val traceField = lazyClassContext.trace.javaClass.getDeclaredField("trace")
        traceField.isAccessible = true
        val currentTrace: NoScopeRecordCliBindingTrace = traceField.get(lazyClassContext.trace) as NoScopeRecordCliBindingTrace
        val mapField = currentTrace.javaClass.superclass.superclass.getDeclaredField("map")
        mapField.isAccessible = true
        val sliceMap: SlicedMapImpl = mapField.get(currentTrace) as SlicedMapImpl
        val alwaysAllowRewriteField = sliceMap.javaClass.getDeclaredField("alwaysAllowRewrite")
        alwaysAllowRewriteField.isAccessible = true
        alwaysAllowRewriteField.set(sliceMap, true)
        descriptor
      } else foundDescriptor
    } else super.getClassDescriptor(classOrObject, location)

//    val descriptor = super.getClassDescriptor(classOrObject, location)
//    val classQuote = compilerContext.quotes[classOrObject]
//    return if (classQuote != null) {
//      val transformation: Transformation<ClassDescriptor>? = classQuote.process(descriptor)
//      return if (transformation != null) {
//        val mutatingDescriptor = descriptor as LazyClassDescriptor
//        val descriptorSourceField = mutatingDescriptor.javaClass.getDeclaredField("classOrObject")
//        descriptorSourceField.isAccessible = true
//        val newBody = (transformation.newDescriptor.source as KotlinSourceElement).psi
//        descriptorSourceField.set(mutatingDescriptor, newBody)
//        mutatingDescriptor
//      } else descriptor
//    } else descriptor
  }

  override fun getClassDescriptorIfAny(classOrObject: KtClassOrObject, location: LookupLocation): ClassDescriptor? {
    println("MetaLazyDeclarationResolver.getClassDescriptorIfAny: ${classOrObject.name}")
    return super.getClassDescriptorIfAny(classOrObject, location)
  }
}