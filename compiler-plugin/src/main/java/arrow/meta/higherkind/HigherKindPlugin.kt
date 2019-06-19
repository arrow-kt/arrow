package arrow.meta.higherkind

import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaCompilerPlugin
import com.google.auto.service.AutoService
import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.psi.PsiClass
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.incremental.components.LookupTracker
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.util.transformDeclarationsFlat
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.types.typeUtil.isInterface

@AutoService(ComponentRegistrar::class)
class HigherKindPlugin : MetaCompilerPlugin {
  override fun intercept(): List<ExtensionPhase> =
    meta(
      enableIr(),
      analysys(
        doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
          val declaredElements: List<PsiClass> = files.first().allChildren.toList().filterIsInstance<PsiClass>()
          println("doAnalysis: $project, $module, $projectContext, $files, $bindingTrace, $componentProvider")
          null
        },
        analysisCompleted = { project, module, bindingTrace, files ->
          println("analysisCompleted: $project, $module, $bindingTrace, $files")
          null
        }
      ),
      syntheticResolver(
        addSyntheticSupertypes = { descriptor, supertypes ->
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
        //KtLightPackage(PsiManager.getInstance(project), FqName("arrow.sample"), ProjectScope.getAllScope(project))
        AddSupertypesPackageFragmentProvider(this, module)
      },
      classBuilderFactory { interceptedFactory, bindingContext, diagnostics ->
        println("classBuilderFactory: ")
        interceptedFactory
      },
      syntheticResolver(
        generatePackageSyntheticClasses = { descriptor: PackageFragmentDescriptor, name, ctx, declarationProvider, result ->
          val classDescriptor = result.firstOrNull { it.name == name }
          classDescriptor?.let {
            if (it.shouldGenerateKindMarker()) {
              val kindMarker = descriptor.kindMarker(it.fqNameSafe)
              println("${descriptor.name} : ${it.fqNameSafe} ~> generatePackageSyntheticClasses = $kindMarker")
              result.add(kindMarker)
            }
          }
        }
      ),
      IrGeneration { compilerContext, file, backendContext, bindingContext ->
        backendContext.run {
          file.transformDeclarationsFlat { decl ->
            val result = if (decl is IrClass && decl.descriptor.shouldGenerateKindMarker()) {
              val higherKindSuperType = irHigherKind(decl)
              decl.superTypes.add(higherKindSuperType)
              println("${decl.name} ~> IrGeneration.supertypes.add = $higherKindSuperType")
              val marker = kindMarker(decl)
              //decl.getPackageFragment()?.declarations?.add(marker)
              println("${decl.name} : ${marker.name} ~> IrGeneration.generation = $marker")
              listOf(decl, marker)
            } else {
              listOf(decl)
            }
            result
          }
        }
      }
    )

}

