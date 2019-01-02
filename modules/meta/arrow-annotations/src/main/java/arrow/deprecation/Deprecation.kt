package arrow.deprecation

const val ExtensionsDSLDeprecated: String =
  "Since Arrow 0.8.0 ForX extensions {...} is deprecated in favor of direct extensions access via type class projections." +
  "\nImport the syntax functions directly that have been projected for your data type instances"