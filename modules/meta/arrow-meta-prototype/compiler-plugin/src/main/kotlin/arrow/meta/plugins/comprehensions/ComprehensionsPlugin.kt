package arrow.meta.plugins.comprehensions

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.Transform
import arrow.meta.quotes.quote
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.before
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@ExperimentalContracts
val Meta.comprehensions: Plugin
  get() =
    "comprehensions" {
      meta(
        quote(KtDotQualifiedExpression::containsFxBlock) { fxExpression ->
          println("fxBlock: ${fxExpression.text}")
          Transform.replace(
            replacing = fxExpression,
            newDeclaration = toFlatMap(fxExpression).expression
          )
        }
      )
    }

private fun KtDotQualifiedExpression.containsFxBlock(): Boolean =
  findDescendantOfType<KtBlockExpression> { it.isFxBlock() } != null

@ExperimentalContracts
private fun KtExpression?.containsNestedFxBlock(): Boolean {
  contract {
    returns() implies (this@containsNestedFxBlock != null)
  }
  return this?.findDescendantOfType<KtBlockExpression> { it.isFxBlock() } != null
}



@ExperimentalContracts
private fun ElementScope.delegationToFlatMap(ktExpression: KtExpression): String? =
  ktExpression.findDescendantOfType<KtBlockExpression> { it.isFxBlock()}?.let {
    toFlatMap(it)
  }

@ExperimentalContracts
private fun ElementScope.toFlatMap(fxCall: KtDotQualifiedExpression): String =
  fxCall.findDescendantOfType<KtBlockExpression> { it.isFxBlock() }?.let {
    toFlatMap(it)
  } ?: ""


@ExperimentalContracts
private fun ElementScope.toFlatMap(fxBlock: KtBlockExpression): String {
  val nextBind = fxBlock.statements.filterIsInstance<KtProperty>().first()
  val (beforeBind, afterBind) =
    fxBlock.statements.filterNot { it == nextBind }.partition { it.before(nextBind) }
  val flatMap = toFlatMap(nextBind, afterBind).expression.value
  val newStatements = (beforeBind + flatMap).filterNotNull()
  return newStatements.joinToString("\n") { it.text }
}

@ExperimentalContracts
private fun ElementScope.toFlatMap(bind: KtProperty, remaining: List<KtExpression>): String {
  val target = bind.delegateExpression
  val targetSource = when {
    target.containsNestedFxBlock() -> delegationToFlatMap(target)
    else -> target.text
  }
  val argName = bind.name
  val typeName = bind.typeReference?.let { ": ${it.text}" } ?: ""
  return """|${targetSource}.flatMap { $argName $typeName -> 
            |  ${toFlatMap(remaining)}  
            |}
            |"""
}

@ExperimentalContracts
private fun ElementScope.toFlatMap(remaining: List<KtExpression>): String =
  when {
    remaining.isEmpty() -> ""
    else -> {
      val head = remaining[0]
      val tail = remaining.drop(1)
      when {
        head.isBinding() -> toFlatMap(head, tail)
        remaining.size == 1 -> head.returningJust()
        else -> head.text + "\n" + toFlatMap(tail)
      }
    }
  }


@ExperimentalContracts
private fun KtExpression?.isBinding(): Boolean {
  contract {
    returns() implies (this@isBinding is KtProperty)
    returns() implies (this@isBinding != null)
  }
  return this is KtProperty && hasDelegate()
}

//TODO rewrite to return a DotQualifiedExpression which is the flatMap call application
//TODO with a fold over the statements where the dotqualified expression is applied over ktelement and nesting recursively

private fun KtElement?.returningJust(): String =
  this?.let {
    it.getParentOfType<KtCallExpression>(true).generateJust(it.text)
  }.orEmpty()

fun KtCallExpression?.generateJust(value: String): String =
  this?.parent?.firstChild?.text?.let { "$it.just($value)" }
    ?: "just($value)"

private fun KtBlockExpression.isFxBlock(): Boolean {
  val result = statements.any { it is KtProperty && it.hasDelegate() } &&
    getParentOfType<KtCallExpression>(true)?.firstChild.safeAs<KtReferenceExpression>()?.text == "fx"
  println("isFxBlock ${this.text}: $result")
  return result
}
