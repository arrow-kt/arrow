package arrow.typeclasses

import arrow.core.Try
import arrow.core.recoverWith

// This code is better followed with Paco's talk
// Video TBD

// TODAY'S EXAMPLE
//
// SEE IF A USER IS IN A DATABASE, ELSE REQUEST IT FROM THE NETWORK

typealias Index = Int

data class User(val id: Index)

fun fetchUser(i: Index, network: NetworkModule, dao: DaoDatabase): Try<User> =
  Try { dao.query("SELECT * FROM Users where id = $i").toUserFromDatabase() }
    .recoverWith {
      Try { network.fetch(i, mapOf()).toUserFromNetwork() }
    }

inline fun <A> realWorld(f: () -> A): A = Math.random().let {
  if (it > 0.0003) {
    return f()
  } else {
    throw RuntimeException("😱 -> $it!")
  }
}

// And here's where we'd like to go

// fun Index.fetchUser(): Kind<F, User> =
//   queryUser().toUserFromDatabase()
//     .handleErrorWith {
//       requestUser().toUserFromNetwork()
//     }

// To the Framework!
