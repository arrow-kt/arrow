package arrow.meta.higherkind

import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaComponentRegistrar
import arrow.meta.qq.classOrObject
import org.jetbrains.kotlin.ir.util.transformDeclarationsFlat
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.psiUtil.modalityModifier
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker

class HigherKindPlugin : MetaComponentRegistrar {

  private val ArrowKind: FqName = FqName("arrow.Kind")

  override fun intercept(): List<ExtensionPhase> =
    meta(
      enableIr(),
      compilerContextService(),
      registerKindAwareTypeChecker(),
      classOrObject({
        """
          |class $name<$typeArgsWithVariance>($params): $supertypes {
          |  $body
          |}
          |  """.trimMargin()
      }) { ktClass ->
        if (isHigherKindedType(ktClass)) {
          listOf(
            "class For$name", // Kind marker
            "typealias ${name}Of<$typeArgs> = arrow.Kind<For$name, $typeArgs>", // type alias
            // class redefinition with added supertype
            """
              |${ktClass.modalityModifier()?.text} class $name<$typeArgsWithVariance>($params): ${name}Of<$typeArgs> {
              |  $body
              |}
              |"""
          ).map { it.trimMargin() }
        } else emptyList()
      },
      IrGeneration { compilerContext, file, backendContext, bindingContext ->
        println("~> IrGeneration")
        backendContext.run {
          file.transformDeclarationsFlat { decl ->
            println("IR: ${decl.descriptor.javaClass}")
            listOf(decl)
          }
        }
      }
    )

  private fun registerKindAwareTypeChecker(): ExtensionPhase.StorageComponentContainer =
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
    )

  private fun isHigherKindedType(ktClass: KtClass): Boolean {
    return ktClass.fqName != ArrowKind &&
      !ktClass.isAnnotation() &&
      ktClass.typeParameters.isNotEmpty()
  }

}