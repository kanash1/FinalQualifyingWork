// автор: Медведев О. В.
// файл внедрения зависимостей

package ru.etu.graduatework.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import ru.etu.graduatework.data.network.interceptor.AuthInterceptor
import ru.etu.graduatework.data.repository.AttractionsRepositoryImp
import ru.etu.graduatework.data.repository.AuthRepositoryImp
import ru.etu.graduatework.data.repository.RoutesRepositoryImp
import ru.etu.graduatework.data.storage.AppStorage
import ru.etu.graduatework.domain.repository.AttractionsRepository
import ru.etu.graduatework.domain.repository.AuthRepository
import ru.etu.graduatework.domain.repository.RoutesRepository
import javax.inject.Singleton

// расширение для доступа к хранилищу
val Context.dataStore by preferencesDataStore(name = "appData")

// модуль внедрения
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val API_SERVER_URL = "http://26.228.215.194:3000/"
    private const val CONTENT_TYPE = "application/json; charset=utf-8"

    private val json = Json { ignoreUnknownKeys = true }

    // предоставляет адаптер интерфейса API к HTTP вызовам
    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(API_SERVER_URL)
            .client(client)
            .addConverterFactory(
                json.asConverterFactory(CONTENT_TYPE.toMediaType())
            )
            .build()
    }
    // предоставляет фабрику вызовов для отправки HTTP запросов
    @Singleton
    @Provides
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        val loggingInterceptor =
            HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)
        val okHttpClientBuilder = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
        return okHttpClientBuilder.build()
    }
    // предоставляет перехватчик HTTP запросов
    @Singleton
    @Provides
    fun provideAuthInterceptor(
        storage: AppStorage
    ) = AuthInterceptor(storage)
    // предоставляет репозиторий достопримечательностей
    @Provides
    @Singleton
    fun provideAttractionRepository(
        source: AttractionsRepositoryImp
    ): AttractionsRepository = source
    // предоставляет репозиторий аутентификации
    @Provides
    @Singleton
    fun provideAuthRepository(
        source: AuthRepositoryImp
    ): AuthRepository = source
    // предоставляет репозиторий маршрутов
    @Provides
    @Singleton
    fun provideRoutesRepository(
        source: RoutesRepositoryImp
    ): RoutesRepository = source
    // предоставляет доступ к хранилищу устройства
    @Provides
    @Singleton
    fun provideStorage(
        @ApplicationContext appContext: Context
    ): AppStorage {
        return AppStorage(appContext)
    }
}