package arrow.integrations.jackson.module

import com.fasterxml.jackson.databind.ObjectMapper

public fun ObjectMapper.registerArrowModule(
  eitherModuleConfig: EitherModuleConfig = EitherModuleConfig("left", "right"),
  iorModuleConfig: IorModuleConfig = IorModuleConfig("left", "right"),
): ObjectMapper = registerModules(
  NonEmptyCollectionsModule(),
  OptionModule,
  EitherModule(eitherModuleConfig.leftFieldName, eitherModuleConfig.rightFieldName),
  IorModule(iorModuleConfig.leftFieldName, iorModuleConfig.rightFieldName),
)

public data class EitherModuleConfig(val leftFieldName: String, val rightFieldName: String)

public data class IorModuleConfig(val leftFieldName: String, val rightFieldName: String)
