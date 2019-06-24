package arrow.meta.typeclasses

import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaComponentRegistrar
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptorVisitor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PackageViewDescriptor
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.descriptors.impl.DeclarationDescriptorVisitorEmptyBodies
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.descriptorUtil.getAllSuperClassifiers
import org.jetbrains.kotlin.resolve.descriptorUtil.getAllSuperclassesWithoutAny
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperInterfaces
import org.jetbrains.kotlin.resolve.lazy.LazyClassContext
import org.jetbrains.kotlin.resolve.lazy.declarations.PackageMemberDeclarationProvider
import org.jetbrains.kotlin.types.KotlinType

class TypeClassesComponentRegistrar: MetaComponentRegistrar {
  override fun intercept(): List<ExtensionPhase> =
    meta(
      enableIr(),
      syntheticResolver(
        generatePackageSyntheticClasses = {
          packageFragmentDescriptor: PackageFragmentDescriptor,
          name: Name,
          ctx: LazyClassContext,
          declarationProvider: PackageMemberDeclarationProvider,
          result: MutableSet<ClassDescriptor> ->


          val extensionDeclaration = result.firstOrNull()
          val annotationDescriptor: AnnotationDescriptor? = result.firstOrNull()?.annotations?.findAnnotation(FqName("arrow.extension"))
          annotationDescriptor?.let {
            if (extensionDeclaration != null) {
              val typeClass = extensionDeclaration.getSuperInterfaces().firstOrNull {
                it.declaredTypeParameters.isNotEmpty()
              }
              typeClass?.let {
                val dataType: KotlinType =
                  extensionDeclaration.typeConstructor.supertypes.first().arguments.first().type
                val dataTypeDescriptor = dataType.constructor.declarationDescriptor
                println("Found: ${extensionDeclaration.name} for typeclass : $it, data type: $dataTypeDescriptor")
              }
            }
          }
          println("syntheticResolver.generatePackageSyntheticClasses: result: $result")
        }
      )
    )

}

