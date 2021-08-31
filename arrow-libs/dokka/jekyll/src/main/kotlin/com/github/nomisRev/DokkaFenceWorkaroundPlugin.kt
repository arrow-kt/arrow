package com.github.nomisRev

import org.intellij.markdown.MarkdownElementTypes
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
import org.jetbrains.dokka.model.doc.A
import org.jetbrains.dokka.model.doc.B
import org.jetbrains.dokka.model.doc.BlockQuote
import org.jetbrains.dokka.model.doc.Br
import org.jetbrains.dokka.model.doc.Caption
import org.jetbrains.dokka.model.doc.CodeBlock
import org.jetbrains.dokka.model.doc.CodeInline
import org.jetbrains.dokka.model.doc.CustomDocTag
import org.jetbrains.dokka.model.doc.DocTag
import org.jetbrains.dokka.model.doc.DocumentationLink
import org.jetbrains.dokka.model.doc.H1
import org.jetbrains.dokka.model.doc.H2
import org.jetbrains.dokka.model.doc.H3
import org.jetbrains.dokka.model.doc.H4
import org.jetbrains.dokka.model.doc.H5
import org.jetbrains.dokka.model.doc.H6
import org.jetbrains.dokka.model.doc.HorizontalRule
import org.jetbrains.dokka.model.doc.I
import org.jetbrains.dokka.model.doc.Img
import org.jetbrains.dokka.model.doc.Index
import org.jetbrains.dokka.model.doc.Li
import org.jetbrains.dokka.model.doc.Ol
import org.jetbrains.dokka.model.doc.P
import org.jetbrains.dokka.model.doc.Pre
import org.jetbrains.dokka.model.doc.Strikethrough
import org.jetbrains.dokka.model.doc.TBody
import org.jetbrains.dokka.model.doc.THead
import org.jetbrains.dokka.model.doc.Table
import org.jetbrains.dokka.model.doc.Text
import org.jetbrains.dokka.model.doc.Th
import org.jetbrains.dokka.model.doc.Tr
import org.jetbrains.dokka.model.doc.Ul
import org.jetbrains.dokka.model.properties.PropertyContainer
import org.jetbrains.dokka.model.properties.plus
import org.jetbrains.dokka.model.toDisplaySourceSets
import org.jetbrains.dokka.pages.CommentTable
import org.jetbrains.dokka.pages.ContentBreakLine
import org.jetbrains.dokka.pages.ContentCodeBlock
import org.jetbrains.dokka.pages.ContentCodeInline
import org.jetbrains.dokka.pages.ContentDRILink
import org.jetbrains.dokka.pages.ContentEmbeddedResource
import org.jetbrains.dokka.pages.ContentGroup
import org.jetbrains.dokka.pages.ContentHeader
import org.jetbrains.dokka.pages.ContentKind
import org.jetbrains.dokka.pages.ContentList
import org.jetbrains.dokka.pages.ContentNode
import org.jetbrains.dokka.pages.ContentPage
import org.jetbrains.dokka.pages.ContentResolvedLink
import org.jetbrains.dokka.pages.ContentStyle
import org.jetbrains.dokka.pages.ContentTable
import org.jetbrains.dokka.pages.ContentText
import org.jetbrains.dokka.pages.DCI
import org.jetbrains.dokka.pages.HtmlContent
import org.jetbrains.dokka.pages.SimpleAttr
import org.jetbrains.dokka.pages.Style
import org.jetbrains.dokka.pages.TextStyle
import org.jetbrains.dokka.plugability.DokkaContext
import org.jetbrains.dokka.plugability.DokkaPlugin
import org.jetbrains.dokka.plugability.Extension
import org.jetbrains.dokka.plugability.ExtensionPoint
import org.jetbrains.dokka.plugability.plugin
import org.jetbrains.dokka.plugability.query
import org.jetbrains.dokka.renderers.Renderer
import org.jetbrains.dokka.transformers.pages.PageTransformer
import org.jetbrains.dokka.utilities.htmlEscape
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstanceOrNull

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

  override fun buildPage(page: ContentPage, content: (StringBuilder, ContentPage) -> Unit): String {
    val builder = StringBuilder()
    builder.append("---\n")
    builder.append("title: ${page.name}\n")
    builder.append("---\n")
    content(builder, page)
    return builder.toString()
  }

  override fun StringBuilder.buildCodeBlock(code: ContentCodeBlock, pageContext: ContentPage) {
    append("```${code.language}\n")
    code.children.forEach { buildContentNode(it, pageContext) }
    append("\n```")
  }

  override fun StringBuilder.buildCodeInline(code: ContentCodeInline, pageContext: ContentPage) {
    append('`')
    code.children.forEach { buildContentNode(it, pageContext) }
    append('`')
  }

  // Fix merged in Dokka https://github.com/Kotlin/dokka/pull/2102
  private fun decorators(styles: Set<Style>) = buildString {
    styles.forEach {
      when (it) {
        TextStyle.Bold -> append("**")
        TextStyle.Italic -> append("*")
        TextStyle.Strong -> append("**")
        TextStyle.Strikethrough -> append("~~")
        else -> Unit
      }
    }
  }

  override fun StringBuilder.buildText(textNode: ContentText) {
    if (textNode.extra[HtmlContent] != null) {
      append(textNode.text)
    } else if (textNode.text.isNotBlank()) {
      val decorators = decorators(textNode.style)
      append(textNode.text.takeWhile { it == ' ' })
      append(decorators)
      append(textNode.text.trim())
      append(textNode.text.trim().htmlEscape())
      append(decorators.reversed())
      append(textNode.text.takeLastWhile { it == ' ' })
    }
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

    fun buildTableRows(rows: List<DocTag>, newStyle: Style): List<ContentGroup> =
      rows.flatMap {
        buildContent(it, dci, sourceSets, styles + newStyle, extra) as List<ContentGroup>
      }

    fun buildHeader(level: Int) =
      listOf(
        ContentHeader(
          buildChildren(docTag),
          level,
          dci,
          sourceSets.toDisplaySourceSets(),
          styles
        )
      )

    fun buildList(ordered: Boolean, start: Int = 1) =
      listOf(
        ContentList(
          buildChildren(docTag),
          ordered,
          dci,
          sourceSets.toDisplaySourceSets(),
          styles,
          ((PropertyContainer.empty<ContentNode>()) + SimpleAttr("start", start.toString()))
        )
      )

    fun buildNewLine() = listOf(
      ContentBreakLine(
        sourceSets.toDisplaySourceSets()
      )
    )

    fun P.collapseParagraphs(): P =
      if (children.size == 1 && children.first() is P) (children.first() as P).collapseParagraphs() else this

    return when (docTag) {
      is H1 -> buildHeader(1)
      is H2 -> buildHeader(2)
      is H3 -> buildHeader(3)
      is H4 -> buildHeader(4)
      is H5 -> buildHeader(5)
      is H6 -> buildHeader(6)
      is Ul -> buildList(false)
      is Ol -> buildList(true, docTag.params["start"]?.toInt() ?: 1)
      is Li -> listOf(
        ContentGroup(buildChildren(docTag), dci, sourceSets.toDisplaySourceSets(), styles, extra)
      )
      is Br -> buildNewLine()
      is B -> buildChildren(docTag, setOf(TextStyle.Strong))
      is I -> buildChildren(docTag, setOf(TextStyle.Italic))
      is P -> listOf(
        ContentGroup(
          buildChildren(docTag.collapseParagraphs()),
          dci,
          sourceSets.toDisplaySourceSets(),
          styles + setOf(TextStyle.Paragraph),
          extra
        )
      )
      is A -> listOf(
        ContentResolvedLink(
          buildChildren(docTag),
          docTag.params.getValue("href"),
          dci,
          sourceSets.toDisplaySourceSets(),
          styles
        )
      )
      is DocumentationLink -> listOf(
        ContentDRILink(
          buildChildren(docTag),
          docTag.dri,
          DCI(
            setOf(docTag.dri),
            ContentKind.Main
          ),
          sourceSets.toDisplaySourceSets(),
          styles
        )
      )
      is BlockQuote, is Pre, is CodeBlock -> listOf(
        ContentCodeBlock(
          buildChildren(docTag),
          docTag.params.getOrDefault("lang", ""),
          dci,
          sourceSets.toDisplaySourceSets(),
          styles
        )
      )
      is CodeInline -> listOf(
        ContentCodeInline(
          buildChildren(docTag),
          "",
          dci,
          sourceSets.toDisplaySourceSets(),
          styles
        )
      )
      is Img -> listOf(
        ContentEmbeddedResource(
          address = docTag.params["href"]!!,
          altText = docTag.params["alt"],
          dci = dci,
          sourceSets = sourceSets.toDisplaySourceSets(),
          style = styles,
          extra = extra
        )
      )
      is HorizontalRule -> listOf(
        ContentText(
          "",
          dci,
          sourceSets.toDisplaySourceSets(),
          setOf()
        )
      )
      is Text -> listOf(
        ContentText(
          docTag.body,
          dci,
          sourceSets.toDisplaySourceSets(),
          styles,
          extra + HtmlContent.takeIf { docTag.params["content-type"] == "html" }
        )
      )
      is Strikethrough -> buildChildren(docTag, setOf(TextStyle.Strikethrough))
      is Table -> {
        //https://html.spec.whatwg.org/multipage/tables.html#the-caption-element
        if (docTag.children.any { it is TBody }) {
          val head = docTag.children.filterIsInstance<THead>().flatMap { it.children }
          val body = docTag.children.filterIsInstance<TBody>().flatMap { it.children }
          listOf(
            ContentTable(
              header = buildTableRows(head.filterIsInstance<Th>(), CommentTable),
              caption = docTag.children.firstIsInstanceOrNull<Caption>()?.let {
                ContentGroup(
                  buildContent(it, dci, sourceSets),
                  dci,
                  sourceSets.toDisplaySourceSets(),
                  styles,
                  extra
                )
              },
              buildTableRows(body.filterIsInstance<Tr>(), CommentTable),
              dci,
              sourceSets.toDisplaySourceSets(),
              styles + CommentTable
            )
          )
        } else {
          listOf(
            ContentTable(
              header = buildTableRows(docTag.children.filterIsInstance<Th>(), CommentTable),
              caption = null,
              buildTableRows(docTag.children.filterIsInstance<Tr>(), CommentTable),
              dci,
              sourceSets.toDisplaySourceSets(),
              styles + CommentTable
            )
          )
        }
      }
      is Th,
      is Tr -> listOf(
        ContentGroup(
          docTag.children.map {
            ContentGroup(buildChildren(it), dci, sourceSets.toDisplaySourceSets(), styles, extra)
          },
          dci,
          sourceSets.toDisplaySourceSets(),
          styles
        )
      )
      is Index -> listOf(
        ContentGroup(
          buildChildren(docTag, newStyles = styles + ContentStyle.InDocumentationAnchor),
          dci,
          sourceSets.toDisplaySourceSets(),
          styles
        )
      )
      is CustomDocTag -> if (docTag.isNonemptyFile()) {
        listOf(
          ContentGroup(
            buildChildren(docTag),
            dci,
            sourceSets.toDisplaySourceSets(),
            styles,
            extra = extra
          )
        )
      } else {
        buildChildren(docTag)
      }
      is Caption -> listOf(
        ContentGroup(
          buildChildren(docTag),
          dci,
          sourceSets.toDisplaySourceSets(),
          styles + ContentStyle.Caption,
          extra = extra
        )
      )

      else -> buildChildren(docTag)
    }
  }

  private fun CustomDocTag.isNonemptyFile() = name == MarkdownElementTypes.MARKDOWN_FILE.name && children.size > 1
}
