package arrow.meta.higherkind

import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaComponentRegistrar
import com.google.auto.service.AutoService
import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.incremental.components.LookupTracker
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.util.transformDeclarationsFlat
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker
import org.jetbrains.kotlin.types.typeUtil.isInterface

class HigherKindComponentRegistrar : MetaComponentRegistrar {
  override fun intercept(): List<ExtensionPhase> =
    meta(
      enableIr(),
      syntheticScopes(
        getSyntheticScopes = { moduleDescriptor, javaSyntheticPropertiesScope ->
          println("getSyntheticScopes")
          listOf()
        }
      ),
      preprocessedVirtualFileFactory(
        createPreprocessedFile = { file: VirtualFile? ->
          println("preprocessedVirtualFileFactory: $file")
          file
        }
      ),
      storageComponent(
        registerModuleComponents = { container, platform, moduleDescriptor ->
          println("registerModuleComponents")
          val defaultTypeChecker = KotlinTypeChecker.DEFAULT
          if (defaultTypeChecker !is KindAwareTypeChecker) { //nasty hack ahead to circumvent the ability to replace the Kotlin type checker
            val defaultTypeCheckerField = KotlinTypeChecker::class.java.getDeclaredField("DEFAULT")
            setFinalStatic(defaultTypeCheckerField, KindAwareTypeChecker(defaultTypeChecker))
          }
        },
        check = { declaration, descriptor, context ->
          println("check")
        }
      ),
      syntheticResolver(
        addSyntheticSupertypes = { descriptor, supertypes ->
          println("${descriptor.name} ~> addSyntheticSupertypes")
          storeDescriptor(descriptor) //store the target descriptor for a later phase
          val isSubtype = supertypes.any {
            !(it.constructor.declarationDescriptor?.defaultType?.isInterface() ?: false)
          }
          if (!isSubtype && descriptor.shouldApplyKind()) {
            val hk = descriptor.higherKind()
            println("${descriptor.name} ~> addSyntheticSupertypes = $hk")
            supertypes.add(hk)
          } else {
            // println("skipped: " + descriptor.name)
          }
        }
      ),
      packageFragmentProvider { project: Project, module: ModuleDescriptor, storageManager: StorageManager, trace: BindingTrace, moduleInfo: ModuleInfo?, lookupTracker: LookupTracker ->
        println("${module} ~> packageFragmentProvider")
        AddSupertypesPackageFragmentProvider(this, module)
      },
      classBuilderFactory { interceptedFactory, bindingContext, diagnostics ->
        println("classBuilderFactory: ")
        interceptedFactory
      },
      syntheticResolver(
        generatePackageSyntheticClasses = { descriptor: PackageFragmentDescriptor, name, ctx, declarationProvider, result ->
          println("${name} ~> generatePackageSyntheticClasses")
          val classDescriptorCandidate = result.firstOrNull { it.name == name }
          val classDescritorKinded: ClassDescriptor? = classDescriptorCandidate //classDescriptorCandidate?.replaceKinds(ktPsiElementFactory)
          classDescritorKinded?.let {
            if (it.shouldGenerateKindMarker()) {
              val kindMarker = descriptor.kindMarker(it.fqNameSafe)
              println("${descriptor.name} : ${it.fqNameSafe} ~> generatePackageSyntheticClasses = $kindMarker")
              result.remove(classDescriptorCandidate)
              result.add(it)
              result.add(kindMarker)
            }
          }
        }
      ),
      IrGeneration { compilerContext, file, backendContext, bindingContext ->
        println("~> IrGeneration")
        backendContext.run {
          file.transformDeclarationsFlat { decl ->
            val result = if (decl is IrClass && decl.descriptor.shouldGenerateKindMarker()) {
              val higherKindSuperType = irHigherKind(decl)
              decl.superTypes.add(higherKindSuperType)
              println("${decl.name} ~> IrGeneration.supertypes.add = $higherKindSuperType")
              val marker = kindMarker(decl)
              //val typeAlias = compilerContext.irKindTypeAlias(decl)
              println("${decl.name} : ${marker.name} ~> IrGeneration.generation = $marker")
              listOf(decl, marker) //, typeAlias)
            } else {
              listOf(decl)
            }
            result
          }
        }
      }
    )

}

