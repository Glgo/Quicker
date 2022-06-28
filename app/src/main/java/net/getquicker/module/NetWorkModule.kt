package net.getquicker.module

import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.getquicker.BuildConfig
import net.getquicker.net.HttpApi
import net.getquicker.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.Proxy
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 *  author : Clay
 *  date : 2021/12/20 14:05:43
 *  description : Http模块
 */
@Module
@InstallIn(SingletonComponent::class)
object NetWorkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor { message -> Log.e("okhttp", message) }
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            builder.addInterceptor(loggingInterceptor)
        }
        //设置超时
        builder.connectTimeout(Constants.NET_TIMEOUT, TimeUnit.SECONDS)
        builder.readTimeout(Constants.NET_TIMEOUT, TimeUnit.SECONDS)
        builder.writeTimeout(Constants.NET_TIMEOUT, TimeUnit.SECONDS)
        //使用无代理 Proxy.NO_PROXY 防止抓包
        builder.proxy(Proxy.NO_PROXY)
        //错误重连
        builder.retryOnConnectionFailure(true)
        return builder.build()
    }

    @Singleton
    @Provides
    fun provideHttpApi(okHttpClient: OkHttpClient): HttpApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build().create(HttpApi::class.java)
    }
}