package arrow.meta.dsl.ide.editor.inspection

import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugin.idea.IdeMetaPlugin
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.openapi.extensions.ExtensionPointName
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.idea.util.actualsForExpected
import org.jetbrains.kotlin.idea.util.liftToExpected
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.util.Check

interface InspectionUtilitySyntax {
  fun KtDeclaration.withExpectedActuals(): List<KtDeclaration> {
    val expect = liftToExpected() ?: return listOf(this)
    val actuals = expect.actualsForExpected()
    return listOf(expect) + actuals
  }

  fun IdeMetaPlugin.addLocalInspectionToolToIdeRegistry(): ExtensionPhase =
    registerExtensionPoint(
      EP_NAME,
      LocalInspectionTool::class.java
    )
}

sealed class ExtendedReturnsCheck(val name: String, val type: KotlinBuiltIns.() -> KotlinType) : Check {
  override val description = "must return $name"
  override fun check(functionDescriptor: FunctionDescriptor) =
    functionDescriptor.returnType == functionDescriptor.builtIns.type()

  object ReturnsNothing : ExtendedReturnsCheck("Nothing", { nothingType })
  object ReturnsNullableNothing : ExtendedReturnsCheck("NullableNothing", { nullableNothingType })
}

val EP_NAME: ExtensionPointName<LocalInspectionTool>
  get() = ExtensionPointName.create<LocalInspectionTool>("com.intellij.codeInspection.LocalInspectionTool")
