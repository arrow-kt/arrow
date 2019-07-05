package arrow.meta.qq

import arrow.meta.extensions.CompilerContext
import org.jetbrains.kotlin.resolve.BindingTrace

interface QuasiQuoteContext {
  val compilerContext: CompilerContext
  val bindingTrace: BindingTrace

  companion object {
    operator fun invoke(compilerContext: CompilerContext, bindingTrace: BindingTrace) =
      object : QuasiQuoteContext {
        override val compilerContext: CompilerContext = compilerContext
        override val bindingTrace: BindingTrace = bindingTrace
      }
  }
}