package arrow.meta.higherkind

import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaCompilerPlugin
import com.google.auto.service.AutoService
import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.container.ComponentProvider
import org.jetbrains.kotlin.container.get
import org.jetbrains.kotlin.container.getService
import org.jetbrains.kotlin.container.registerInstance
import org.jetbrains.kotlin.container.registerSingleton
import org.jetbrains.kotlin.container.useImpl
import org.jetbrains.kotlin.container.useInstance
import org.jetbrains.kotlin.context.ProjectContext
import org.jetbrains.kotlin.descriptors.DeclarationDescriptorWithVisibility
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.SupertypeLoopChecker
import org.jetbrains.kotlin.incremental.components.LookupTracker
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.util.transformDeclarationsFlat
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.DeclarationSignatureAnonymousTypeTransformer
import org.jetbrains.kotlin.resolve.QualifiedExpressionResolver
import org.jetbrains.kotlin.resolve.TypeResolver
import org.jetbrains.kotlin.resolve.calls.checkers.CallChecker
import org.jetbrains.kotlin.resolve.calls.checkers.CallCheckerContext
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValueFactory
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeConstructor
import org.jetbrains.kotlin.types.typeUtil.isInterface

/**
 * DescriptorResolver
 * AnalisysResult
 */
@AutoService(ComponentRegistrar::class)
class HigherKindPlugin : MetaCompilerPlugin {
  override fun intercept(): List<ExtensionPhase> =
    meta(
      enableIr(),
      storageComponent(
        registerModuleComponents = { container, platform, moduleDescriptor ->
          container.registerInstance(object : CallChecker {
            override fun check(resolvedCall: ResolvedCall<*>, reportOn: PsiElement, context: CallCheckerContext) {
              println("check: `${reportOn.text}` ${resolvedCall.resultingDescriptor.name}")
            }
          })
          println("registerModuleComponents")
        },
        check = { declaration, descriptor, context ->
          println("check")
        }
      ),
//      resolveSession { ctx ->
//        println("resolveSession: $ctx")
//        ctx.setTypeResolver(with(ctx.typeResolver) {
//          TypeResolver(
//            annotationResolver = ctx.annotationResolver,
//            qualifiedExpressionResolver = ctx.qua,
//            moduleDescriptor = ctx.moduleDescriptor,
//            typeTransformerForTests = ctx.ty,
//            dynamicTypesSettings = ctx.dyn,
//            dynamicCallableDescriptors = ctx.dyn,
//            identifierChecker = ctx.ide,
//            platformToKotlinClassMap = ctx.pla,
//            languageVersionSettings = ctx.languageVersionSettings
//          )
//        })
//      },
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

