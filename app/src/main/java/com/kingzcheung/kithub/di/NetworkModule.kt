package com.kingzcheung.kithub.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.kingzcheung.kithub.data.remote.api.GitHubApi
import com.kingzcheung.kithub.data.remote.api.GitHubAuthApi
import com.kingzcheung.kithub.data.repository.*
import com.kingzcheung.kithub.util.ErrorResponseInterceptor
import com.kingzcheung.kithub.util.TokenInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    private const val GITHUB_API_BASE_URL = "https://api.github.com/"
    private const val GITHUB_AUTH_BASE_URL = "https://github.com/login/"
    
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    
    @Provides
    @Singleton
    fun provideOkHttpClient(
        tokenInterceptor: TokenInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(tokenInterceptor)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(ErrorResponseInterceptor())
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("Accept", "application/vnd.github.v3+json")
                    .build()
                chain.proceed(request)
            }
            .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideTokenInterceptor(dataStore: DataStore<Preferences>): TokenInterceptor {
        return TokenInterceptor(dataStore)
    }
    
    @Provides
    @Singleton
    @Named("GitHubApi")
    fun provideGitHubRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(GITHUB_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    @Named("GitHubAuth")
    fun provideGitHubAuthRetrofit(loggingInterceptor: HttpLoggingInterceptor): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("Accept", "application/json")
                    .build()
                chain.proceed(request)
            }
            .build()
        
        return Retrofit.Builder()
            .baseUrl(GITHUB_AUTH_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideGitHubApi(@Named("GitHubApi") retrofit: Retrofit): GitHubApi {
        return retrofit.create(GitHubApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideGitHubAuthApi(@Named("GitHubAuth") retrofit: Retrofit): GitHubAuthApi {
        return retrofit.create(GitHubAuthApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideUserRepository(api: GitHubApi): UserRepository = UserRepository(api)
    
    @Provides
    @Singleton
    fun provideRepositoryRepository(api: GitHubApi): RepositoryRepository = RepositoryRepository(api)
    
    @Provides
    @Singleton
    fun provideIssueRepository(api: GitHubApi): IssueRepository = IssueRepository(api)
    
    @Provides
    @Singleton
    fun providePullRequestRepository(api: GitHubApi): PullRequestRepository = PullRequestRepository(api)
    
    @Provides
    @Singleton
    fun provideCommitRepository(api: GitHubApi): CommitRepository = CommitRepository(api)
    
    @Provides
    @Singleton
    fun provideAuthRepository(dataStore: DataStore<Preferences>, authApi: GitHubAuthApi): AuthRepository =
        AuthRepository(dataStore, authApi)
}