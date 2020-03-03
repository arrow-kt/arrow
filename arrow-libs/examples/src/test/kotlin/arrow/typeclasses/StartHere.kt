package arrow.typeclasses

import arrow.fx.IO
import arrow.fx.handleErrorWith

// This code is explained in "Simple dependency management in Kotlin"
// Video: https://skillsmatter.com/skillscasts/12907-simple-dependency-management-in-kotlin

// TODAY'S EXAMPLE
//
// SEE IF A USER IS IN A DATABASE, ELSE REQUEST IT FROM THE NETWORK

typealias Index = Int

data class User(val id: Index)

fun fetchUser(i: Index, network: NetworkModule, dao: DaoDatabase): IO<User> =
    IO { dao.query("SELECT * FROM Users where id = $i").toUserFromDatabase() }
        .handleErrorWith {
            IO { network.fetch(i, mapOf()).toUserFromNetwork() }
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
