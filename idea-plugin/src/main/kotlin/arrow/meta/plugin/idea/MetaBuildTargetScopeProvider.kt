package arrow.meta.plugin.idea

import com.intellij.compiler.impl.BuildTargetScopeProvider
import com.intellij.openapi.compiler.CompileScope
import com.intellij.openapi.project.Project
import org.jetbrains.jps.api.CmdlineRemoteProto

class MetaBuildTargetScopeProvider : BuildTargetScopeProvider() {
  override fun getBuildTargetScopes(baseScope: CompileScope, project: Project, forceBuild: Boolean): MutableList<CmdlineRemoteProto.Message.ControllerMessage.ParametersMessage.TargetTypeBuildScope> {
    println("MetaBuildTargetScopeProvider.getBuildTargetScopes")
    return super.getBuildTargetScopes(baseScope, project, forceBuild)
  }
}