package arrow.meta.higherkind

import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaComponentRegistrar
import arrow.meta.qq.classOrObject
import org.jetbrains.kotlin.psi.KtClass

class HigherKindPlugin : MetaComponentRegistrar {

  override fun intercept(): List<ExtensionPhase> =
    meta(
      classOrObject({
        """
          |$modality $visibility class $name<$typeParametersWithVariance>($valueParameters): $supertypes {
          |  $body
          |}
          |  """
      }) { ktClass ->
        if (isHigherKindedType(ktClass)) {
          listOfNotNull(
            /** Kind Marker **/
            "class For$name private constructor() { companion object }",
            /** Single arg type alias **/
            "typealias ${name}Of<$typeParameters> = arrow.Kind${ktClass.kindAritySuffix}<For$name, $typeParameters>",
            /** generate partial aliases if this kind has > 1 type parameters **/
            if (ktClass.arity > 1)
              """
                |typealias ${name}PartialOf<${ktClass.partialTypeParameters}> = 
                |  arrow.Kind${ktClass.partialKindAritySuffix}<For$name, ${ktClass.partialTypeParameters}>
              """.trimMargin()
            else null
            ,
            /** Class redefinition with kinded super type **/
            """
              |$modality $visibility class $name<$typeParametersWithVariance>($valueParameters): ${name}Of<$typeParameters> {
              |  $body
              |}
              |"""
          )
        } else emptyList()
      }
    )

  private val KtClass.partialTypeParameters: String
    get() = typeParameters
      .dropLast(1)
      .joinToString(", ") {
        it.nameAsSafeName.identifier
      }

  private val KtClass.arity: Int
    get() = typeParameters.size

  private val KtClass.kindAritySuffix: String
    get() = arity.let { if (it > 1) "$it" else "" }

  private val KtClass.partialKindAritySuffix: String
    get() = (arity - 1).let { if (it > 1) "$it" else "" }

  private fun isHigherKindedType(ktClass: KtClass): Boolean =
    ktClass.fqName?.asString()?.startsWith("arrow.Kind") != true &&
      !ktClass.isAnnotation() &&
      ktClass.typeParameters.isNotEmpty()

}