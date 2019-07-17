package arrow.meta.higherkind

import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaComponentRegistrar
import arrow.meta.qq.classOrObject
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.psiUtil.modalityModifier

class HigherKindPlugin : MetaComponentRegistrar {

  private val ArrowKind: FqName = FqName("arrow.Kind")

  override fun intercept(): List<ExtensionPhase> =
    meta(
      classOrObject({
        """
          |$modality $visibility class $name<$typeArgsWithVariance>($params): $supertypes {
          |  $body
          |}
          |  """.trimMargin()
      }) { ktClass ->
        if (isHigherKindedType(ktClass)) {
          listOf(
            /** Kind Marker **/
            "class For$name",
            /** Single arg type alias **/
            "typealias ${name}Of<$typeArgs> = arrow.Kind<For$name, $typeArgs>",
            /** Class redefinition with kinded super type **/
            """
              |$modality $visibility class $name<$typeArgsWithVariance>($params): ${name}Of<$typeArgs> {
              |  $body
              |}
              |"""
          ).map { it.trimMargin() }
        } else emptyList()
      }
    )

  private fun isHigherKindedType(ktClass: KtClass): Boolean {
    return ktClass.fqName != ArrowKind &&
      !ktClass.isAnnotation() &&
      ktClass.typeParameters.isNotEmpty()
  }

}