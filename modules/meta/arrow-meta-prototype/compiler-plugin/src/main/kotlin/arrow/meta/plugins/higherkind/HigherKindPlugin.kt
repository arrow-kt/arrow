package arrow.meta.plugins.higherkind

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.quotes.Transform
import arrow.meta.quotes.ScopedList
import arrow.meta.quotes.classOrObject
import arrow.meta.quotes.ktClassNamed
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtTypeAlias
import org.jetbrains.kotlin.psi.KtTypeParameter

val Meta.higherKindedTypes: Plugin
  get() =
    "higherKindedTypes" {
      meta(
        registerKindAwareTypeChecker(),
        classOrObject(::isHigherKindedType) { c ->
          println("Processing Higher Kind: ${c.name}: ${c.superTypeIsSealedInFile()}")
          Transform.replace(c, listOfNotNull(
            /** Kind Marker **/
            /** Kind Marker **/
            "class For$name private constructor() { companion object }".`class`,
            /** Single arg type alias **/
            /** Single arg type alias **/
            "typealias ${name}Of<${`(typeParameters)`.invariant()}> = arrow.Kind${c.kindAritySuffix}<For$name, ${`(typeParameters)`.invariant()}>".declaration<KtTypeAlias>(),
            /** KindedJ Support **/
            /** KindedJ Support **/
            if (c.arity < 5)
              "typealias ${name}KindedJ<${`(typeParameters)`.invariant()}> = arrow.HkJ${c.kindAritySuffix}<For$name, ${`(typeParameters)`.invariant()}>".declaration<KtTypeAlias>()
            else null,
            """|fun <${`(typeParameters)`.invariant(true)}> ${name}Of<${`(typeParameters)`.invariant()}>.fix(): $name<${`(typeParameters)`.invariant()}> =
               |  this as $name<${`(typeParameters)`.invariant()}>
               |""".function,
            /** generate partial aliases if this kind has > 1 type parameters **/
            /** generate partial aliases if this kind has > 1 type parameters **/
            if (c.arity > 1)
              "typealias ${name}PartialOf<${c.partialTypeParameters}> = arrow.Kind${c.partialKindAritySuffix}<For$name, ${c.partialTypeParameters}>".declaration<KtTypeAlias>()
            else null,
            /** Class redefinition with kinded super type **/
            /** Class redefinition with kinded super type **/
            """|$`@annotationEntries` $kind $name $`(typeParameters)` $`(valueParameters)` : ${supertypes.."${name}Of<${`(typeParameters)`.invariant()}>"} {
               |  $body
               |}
               |""".`class`
          )
          )
        }
      )
    }


private fun ScopedList<KtTypeParameter>.invariant(constrained: Boolean = false): String =
  value.joinToString {
    it.text
      .replace("out ", "")
      .replace("in ", "").let { s ->
        if (constrained) s
        else s.replace("(.*):(.*)".toRegex(), "$1")
      }.trim()
  }

private val KtClass.partialTypeParameters: String
  get() = typeParameters
    .dropLast(1)
    .joinToString(separator = ", ") {
      it.nameAsSafeName.identifier
    }

private val KtClass.arity: Int
  get() = typeParameters.size

private val KtClass.kindAritySuffix: String
  get() = arity.let { if (it > 1) "$it" else "" }

private val KtClass.partialKindAritySuffix: String
  get() = (arity - 1).let { if (it > 1) "$it" else "" }

fun isHigherKindedType(ktClass: KtClass): Boolean =
  ktClass.annotationEntries.any { it.text == "@higherkind" } &&
    ktClass.fqName?.asString()?.startsWith("arrow.Kind") != true &&
    !ktClass.isAnnotation() &&
    !ktClass.isNested() &&
    !ktClass.superTypeIsSealedInFile() &&
    ktClass.typeParameters.isNotEmpty() &&
    ktClass.parent is KtFile

private fun KtClass.superTypeIsSealedInFile(): Boolean =
  superTypeListEntries.isNotEmpty() &&
    superTypeListEntries.any {
      val className = it.text?.substringBefore("<")
      it.containingKtFile.ktClassNamed(className) != null
    }

private fun KtClass.isNested(): Boolean =
  parent is KtClassOrObject

val kindName: FqName = FqName("arrow.Kind")

val FqName.kindTypeAliasName: Name
  get() {
    val segments = pathSegments()
    val simpleName = segments.lastOrNull() ?: Name.special("index not ready")
    return Name.identifier("${simpleName}Of")
  }
