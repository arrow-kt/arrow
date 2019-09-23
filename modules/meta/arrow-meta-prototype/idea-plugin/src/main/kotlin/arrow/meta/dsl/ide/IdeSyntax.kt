package arrow.meta.dsl.ide

import arrow.meta.dsl.ide.editor.action.AnActionSyntax
import arrow.meta.dsl.ide.editor.color.ColorSyntax
import arrow.meta.dsl.ide.editor.hints.HintsSyntax
import arrow.meta.dsl.ide.editor.icon.IconProviderSyntax
import arrow.meta.dsl.ide.editor.inspection.InspectionSyntax
import arrow.meta.dsl.ide.editor.intention.IntentionExtensionProviderSyntax
import arrow.meta.dsl.ide.editor.language.LanguageSyntax
import arrow.meta.dsl.ide.editor.lineMarker.LineMarkerSyntax
import arrow.meta.dsl.ide.editor.liveTemplate.LiveTemplateSyntax
import arrow.meta.dsl.ide.editor.navigation.NavigationSyntax
import arrow.meta.dsl.ide.editor.search.SearchSyntax
import arrow.meta.dsl.ide.editor.structureView.StructureViewSyntax
import arrow.meta.dsl.ide.editor.syntaxHighlighter.SyntaxHighlighterExtensionProviderSyntax


interface IdeSyntax : IntentionExtensionProviderSyntax, IconProviderSyntax,
  SyntaxHighlighterExtensionProviderSyntax, InspectionSyntax, AnActionSyntax, ColorSyntax, HintsSyntax,
  LanguageSyntax, LineMarkerSyntax, LiveTemplateSyntax, NavigationSyntax, SearchSyntax, StructureViewSyntax
