@file:Suppress("unused")

package arrow.typeclasses

typealias Query = String

data class UserDao(val id: Int)

class DaoDatabase {
  fun query(s: Query): UserDao = realWorld {
    UserDao(1)
  }

  fun queryAsync(s: Query, fe: (Throwable) -> Unit, f: (UserDao) -> Unit): Unit =
    try {
      f(query(s))
    } catch (t: Throwable) {
      fe(t)
    }

  companion object
}
