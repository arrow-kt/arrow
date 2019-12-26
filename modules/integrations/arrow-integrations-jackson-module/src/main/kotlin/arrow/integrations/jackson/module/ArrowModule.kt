package arrow.integrations.jackson.module

import com.fasterxml.jackson.databind.ObjectMapper

fun ObjectMapper.registerArrowModule(): ObjectMapper = registerModules(NonEmptyListModule, OptionModule)
