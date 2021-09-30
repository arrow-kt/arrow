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
import org.jetbrains.dokka.model.DisplaySourceSet
import org.jetbrains.dokka.model.doc.CodeBlock
import org.jetbrains.dokka.model.doc.DocTag
import org.jetbrains.dokka.model.properties.PropertyContainer
import org.jetbrains.dokka.model.toDisplaySourceSets
import org.jetbrains.dokka.pages.ContentCodeBlock
import org.jetbrains.dokka.pages.ContentCodeInline
import org.jetbrains.dokka.pages.ContentDivergentGroup
import org.jetbrains.dokka.pages.ContentDivergentInstance
import org.jetbrains.dokka.pages.ContentGroup
import org.jetbrains.dokka.pages.ContentNode
import org.jetbrains.dokka.pages.ContentPage
import org.jetbrains.dokka.pages.ContentTable
import org.jetbrains.dokka.pages.DCI
import org.jetbrains.dokka.pages.PlatformHintedContent
import org.jetbrains.dokka.pages.SimpleAttr
import org.jetbrains.dokka.pages.Style
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

  public override fun StringBuilder.buildNewLine() {
    append("\n")
  }

  public fun StringBuilder.buildParagraph() {
    buildNewLine()
    buildNewLine()
  }

  override fun buildPage(page: ContentPage, content: (StringBuilder, ContentPage) -> Unit): String {
    val builder = StringBuilder()
    builder.append("---\n")
    builder.append("title: ${page.name}\n")
    builder.append("---\n")
    content(builder, page)
    return builder.toString()
  }

  override fun StringBuilder.buildDivergent(node: ContentDivergentGroup, pageContext: ContentPage) {

    val distinct =
      node.groupDivergentInstances(pageContext, { instance, contentPage, sourceSet ->
        instance.before?.let { before ->
          buildString { buildContentNode(before, pageContext, sourceSet) }
        } ?: ""
      }, { instance, contentPage, sourceSet ->
        instance.after?.let { after ->
          buildString { buildContentNode(after, pageContext, sourceSet) }
        } ?: ""
      })

    distinct.values.forEach { entry ->
      val (instance, sourceSets) = entry.getInstanceAndSourceSets()

      buildParagraph()
      buildSourceSetTags(sourceSets)
      buildNewLine()

      instance.before?.let {
        buildContentNode(
          it,
          pageContext,
          sourceSets.first()
        ) // It's workaround to render content only once
        buildParagraph()
      }

      entry.groupBy { buildString { buildContentNode(it.first.divergent, pageContext, setOf(it.second)) } }
        .values.forEach { innerEntry ->
          val (innerInstance, innerSourceSets) = innerEntry.getInstanceAndSourceSets()
          if (sourceSets.size > 1) {
            buildSourceSetTags(innerSourceSets)
            buildNewLine()
          }
          innerInstance.divergent.build(
            this@buildDivergent,
            pageContext,
            setOf(innerSourceSets.first())
          ) // It's workaround to render content only once
          buildParagraph()
        }

      instance.after?.let {
        buildContentNode(
          it,
          pageContext,
          sourceSets.first()
        ) // It's workaround to render content only once
      }

      buildParagraph()
    }
  }

  private fun List<Pair<ContentDivergentInstance, DisplaySourceSet>>.getInstanceAndSourceSets() =
    this.let { Pair(it.first().first, it.map { it.second }.toSet()) }

  override fun StringBuilder.buildPlatformDependent(
    content: PlatformHintedContent,
    pageContext: ContentPage,
    sourceSetRestriction: Set<DisplaySourceSet>?
  ) {
    buildPlatformDependentItem(content.inner, content.sourceSets, pageContext)
  }

  private fun StringBuilder.buildPlatformDependentItem(
    content: ContentNode,
    sourceSets: Set<DisplaySourceSet>,
    pageContext: ContentPage,
  ) {
    if (content is ContentGroup && content.children.firstOrNull { it is ContentTable } != null) {
      buildContentNode(content, pageContext, sourceSets)
    } else {
      val distinct = sourceSets.map {
        it to buildString { buildContentNode(content, pageContext, setOf(it)) }
      }.groupBy(Pair<DisplaySourceSet, String>::second, Pair<DisplaySourceSet, String>::first)

      distinct.filter { it.key.isNotBlank() }.forEach { (text, platforms) ->
        buildParagraph()
        buildSourceSetTags(platforms.toSet())
        buildNewLine()
        append(text.trim())
        buildParagraph()
      }
    }
  }

  private fun StringBuilder.buildSourceSetTags(sourceSets: Set<DisplaySourceSet>) =
    sourceSets.forEach { append("""<span class="platform-${it.name}">${it.name}</span>""") }

  override fun StringBuilder.buildCodeBlock(code: ContentCodeBlock, pageContext: ContentPage) {
    append("```${code.language}\n")
    code.children.forEach { it.build(this, pageContext) }
    append("\n```")
  }

  override fun StringBuilder.buildCodeInline(code: ContentCodeInline, pageContext: ContentPage) {
    append('`')
    code.children.forEach { it.build(this, pageContext) }
    append('`')
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
