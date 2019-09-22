package arrow.meta.dsl

import arrow.meta.dsl.analysis.AnalysisSyntax
import arrow.meta.dsl.codegen.CodegenSyntax
import arrow.meta.dsl.config.ConfigSyntax
import arrow.meta.dsl.resolve.ResolveSyntax
import arrow.meta.phases.ExtensionPhase

interface MetaPluginSyntax : ConfigSyntax, AnalysisSyntax, ResolveSyntax, CodegenSyntax {

  fun meta(vararg phases: ExtensionPhase): List<ExtensionPhase> =
    phases.toList()

}