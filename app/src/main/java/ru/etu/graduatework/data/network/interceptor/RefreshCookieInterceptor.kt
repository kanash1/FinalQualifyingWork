package ru.etu.graduatework.data.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import ru.etu.graduatework.data.storage.AppStorage
import javax.inject.Inject

//@Target(AnnotationTarget.FUNCTION)
//@Retention(AnnotationRetention.RUNTIME)
//annotation class RefreshCookie
//
//class RefreshCookieInterceptor @Inject constructor(private val appStorage: AppStorage) :
//    Interceptor {
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val request = chain.request()
//        val method = request.tag(Invocation::class.java)!!.method()
//        val response = chain.proceed(request)
//
//        if (method.isAnnotationPresent(RefreshCookie::class.java) &&
//            response.headers("Set-Cookie").isNotEmpty()
//        ) {
//            appStorage.saveJWTData()
//            response.headers("Set-Cookie").toSet()
//
//        }
//    }
//}