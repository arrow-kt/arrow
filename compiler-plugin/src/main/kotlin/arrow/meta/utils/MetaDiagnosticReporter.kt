package arrow.meta.utils

import org.jetbrains.kotlin.resolve.calls.model.DiagnosticReporter
import org.jetbrains.kotlin.resolve.calls.model.KotlinCallArgument
import org.jetbrains.kotlin.resolve.calls.model.KotlinCallDiagnostic
import org.jetbrains.kotlin.resolve.calls.model.SimpleKotlinCallArgument
import org.jetbrains.kotlin.resolve.calls.model.TypeArgument

class MetaDiagnosticReporter : DiagnosticReporter {
  override fun constraintError(diagnostic: KotlinCallDiagnostic) {
    println("constraintError"); TODO()
  }

  override fun onCall(diagnostic: KotlinCallDiagnostic) {
    println("onCall"); TODO()
  }

  override fun onCallArgument(callArgument: KotlinCallArgument, diagnostic: KotlinCallDiagnostic) {
    println("onCallArgument"); TODO()
  }

  override fun onCallArgumentName(callArgument: KotlinCallArgument, diagnostic: KotlinCallDiagnostic) {
    println("onCallArgumentName"); TODO()
  }

  override fun onCallArgumentSpread(callArgument: KotlinCallArgument, diagnostic: KotlinCallDiagnostic) {
    println("onCallArgumentSpread"); TODO()
  }

  override fun onCallName(diagnostic: KotlinCallDiagnostic) {
    println("onCallName"); TODO()
  }

  override fun onCallReceiver(callReceiver: SimpleKotlinCallArgument, diagnostic: KotlinCallDiagnostic) {
    println("onCallReceiver"); TODO()
  }

  override fun onExplicitReceiver(diagnostic: KotlinCallDiagnostic) {
    println("onExplicitReceiver"); TODO()
  }

  override fun onTypeArgument(typeArgument: TypeArgument, diagnostic: KotlinCallDiagnostic) {
    println("onTypeArgument"); TODO()
  }

  override fun onTypeArguments(diagnostic: KotlinCallDiagnostic) {
    println("onTypeArguments"); TODO()
  }
}