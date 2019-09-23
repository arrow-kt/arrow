package arrow.meta.dsl.ide.editor.syntaxHighlighter

import arrow.meta.dsl.platform.ide
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugin.idea.IdeMetaPlugin
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SingleLazyInstanceSyntaxHighlighterFactory
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.lexer.KotlinLexer

interface SyntaxHighlighterExtensionProviderSyntax {
  // TODO: Test impl
  fun IdeMetaPlugin.addSyntaxHighlighter(
    highlightingLexer: Lexer = KotlinLexer(),
    tokenHighlights: (tokenType: IElementType?) -> Array<TextAttributesKey>
  ): ExtensionPhase =
    ide {
      SyntaxHighlighterFactory.LANGUAGE_FACTORY
        .addExplicitExtension(KotlinLanguage.INSTANCE, addSyntaxHighlighterFactory(
          this@SyntaxHighlighterExtensionProviderSyntax.addSyntaxHighlighter(highlightingLexer, tokenHighlights)
        ))
      ExtensionPhase.Empty
    } ?: ExtensionPhase.Empty

  fun SyntaxHighlighterExtensionProviderSyntax.addSyntaxHighlighter(
    highlightingLexer: Lexer = KotlinLexer(),
    tokenHighlights: (tokenType: IElementType?) -> Array<TextAttributesKey>
  ): SyntaxHighlighter =
    object : SyntaxHighlighterBase() {
      override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> =
        tokenHighlights(tokenType)

      override fun getHighlightingLexer(): Lexer =
        highlightingLexer
    }

  fun SyntaxHighlighterExtensionProviderSyntax.addSyntaxHighlighterFactory(
    syntaxHighlighter: SyntaxHighlighter
  ): SyntaxHighlighterFactory =
    object : SingleLazyInstanceSyntaxHighlighterFactory() {
      override fun createHighlighter(): SyntaxHighlighter =
        syntaxHighlighter
    }
}