package arrow.integrations.retrofit.adapter.retrofit

import arrow.integrations.retrofit.adapter.Async2CallAdapterFactory
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private fun provideOkHttpClient(): OkHttpClient =
  OkHttpClient.Builder().build()

private fun configRetrofit(retrofitBuilder: Retrofit.Builder) =
  retrofitBuilder
    .addCallAdapterFactory(Async2CallAdapterFactory.create())
    .addConverterFactory(GsonConverterFactory.create())
    .client(provideOkHttpClient())

private fun getRetrofitBuilderDefaults(baseUrl: HttpUrl) = Retrofit.Builder().baseUrl(baseUrl)

fun retrofit(baseUrl: HttpUrl): Retrofit = configRetrofit(getRetrofitBuilderDefaults(baseUrl)).build()
