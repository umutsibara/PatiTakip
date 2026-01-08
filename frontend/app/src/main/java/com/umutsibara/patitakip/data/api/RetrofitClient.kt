package com.umutsibara.patitakip.data.api

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import com.umutsibara.patitakip.util.Constants
import com.umutsibara.patitakip.util.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var retrofit: Retrofit? = null

    fun getClient(context: Context): Retrofit {
        if (retrofit == null) {
            val loggingInterceptor =
                    HttpLoggingInterceptor { message ->
                        Log.d("OkHttp", message)
                    }.apply { level = HttpLoggingInterceptor.Level.BODY }

            val authInterceptor =
                    okhttp3.Interceptor { chain ->
                        val original = chain.request()
                        val token = SessionManager(context).getAuthToken()

                        val requestBuilder = original.newBuilder()
                        if (!token.isNullOrEmpty()) {
                            requestBuilder.header("Authorization", "Bearer $token")
                        }

                        val request = requestBuilder.build()
                        chain.proceed(request)
                    }

            val client =
                    OkHttpClient.Builder()
                            .addInterceptor(loggingInterceptor)
                            .addInterceptor(authInterceptor)
                            .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                            .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                            .writeTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                            .build()

            // Gson with pretty printing for better debugging
            val gson = GsonBuilder()
                .setPrettyPrinting()
                .setLenient()
                .create()

            retrofit =
                    Retrofit.Builder()
                            .baseUrl(Constants.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .client(client)
                            .build()
        }
        return retrofit!!
    }

    fun getApiService(context: Context): ApiService {
        return getClient(context).create(ApiService::class.java)
    }
}
