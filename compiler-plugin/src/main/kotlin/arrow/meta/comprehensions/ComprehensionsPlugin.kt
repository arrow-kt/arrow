package arrow.meta.comprehensions

 import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaComponentRegistrar
import arrow.meta.ir.irBody
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrStatementContainer
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@ExperimentalContracts
val MetaComponentRegistrar.comprehensions: List<ExtensionPhase>
  get() =
    meta(
      irBody { body ->
        if (body.hasBindings()) {
          body.rewriteBindAsFlatMap()
        }
        body
      }
    )

private fun IrStatementContainer.replaceStatements(newStatements: List<IrStatement>): IrBody {
  statements.clear()
  statements.addAll(newStatements)
  return this as IrBody
}

@ExperimentalContracts
private fun IrStatementContainer.flatBindRewrite(): List<IrStatement> =
  statements.foldIndexed(emptyList()) { n, acc, head ->
    if (head.isBinding()) {
      val bodyInFlatMap = statements.subList(n, statements.size - 1) // this should be replaced by the flatMap call
      acc + bodyInFlatMap
    } else acc + head
  }

@ExperimentalContracts
private fun IrStatementContainer.rewriteBindAsFlatMap(): Unit {
  val newTree = flatBindRewrite()
  statements.clear()
  newTree.forEach {
    if (it is IrStatementContainer) {
      it.rewriteBindAsFlatMap()
    }
  }
  statements.addAll(newTree)
}

@ExperimentalContracts
private fun IrStatement.isBinding(): Boolean {
  contract {
    returns(true) implies (this@isBinding is IrCall)
  }
  return this is IrCall && this.descriptor.name.asString() == "bind"
}

@ExperimentalContracts
private fun IrBody.hasBindings(): Boolean {
  contract {
    returns(true) implies (this@hasBindings is IrStatementContainer)
  }
  return this is IrStatementContainer && statements.any { it.isBinding() }
}

