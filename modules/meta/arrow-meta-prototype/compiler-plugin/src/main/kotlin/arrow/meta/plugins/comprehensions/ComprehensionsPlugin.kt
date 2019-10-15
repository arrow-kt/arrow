package arrow.meta.plugins.comprehensions

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.Scope
import arrow.meta.quotes.Transform
import arrow.meta.quotes.orEmpty
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
        quote(KtDotQualifiedExpression::containsFxBlock) { fxExpression: KtDotQualifiedExpression ->
          println("fxBlock: ${fxExpression.text}")
          Transform.replace(
            replacing = fxExpression,
            newDeclaration = toFlatMap(fxExpression)
          )
        }
      )
    }


// findDescendantOfType ?? Helper for matching on nested  code??
private fun KtDotQualifiedExpression.containsFxBlock(): Boolean =
  findDescendantOfType(KtBlockExpression::isFxBlock) != null

@ExperimentalContracts
private fun KtExpression?.containsNestedFxBlock(): Boolean {
  contract {
    returns() implies (this@containsNestedFxBlock != null)
  }
  return this?.findDescendantOfType<KtBlockExpression> { it.isFxBlock() } != null
}

@ExperimentalContracts
private fun ElementScope.delegationToFlatMap(ktExpression: KtExpression): Scope<KtDotQualifiedExpression> =
  ktExpression.findDescendantOfType(KtBlockExpression::isFxBlock)?.let {
    toFlatMap(it)
  }.orEmpty()

@ExperimentalContracts
private fun ElementScope.toFlatMap(fxCall: KtDotQualifiedExpression): Scope<KtDotQualifiedExpression> =
  fxCall.findDescendantOfType(KtBlockExpression::isFxBlock)?.let {
    toFlatMap(it)
  }.orEmpty()

@ExperimentalContracts
private fun ElementScope.toFlatMap(fxBlock: KtBlockExpression): Scope<KtDotQualifiedExpression> {
  if (fxBlock.statements.size == 1)
    return toFlatMap(listOf(fxBlock.statements[0])).dotQualifiedExpression
  val nextBind = fxBlock.statements.filterIsInstance<KtProperty>().first()
  val (beforeBind, afterBind) =
    fxBlock.statements.filterNot { it == nextBind }.partition { it.before(nextBind) }
  val flatMap = toFlatMap(nextBind, afterBind).value
  val newStatements = (beforeBind + flatMap).filterNotNull()
  return newStatements.joinToString("\n") { it.text }.dotQualifiedExpression
}

@ExperimentalContracts
private fun ElementScope.toFlatMap(bind: KtProperty, remaining: List<KtExpression>): Scope<KtExpression> {
  val target = bind.delegateExpression
  val targetSource = when {
    target.containsNestedFxBlock() -> delegationToFlatMap(target)
    else -> target.text
  }
  val argName = bind.name
  val typeName = bind.typeReference?.let { ": ${it.text}" } ?: ""
  return """|${targetSource}.flatMap { $argName $typeName -> 
            |  ${toFlatMap(remaining)}  
            |}""".expression
}

@ExperimentalContracts
private tailrec fun ElementScope.toFlatMap(remaining: List<KtExpression>, prefix: String = ""): String =
  when {
    remaining.isEmpty() -> ""
    else -> {
      val head = remaining[0]
      val tail = remaining.drop(1)
      when {
        head.isBinding() -> prefix + toFlatMap(head, tail)
        remaining.size == 1 -> prefix + head.returningJust()
        else -> toFlatMap(tail, head.text + "\n")
      }
    }
  }

@ExperimentalContracts
fun KtExpression?.isBinding(): Boolean {
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

// Predicate to detect if nested code has `delegates and the surrounding block is `fx`.
// If it is found than we need to rewrite this block.
// => We haven't checked if it's `flatMap` or other delegates. Can we find out???
private fun KtBlockExpression.isFxBlock(): Boolean {
  val result = (isSingleExpression() || containsPropertyWithDelegate())
    && parentBlockIsfx()
  println("isFxBlock ${this.text}: $result")
  return result
}

// Look for `val a by io`
private fun KtBlockExpression.containsPropertyWithDelegate(): Boolean =
  statements.any { it is KtProperty && it.hasDelegate() }

private fun KtBlockExpression.isSingleExpression(): Boolean =
  statements.size == 1 && statements[0] is KtExpression

// If the parent block is `fx` than we're a comprehension
private fun KtBlockExpression.parentBlockIsfx(): Boolean =
  getParentOfType<KtCallExpression>(true)?.firstChild.safeAs<KtReferenceExpression>()?.text == "fx"
