package arrow.meta.dsl.ide.editor.navigation

import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugin.idea.IdeMetaPlugin
import com.intellij.navigation.ChooseByNameContributor
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.project.Project

interface NavigationSyntax {
  fun IdeMetaPlugin.addChooseByNameContributorForFile(
    itemsByName: (name: String?, pattern: String?, project: Project?, includeNonProjectItems: Boolean) -> Array<NavigationItem>,
    names: (project: Project?, includeNonProjectItems: Boolean) -> Array<String>
  ): ExtensionPhase =
    extensionProvider(
      ChooseByNameContributor.FILE_EP_NAME,
      chooseByNameContributor(itemsByName, names)
    )

  fun IdeMetaPlugin.addChooseByNameContributorForClass(
    itemsByName: (name: String?, pattern: String?, project: Project?, includeNonProjectItems: Boolean) -> Array<NavigationItem>,
    names: (project: Project?, includeNonProjectItems: Boolean) -> Array<String>
  ): ExtensionPhase =
    extensionProvider(
      ChooseByNameContributor.CLASS_EP_NAME,
      chooseByNameContributor(itemsByName, names)
    )

  fun IdeMetaPlugin.addChooseByNameContributorForSymbol(
    itemsByName: (name: String?, pattern: String?, project: Project?, includeNonProjectItems: Boolean) -> Array<NavigationItem>,
    names: (project: Project?, includeNonProjectItems: Boolean) -> Array<String>
  ): ExtensionPhase =
    extensionProvider(
      ChooseByNameContributor.SYMBOL_EP_NAME,
      chooseByNameContributor(itemsByName, names)
    )

  fun NavigationSyntax.chooseByNameContributor(
    itemsByName: (name: String?, pattern: String?, project: Project?, includeNonProjectItems: Boolean) -> Array<NavigationItem>,
    names: (project: Project?, includeNonProjectItems: Boolean) -> Array<String>
  ): ChooseByNameContributor =
    object : ChooseByNameContributor {
      override fun getItemsByName(name: String?, pattern: String?, project: Project?, includeNonProjectItems: Boolean): Array<NavigationItem> =
        itemsByName(name, pattern, project, includeNonProjectItems)

      override fun getNames(project: Project?, includeNonProjectItems: Boolean): Array<String> =
        names(project, includeNonProjectItems)
    }
}
