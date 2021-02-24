package arrow.integrations.retrofit.adapter

import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class CallKind2CallAdapter<R>(private val type: Type) : CallAdapter<R, CallK<R>> {
  override fun adapt(call: Call<R>): CallK<R> = CallK(call)

  override fun responseType(): Type = type
}
