package com.example.pixabay.model.api

import com.example.pixabay.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.koin.dsl.module
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

val networkModule = module {
    factory { AuthInterceptor() }
    factory { provideHttpLoggingInterceptor() }
    factory { provideOkHttpClient(get(), get()) }
    factory { providePixabayApi(get()) }
    single { provideRetrofit(get()) }
}

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create()).build()
}

fun provideOkHttpClient(
    authInterceptor: AuthInterceptor,
    loggingInterceptor: HttpLoggingInterceptor
): OkHttpClient {
    return OkHttpClient()
        .newBuilder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .readTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .build()
}

fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
}

fun providePixabayApi(retrofit: Retrofit): PixabayService = retrofit.create(PixabayService::class.java)

