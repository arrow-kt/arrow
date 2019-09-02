package arrow.meta.qq

import arrow.meta.extensions.CompilerContext
import org.jetbrains.kotlin.container.get
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.lazy.LazyDeclarationResolver

interface QuasiQuoteContext {
  val compilerContext: CompilerContext
  val bindingContext: BindingContext
  val bindingTrace: BindingTrace

  companion object {
    operator fun invoke(compilerContext: CompilerContext) =
      object : QuasiQuoteContext {
        override val compilerContext: CompilerContext = compilerContext
        override val bindingContext: BindingContext = compilerContext.bindingTrace.bindingContext
        override val bindingTrace: BindingTrace = compilerContext.bindingTrace
      }
  }
}