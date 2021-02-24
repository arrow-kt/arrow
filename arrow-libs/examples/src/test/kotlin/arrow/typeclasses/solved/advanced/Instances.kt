package com.pacoworks.typeclasses.basics.solved.advanced

import arrow.typeclasses.DaoDatabase
import arrow.typeclasses.NetworkModule
import arrow.typeclasses.Query
import arrow.typeclasses.UserDao
import arrow.typeclasses.UserDto

interface NetworkModuleNetworkFetcher : NetworkFetcher {
  companion object {
    private val nm = NetworkModule()
  }

  override fun fetch(id: Int, headers: Map<String, String>): UserDto =
    nm.fetch(id, headers)

  override fun fetchAsync(id: Int, headers: Map<String, String>, fe: (Throwable) -> Unit, f: (UserDto) -> Unit) =
    nm.fetchAsync(id, headers, fe, f)
}

interface DaoDatabaseDaoFetcher : DaoFetcher {

  companion object {
    private val dao = DaoDatabase()
  }

  override fun query(s: Query): UserDao =
    dao.query(s)

  override fun queryAsync(s: Query, fe: (Throwable) -> Unit, f: (UserDao) -> Unit) =
    dao.queryAsync(s, fe, f)
}
