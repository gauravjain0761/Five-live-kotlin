package com.fivelive.app.retrofit

import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.fivelive.app.app.FiveLiveApplication
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient {
    // .header("Authorization", AllAPIs.Authorization_TOKEN)
    //loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
    val client: APIServiceInterface
        get() {
            if (apiServiceInterface == null) {
                val loggingInterceptor = HttpLoggingInterceptor()
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                //loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
                val httpBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS)
                httpBuilder.addNetworkInterceptor(loggingInterceptor)

                FiveLiveApplication.instance?.let {
                    httpBuilder.addInterceptor(
                        ChuckerInterceptor.Builder(it)
                            .collector(ChuckerCollector(it))
                            .build()
                    )
                }

                httpBuilder.addInterceptor(Interceptor { chain ->
                    val original: Request = chain.request()
                    val request =
                        original.newBuilder() // .header("Authorization", AllAPIs.Authorization_TOKEN)
                            .header("Content-Type", "application/x-www-form-urlencoded")
                            .method(original.method, original.body)
                            .build()
                    chain.proceed(request)
                })
                val builder = Retrofit.Builder()
                    .baseUrl(Base_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpBuilder.build())
                val retrofit = builder.build()
                apiServiceInterface = retrofit.create(
                    APIServiceInterface::class.java
                )
            }
            return apiServiceInterface!!
        }

    companion object {
        // public static final String Base_URL = "http://mobuloustech.com/5live/api/";
        // public static final String Base_URL = "http://3.13.24.30/api/";
        const val Base_URL = "http://13.59.114.202/api/"
        private var apiClientConnection: ApiClient? = null
        private var apiServiceInterface: APIServiceInterface? = null
        val instance: ApiClient
            get() {
                if (apiClientConnection == null) {
                    apiClientConnection = ApiClient()
                }
                return apiClientConnection!!
            }
    }
}