package arrow.meta.quotes

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression

/**
 * A binary expression with an operator can occur in the following instances:
 *  - from an expression
 *  - from an element
 *  - from an argument
 *
 *  Since there are so many ways an operator can be detected, it may be beneficial to drill down
 *  using a more generalized visitor as the top Meta operation....
 *
 *  Below is starting in the wrong place so this will need to start in a more broad area
 */
fun Meta.binaryOper(
  match: KtBinaryExpression.() -> Boolean,
  map: BinaryExpressionScope.(KtBinaryExpression) -> Transform<KtBinaryExpression>
): ExtensionPhase =
  quote(match, map) { BinaryExpressionScope(it) } // How can I drill down into the right scope here?

// TODO need to rescope up to the ??? -> binary operator -> expression -> operation reference expression

class CallExpressionScope(
  override val value: KtCallExpression

): Scope<KtCallExpression>(value)

// TODO this binary expression needs to be drilled down to primaries or objects, so this will have to be filtered
class BinaryExpressionScope(
  override val value: KtBinaryExpression,
  val left: ExpressionScope? = value.left?.let { ExpressionScope(it) },
  val right: ExpressionScope? = value.right?.let { ExpressionScope(it) },
  val operationReference: OperationReferenceExpressionScope? = value.operationReference.let { OperationReferenceExpressionScope(it) }
): Scope<KtBinaryExpression>(value)

class ExpressionScope(
  override val value: KtExpression
  // val type: KtTypeReference
): Scope<KtExpression>(value)

class OperationReferenceExpressionScope(
  override val value: KtOperationReferenceExpression
): Scope<KtOperationReferenceExpression>(value)
