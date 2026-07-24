package example

import arrow.optics.optics

// enum class ImAnEnum {
//   Foo, Bar, Baz
// }

@optics
data class ScreenContent(
  val state: ScreenState = ScreenState.Loading,
)

@optics
sealed interface ScreenState {
  @optics
  data class Content(val text: String? = null) : ScreenState

  data object Loading : ScreenState
}

fun setText(state: ScreenContent, text: String?): ScreenContent {
  return ScreenContent.state
    .content
    .text.set(state, text)
}
