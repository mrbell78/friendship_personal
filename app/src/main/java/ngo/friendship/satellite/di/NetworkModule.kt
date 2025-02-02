package ngo.friendship.satellite.di

import com.example.frindshipassignment.api.AuthorizationInterceptor
import com.example.frindshipassignment.api.EndPoints
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ngo.friendship.satellite.constants.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
/**
 * Created by Yeasin Ali on 7/3/2023.
 * friendship.ngo
 * yeasinali@friendship.ngo
 */
@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {
    @Singleton
    @Provides
    fun providesRetrofit(): Retrofit {
        val token = "5f54c461fb7d6b345b3a770fa4586795ec1125bb80459ed9daba5f1c26cf26e0"
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthorizationInterceptor(token))
            .build()
        return Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Singleton
    @Provides
    fun providesFakerAPI(retrofit: Retrofit) : EndPoints {
        return retrofit.create(EndPoints::class.java)
    }
}