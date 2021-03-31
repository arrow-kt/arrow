package arrow.typeclasses

import com.pacoworks.typeclasses.basics.solved.advanced.NetworkFetcher

typealias Request = String

data class UserDto(val id: Index)

class NetworkModule : NetworkFetcher {
  override fun fetch(id: Int, headers: Map<String, String>): UserDto = realWorld {
    UserDto(id)
  }

  override fun fetchAsync(id: Int, headers: Map<String, String>, fe: (Throwable) -> Unit, f: (UserDto) -> Unit): Unit =
    try {
      f(fetch(id, headers))
    } catch (t: Throwable) {
      fe(t)
    }

  companion object
}
