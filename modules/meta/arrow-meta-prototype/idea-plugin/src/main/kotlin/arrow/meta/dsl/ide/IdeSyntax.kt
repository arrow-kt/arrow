package arrow.meta.dsl.ide

import arrow.meta.dsl.ide.editor.action.AnActionSyntax
import arrow.meta.dsl.ide.editor.color.ColorSyntax
import arrow.meta.dsl.ide.editor.icon.IconProviderSyntax
import arrow.meta.dsl.ide.editor.inspection.InspectionSyntax
import arrow.meta.dsl.ide.editor.intention.IntentionExtensionProviderSyntax
import arrow.meta.dsl.ide.editor.syntaxHighlighter.SyntaxHighlighterExtensionProviderSyntax


interface IdeSyntax : IntentionExtensionProviderSyntax, IconProviderSyntax,
  SyntaxHighlighterExtensionProviderSyntax, InspectionSyntax, AnActionSyntax, ColorSyntax
