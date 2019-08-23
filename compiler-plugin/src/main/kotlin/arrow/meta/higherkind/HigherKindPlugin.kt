package arrow.meta.higherkind

import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaComponentRegistrar
import arrow.meta.qq.classOrObject
import arrow.meta.utils.arity
import arrow.meta.utils.invariant
import arrow.meta.utils.isKinded
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile


val MetaComponentRegistrar.higherKindedTypes: List<ExtensionPhase>
  get() =
    meta(
      classOrObject(::isHigherKindedType) { c ->
        println("Processing Higher Kind: ${c.name}")
        listOfNotNull(
          /** Kind Marker **/
          "class For$name private constructor() { companion object }",
          /** Single arg type alias **/
          "typealias ${name}Of<${typeParameters.invariant}> = arrow.Kind${c.kindAritySuffix}<For$name, ${typeParameters.invariant}>",
          /** generate partial aliases if this kind has > 1 type parameters **/
          if (c.arity > 1)
            "typealias ${name}PartialOf<${c.partialTypeParameters}> = arrow.Kind${c.partialKindAritySuffix}<For$name, ${c.partialTypeParameters}>"
          else null,
          /** Class redefinition with kinded super type **/
          """
              |@Suppress("INCONSISTENT_TYPE_PARAMETER_VALUES")
              |$visibility $modality $kind $name<$typeParameters>($valueParameters) : ${if ((String::isNotEmpty)(supertypes.identifier)) ({ it: String -> "$it, " })(supertypes.identifier) else supertypes.identifier}${name}Of<${typeParameters.invariant}> {
              |  $body
              |}
              """.trimMargin()
        )
      }
    )

private val KtClass.partialTypeParameters: String
  get() = typeParameters
    .dropLast(1)
    .joinToString(separator = ", ") {
      it.nameAsSafeName.identifier
    }

private val KtClass.kindAritySuffix: String
  get() = arity.let { if (it > 1) "$it" else "" }

private val KtClass.partialKindAritySuffix: String
  get() = (arity - 1).let { if (it > 1) "$it" else "" }

private fun isHigherKindedType(ktClass: KtClass): Boolean =
  ktClass.isKinded() && !ktClass.isAnnotation() &&
    ktClass.typeParameters.isNotEmpty() &&
    ktClass.parent is KtFile

