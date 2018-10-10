package arrow.integrations.retrofit.adapter

import arrow.effects.IO
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Response
import java.lang.reflect.Type

class IO2CallAdapter<R>(private val type: Type) : CallAdapter<R, IO<Response<R>>> {
  override fun adapt(call: Call<R>): IO<Response<R>> =
    IO.async { proc -> call.enqueue(ResponseCallback(proc)) }

  override fun responseType(): Type = type
}
