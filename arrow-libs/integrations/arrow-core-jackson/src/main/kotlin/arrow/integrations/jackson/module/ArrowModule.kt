package arrow.integrations.jackson.module

import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.cfg.MapperBuilder

public fun <M : ObjectMapper, B : MapperBuilder<M, B>> MapperBuilder<M, B>.addArrowModule(
  eitherModuleConfig: EitherModuleConfig = EitherModuleConfig("left", "right"),
  iorModuleConfig: IorModuleConfig = IorModuleConfig("left", "right"),
): MapperBuilder<M, B> = addModules(
  NonEmptyCollectionsModule(),
  OptionModule,
  EitherModule(eitherModuleConfig.leftFieldName, eitherModuleConfig.rightFieldName),
  IorModule(iorModuleConfig.leftFieldName, iorModuleConfig.rightFieldName),
)

public data class EitherModuleConfig(val leftFieldName: String, val rightFieldName: String)

public data class IorModuleConfig(val leftFieldName: String, val rightFieldName: String)
