package com.github.nomisRev

import org.jetbrains.dokka.CoreExtensions
import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.renderers.PackageListCreator
import org.jetbrains.dokka.base.renderers.RootCreator
import org.jetbrains.dokka.base.resolvers.local.DokkaLocationProviderFactory
import org.jetbrains.dokka.base.resolvers.local.LocationProviderFactory
import org.jetbrains.dokka.base.resolvers.shared.RecognizedLinkFormat
import org.jetbrains.dokka.base.transformers.pages.comments.CommentsToContentConverter
import org.jetbrains.dokka.base.transformers.pages.comments.DocTagToContentConverter
import org.jetbrains.dokka.gfm.GfmPlugin
import org.jetbrains.dokka.gfm.renderer.BriefCommentPreprocessor
import org.jetbrains.dokka.gfm.renderer.CommonmarkRenderer
import org.jetbrains.dokka.model.doc.CodeBlock
import org.jetbrains.dokka.model.doc.DocTag
import org.jetbrains.dokka.model.properties.PropertyContainer
import org.jetbrains.dokka.model.toDisplaySourceSets
import org.jetbrains.dokka.pages.ContentBreakLine
import org.jetbrains.dokka.pages.ContentCodeBlock
import org.jetbrains.dokka.pages.ContentCodeInline
import org.jetbrains.dokka.pages.ContentDRILink
import org.jetbrains.dokka.pages.ContentGroup
import org.jetbrains.dokka.pages.ContentNode
import org.jetbrains.dokka.pages.ContentPage
import org.jetbrains.dokka.pages.ContentText
import org.jetbrains.dokka.pages.DCI
import org.jetbrains.dokka.pages.SimpleAttr
import org.jetbrains.dokka.pages.Style
import org.jetbrains.dokka.pages.TextStyle
import org.jetbrains.dokka.pages.hasStyle
import org.jetbrains.dokka.plugability.DokkaContext
import org.jetbrains.dokka.plugability.DokkaPlugin
import org.jetbrains.dokka.plugability.Extension
import org.jetbrains.dokka.plugability.ExtensionPoint
import org.jetbrains.dokka.plugability.plugin
import org.jetbrains.dokka.plugability.query
import org.jetbrains.dokka.renderers.Renderer
import org.jetbrains.dokka.transformers.pages.PageTransformer

public class DokkaFenceWorkaround : DokkaPlugin() {
  public val jekyllPreprocessors: ExtensionPoint<PageTransformer> by extensionPoint()

  private val dokkaBase by lazy { plugin<DokkaBase>() }
  private val gfmPlugin by lazy { plugin<GfmPlugin>() }

  public val renderer: Extension<Renderer, *, *> by extending {
    (CoreExtensions.renderer
      providing { JekyllRenderer(it) }
      override plugin<GfmPlugin>().renderer)
  }

  public val comments: Extension<CommentsToContentConverter, *, *> by extending {
    dokkaBase.commentsToContentConverter with PatchedDocTagToContentConverter() override dokkaBase.docTagToContentConverter
  }

  public val rootCreator: Extension<PageTransformer, *, *> by extending {
    jekyllPreprocessors with RootCreator
  }

  public val briefCommentPreprocessor: Extension<PageTransformer, *, *> by extending {
    jekyllPreprocessors with BriefCommentPreprocessor()
  }

  public val packageListCreator: Extension<PageTransformer, *, *> by extending {
    jekyllPreprocessors providing {
      PackageListCreator(it, RecognizedLinkFormat.DokkaJekyll)
    } order { after(rootCreator) }
  }

  public val locationProvider: Extension<LocationProviderFactory, *, *> by extending {
    dokkaBase.locationProviderFactory providing ::DokkaLocationProviderFactory override listOf(gfmPlugin.locationProvider)
  }
}

public class JekyllRenderer(context: DokkaContext) : CommonmarkRenderer(context) {

  override val preprocessors: List<PageTransformer> =
    context.plugin<DokkaFenceWorkaround>().query { jekyllPreprocessors }

  override fun StringBuilder.buildNewLine() {
    append("\n")
  }

  public fun StringBuilder.buildParagraph() {
    buildNewLine()
    buildNewLine()
  }

  override fun StringBuilder.wrapGroup(
    node: ContentGroup,
    pageContext: ContentPage,
    childrenCallback: StringBuilder.() -> Unit
  ) {
    return when {
      node.hasStyle(TextStyle.Monospace) -> {
        buildParagraph()
        inlineCodeBlock(node, pageContext)
        buildParagraph()
      }
      node.hasStyle(TextStyle.Block) || node.hasStyle(TextStyle.Paragraph) -> {
        buildParagraph()
        childrenCallback()
        buildParagraph()
      }
      else -> childrenCallback()
    }
  }

  override fun buildPage(page: ContentPage, content: (StringBuilder, ContentPage) -> Unit): String {
    val builder = StringBuilder()
    builder.append("---\n")
    builder.append("title: ${page.name}\n")
    builder.append("---\n")
    content(builder, page)
    return builder.toString()
  }

  override fun StringBuilder.buildCodeBlock(
    code: ContentCodeBlock,
    pageContext: ContentPage
  ) {
    fun buildGroup(group: ContentNode, pageContext: ContentPage) {
      group.children.forEach { node ->
        when (node) {
          is ContentText -> append(node.text)
          is ContentBreakLine -> buildNewLine()
          else -> buildGroup(node, pageContext)
        }
      }
    }
    append("```${code.language}")
    buildNewLine()
    buildGroup(code, pageContext)
    buildNewLine()
    append("```")
  }

  override fun StringBuilder.buildCodeInline(code: ContentCodeInline, pageContext: ContentPage) {
    inlineCodeBlock(code, pageContext)
  }

  private fun StringBuilder.inlineCodeBlock(
    node: ContentNode,
    pageContext: ContentPage
  ) {
    val code = StringBuilder()

    fun buildCode() {
      if (code.isNotEmpty()) {
        val decorators = "`".repeat(code.count { it == '`' } + 1) +
          if (code.first() == '`' || code.last() == '`' ||
            (code.first() == ' ' && code.last() == ' ')
          ) " " else ""
        append(decorators)
        append(code)
        append(decorators.reversed())
        code.clear()
      }
    }

    fun buildGroup(
      group: ContentNode,
      pageContext: ContentPage
    ): Unit = group.children.forEach { node ->
      when (node) {
        is ContentText -> if (!node.text.contains('\n')) {
          code.append(node.text)
        } else {
          val chunks = node.text.split('\n')
          chunks.dropLast(1).forEach {
            code.append(it)
            buildCode()
            buildNewLine()
          }
          chunks.last().apply {
            code.append(node)
          }
        }
        is ContentBreakLine -> {
          buildCode()
          buildNewLine()
        }
        is ContentDRILink -> {
          val link = locationProvider.resolve(node.address, node.sourceSets, pageContext)
          if (link.isNullOrEmpty()) {
            buildGroup(node, pageContext)
          } else {
            buildCode()
            append("[")
            buildGroup(node, pageContext)
            buildCode()
            append("]($link)")
          }
        }
        else -> buildGroup(node, pageContext)
      }
    }
    buildGroup(node, pageContext)
    buildCode()
  }
}

public class PatchedDocTagToContentConverter : DocTagToContentConverter() {
  override fun buildContent(
    docTag: DocTag,
    dci: DCI,
    sourceSets: Set<DokkaConfiguration.DokkaSourceSet>,
    styles: Set<Style>,
    extra: PropertyContainer<ContentNode>
  ): List<ContentNode> {
    fun buildChildren(docTag: DocTag, newStyles: Set<Style> = emptySet(), newExtras: SimpleAttr? = null) =
      docTag.children.flatMap {
        buildContent(it, dci, sourceSets, styles + newStyles, newExtras?.let { extra + it } ?: extra)
      }

    return if (docTag is CodeBlock) listOf(
      ContentCodeBlock(
        buildChildren(docTag),
        docTag.params.getOrDefault("lang", ""),
        dci,
        sourceSets.toDisplaySourceSets(),
        styles
      )
    ) else super.buildContent(docTag, dci, sourceSets, styles, extra)
  }
}
