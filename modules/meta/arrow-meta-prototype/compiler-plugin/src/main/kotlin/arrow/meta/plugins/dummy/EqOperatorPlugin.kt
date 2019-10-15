package arrow.meta.plugins.dummy

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.quotes.Transform
import arrow.meta.quotes.binaryOper
import org.jetbrains.kotlin.psi.KtBinaryExpression

const val EQEQ = "EQEQ"

val Meta.eq: Plugin
  get() =
    "Eq" {
      meta(
        binaryOper(
          match = { isEQEQ() },  // search for psi match
          map = { binaryOper: KtBinaryExpression ->
            println("interception binary operation for EQEQ operator: ${binaryOper.text}")
            val result = // mode of thought mostly left in string form right now
              """
                | {${left}.type}.run {   // types should be the same, so a function will need to verify this prior
                |   {${left}.value}.eqv({${right}.value})
                | }
                |
                |""".binaryExpression
            Transform.replace(binaryOper, result)
          }
        )
      )
      // ideSynethic[...]Resolution() // ide resolution work to alter text source (to be refactored later)
      // irCall( ) // alter resolution at the bytecode level via IR
    }

private fun KtBinaryExpression.isEQEQ(): Boolean =
  this.operationReference.isConventionOperator() && this.operationReference.operationSignTokenType?.value == EQEQ

// TODO
// private fun IrUtils.mapOperatorReferenceExpressionExtension(expression: )

