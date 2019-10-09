package arrow.meta.dsl.ide.editor.language

import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugin.idea.IdeMetaPlugin
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.lang.jvm.JvmClass
import com.intellij.lang.jvm.JvmMethod
import com.intellij.lang.jvm.JvmModifiersOwner
import com.intellij.lang.jvm.actions.AnnotationRequest
import com.intellij.lang.jvm.actions.ChangeModifierRequest
import com.intellij.lang.jvm.actions.ChangeParametersRequest
import com.intellij.lang.jvm.actions.CreateConstructorRequest
import com.intellij.lang.jvm.actions.CreateFieldRequest
import com.intellij.lang.jvm.actions.CreateMethodRequest
import com.intellij.lang.jvm.actions.EP_NAME
import com.intellij.lang.jvm.actions.JvmElementActionsFactory

interface LanguageSyntax {
  fun IdeMetaPlugin.addJvmElementActionsFactory(
    annotationActions: (target: JvmModifiersOwner, request: AnnotationRequest) -> List<IntentionAction> = Noop.emptyList2(),
    constructorActions: (targetClass: JvmClass, request: CreateConstructorRequest) -> List<IntentionAction> = Noop.emptyList2(),
    fieldActions: (targetClass: JvmClass, request: CreateFieldRequest) -> List<IntentionAction> = Noop.emptyList2(),
    methodActions: (targetClass: JvmClass, request: CreateMethodRequest) -> List<IntentionAction> = Noop.emptyList2(),
    changeModifierActions: (target: JvmModifiersOwner, request: ChangeModifierRequest) -> List<IntentionAction> = Noop.emptyList2(),
    changeParameterActions: (target: JvmMethod, request: ChangeParametersRequest) -> List<IntentionAction> = Noop.emptyList2()
  ): ExtensionPhase =
    extensionProvider(
      EP_NAME,
      object : JvmElementActionsFactory() {
        override fun createAddAnnotationActions(target: JvmModifiersOwner, request: AnnotationRequest): List<IntentionAction> =
          annotationActions(target, request)

        override fun createAddConstructorActions(targetClass: JvmClass, request: CreateConstructorRequest): List<IntentionAction> =
          constructorActions(targetClass, request)

        override fun createAddFieldActions(targetClass: JvmClass, request: CreateFieldRequest): List<IntentionAction> =
          fieldActions(targetClass, request)

        override fun createAddMethodActions(targetClass: JvmClass, request: CreateMethodRequest): List<IntentionAction> =
          methodActions(targetClass, request)

        override fun createChangeModifierActions(target: JvmModifiersOwner, request: ChangeModifierRequest): List<IntentionAction> =
          changeModifierActions(target, request)

        override fun createChangeParametersActions(target: JvmMethod, request: ChangeParametersRequest): List<IntentionAction> =
          changeParameterActions(target, request)
      }
    )
}
