package arrow.meta.higherkind

import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptorWithResolutionScopes
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.SupertypeLoopChecker
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.descriptors.Visibility
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.ClassDescriptorBase
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.DescriptorFactory
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.lazy.LazyClassContext
import org.jetbrains.kotlin.resolve.lazy.descriptors.ClassResolutionScopesSupport
import org.jetbrains.kotlin.resolve.scopes.LexicalScope
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.types.AbstractClassTypeConstructor
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeConstructor

/*
 * This class introduces all attributes that are needed for synthetic classes/object so far.
 * This list may grow in the future, adding more constructor parameters.
 * This class has its own synthetic declaration inside.
 */
class SyntheticPackageClassOrObjectDescriptor(
  c: LazyClassContext,
  containingDeclaration: PackageFragmentDescriptor,
  name: Name,
  source: SourceElement,
  outerScope: LexicalScope,
  private val modality: Modality,
  private val visibility: Visibility,
  override val annotations: Annotations,
  constructorVisibility: Visibility,
  private val kind: ClassKind,
  private val isCompanionObject: Boolean
) : ClassDescriptorBase(c.storageManager, containingDeclaration, name, source, false), ClassDescriptorWithResolutionScopes {


  private val thisDescriptor: SyntheticPackageClassOrObjectDescriptor get() = this // code readability

  private lateinit var typeParameters: List<TypeParameterDescriptor>
  public var secondaryConstructors: List<ClassConstructorDescriptor> = emptyList()

  private val typeConstructor = SyntheticTypeConstructor(c.storageManager)
  private val resolutionScopesSupport = ClassResolutionScopesSupport(thisDescriptor, c.storageManager, c.languageVersionSettings, { outerScope })
  private val syntheticSupertypes =
    mutableListOf<KotlinType>().apply { c.syntheticResolveExtension.addSyntheticSupertypes(thisDescriptor, this) }
  private val _unsubstitutedPrimaryConstructor =
    c.storageManager.createLazyValue { createUnsubstitutedPrimaryConstructor(constructorVisibility) }

  @JvmOverloads
  fun initialize(
    typeParameters: List<TypeParameterDescriptor> = emptyList()
  ) {
    this.typeParameters = typeParameters
  }

  override fun getModality() = modality
  override fun getVisibility() = visibility
  override fun getKind() = kind
  override fun isCompanionObject() = isCompanionObject
  override fun isInner() = false
  override fun isData() = false
  override fun isInline() = false
  override fun isExpect() = false
  override fun isActual() = false

  override fun getCompanionObjectDescriptor(): ClassDescriptorWithResolutionScopes? = null
  override fun getTypeConstructor(): TypeConstructor = typeConstructor
  override fun getUnsubstitutedPrimaryConstructor() = _unsubstitutedPrimaryConstructor()
  override fun getConstructors() = listOf(_unsubstitutedPrimaryConstructor()) + secondaryConstructors
  override fun getDeclaredTypeParameters() = typeParameters
  override fun getStaticScope() = MemberScope.Empty
  override fun getUnsubstitutedMemberScope() = MemberScope.Empty
  override fun getSealedSubclasses() = emptyList<ClassDescriptor>()

  init {
    assert(modality != Modality.SEALED) { "Implement getSealedSubclasses() for this class: ${this::class.java}" }
  }

  override fun getDeclaredCallableMembers(): List<CallableMemberDescriptor> =
    DescriptorUtils.getAllDescriptors(unsubstitutedMemberScope).filterIsInstance<CallableMemberDescriptor>().filter {
      it.kind != CallableMemberDescriptor.Kind.FAKE_OVERRIDE
    }

  override fun getScopeForClassHeaderResolution(): LexicalScope = resolutionScopesSupport.scopeForClassHeaderResolution()
  override fun getScopeForConstructorHeaderResolution(): LexicalScope = resolutionScopesSupport.scopeForConstructorHeaderResolution()
  override fun getScopeForCompanionObjectHeaderResolution(): LexicalScope =
    resolutionScopesSupport.scopeForCompanionObjectHeaderResolution()

  override fun getScopeForMemberDeclarationResolution(): LexicalScope = resolutionScopesSupport.scopeForMemberDeclarationResolution()
  override fun getScopeForStaticMemberDeclarationResolution(): LexicalScope =
    resolutionScopesSupport.scopeForStaticMemberDeclarationResolution()

  override fun getScopeForInitializerResolution(): LexicalScope =
    throw UnsupportedOperationException("Not supported for synthetic class or object")

  override fun toString(): String = "synthetic class " + name.toString() + " in " + containingDeclaration

  private fun createUnsubstitutedPrimaryConstructor(constructorVisibility: Visibility): ClassConstructorDescriptor {
    val constructor = DescriptorFactory.createPrimaryConstructorForObject(thisDescriptor, source)
    constructor.visibility = constructorVisibility
    constructor.returnType = getDefaultType()
    return constructor
  }

  private inner class SyntheticTypeConstructor(storageManager: StorageManager) : AbstractClassTypeConstructor(storageManager) {
    override fun getParameters(): List<TypeParameterDescriptor> = typeParameters
    override fun isDenotable(): Boolean = true
    override fun getDeclarationDescriptor(): ClassDescriptor = thisDescriptor
    override fun computeSupertypes(): Collection<KotlinType> = syntheticSupertypes
    override val supertypeLoopChecker: SupertypeLoopChecker = SupertypeLoopChecker.EMPTY
  }

}

