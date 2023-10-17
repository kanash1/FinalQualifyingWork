package ru.etu.graduatework.data.network

import android.graphics.Bitmap
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StubService @Inject constructor(retrofit: Retrofit) : AppApi {
    private val map = hashMapOf<Int, FullAttractionResponse>(
        (0 to FullAttractionResponse(
            0,
            "Государственный Эрмитаж",
            null,
            rating = null,
            cost = null,
            description = "В самом сердце Санкт-Петербурга, на берегу Невы, находится один из известнейших музеев мира Эрмитаж. Поток желающих посетить его, чтобы полюбоваться произведениями искусства, собранными здесь, не ослабевает ни летом, ни зимой.",
            longitude = 59.93992621187502,
            latitude = 30.31452091850644,
            isWorking = false,
            today = TodayScheduleItemResponse(1, isWorking = false)
        )),
        (1 to FullAttractionResponse(
            1,
            "Храм Воскресения Христова Спас на Крови",
            "Православный храм",
            rating =5.0,
            cost = null,
            description = "В самом центре Петербурга, на набережной канала Грибоедова, привлекает внимание своим нарядным, многоцветным обликом храм Воскресения Христова (Спас на Крови). Построенный на месте, где 1 марта 1881 года был смертельно ранен император Александр II, собор является мемориалом Царю-Освободителю.",
            longitude = 59.9402074852141,
            latitude = 30.328738706561158,
            isWorking = false,
            today = TodayScheduleItemResponse(1, isWorking = false)
        )),
        (2 to FullAttractionResponse(
            2,
            "Исаакиевский собор",
            "Православный храм, музей",
            rating =5.0,
            cost = 250,
            description = "Исаакиевский собор — крупнейший на сегодняшний день православный храма Санкт-Петербурга и одно из высочайших купольных сооружений в мире. Его история началась в 1710 г., когда была построена деревянная церковь в честь Исаакия Далматского — византийского святого, на день памяти которого приходится день рождения Петра Великого.",
            longitude = 59.93416092688723,
            latitude = 30.30624481691349,
            isWorking = false,
            today = TodayScheduleItemResponse(1, isWorking = false)
        )),
        (3 to FullAttractionResponse(
            3,
            "Казанский кафедральный собор",
            "Православный храм",
            rating =null,
            cost = 0,
            description = "Величественный собор построили на месте обветшалой Рождественско-Богородичной церкви в честь Казанской чудотворной иконы Божией Матери - той самой, что сохранилась при пожаре в Казани и которую нашли через 25 лет после взятия этого города Иваном Грозным.",
            longitude = 59.9343687220773,
            latitude = 30.324664653484543,
            isWorking = false,
            today = TodayScheduleItemResponse(1, isWorking = false)
        )),
        (4 to FullAttractionResponse(
            4,
            "Петергоф",
            null,
            rating =5.0,
            cost = 600,
            description = "Петергоф, на протяжении 200 лет бывший парадной летней резиденцией императоров, неразрывно связан с нашей историей. Приморский парадиз строился как грандиозный триумфальный памятник, прославляющий величие России, завоевавшей в ходе Северной войны столь необходимый и желанный выход к Балтийскому морю.",
            longitude = 59.881225,
            latitude = 29.906776,
            isWorking = false,
            today = TodayScheduleItemResponse(1, isWorking = false)
        ))
    )

    override suspend fun getLiteAttractions(): LiteAttractionsResponse {
        return LiteAttractionsResponse(
            mutableListOf<LiteAttractionResponse>().apply {
                for (attraction in map.values)
                    add(attraction.toLiteAttractionResponse())
            }
        )
    }

    override suspend fun optimizeRoute(request: OptimizationRequest): OptimizationResponse {
//        return OptimizationResponse(request.ids)
        return request.ids
    }

    override suspend fun getFullAttraction(attractionId: IdRequest): FullAttractionResponse {
        return map[attractionId.value]!!
    }

    override suspend fun getSchedule(attractionId: IdRequest): ScheduleResponse {
        return ScheduleResponse(emptyList())
    }

    override suspend fun getPicture(attractionId: IdRequest): ResponseBody {
        val stream = ByteArrayOutputStream()
        val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val array = stream.toByteArray()
        bitmap.recycle()
        return ResponseBody.create("image/*".toMediaTypeOrNull(), array)
    }

    override suspend fun signUp(request: SignUpRequest) {}

    override suspend fun signIn(request: SignInRequest): Response<TokenResponse> {
        val headers = Headers.headersOf("Set-Cookie: refresh=TOKEN")
        return Response.success(TokenResponse("TOKEN"), headers)
    }

//    override suspend fun refresh(cookie: Set<String>): Response<TokenResponse> {
//        val headers = Headers.headersOf("Set-Cookie: refresh=TOKEN")
//        return Response.success(TokenResponse("TOKEN"), headers)
//    }
//
//    override suspend fun logout(cookie: Set<String>) {}
    override suspend fun refresh(): Response<TokenResponse> {
        val headers = Headers.headersOf("Set-Cookie: refresh=TOKEN")
        return Response.success(TokenResponse("TOKEN"), headers)
    }

    override suspend fun logout() {}
    override suspend fun saveRoute(request: RouteRequest) {

    }

    override suspend fun getRoute(request: RouteNameRequest): RouteResponse {
        return RouteResponse(request.value, listOf(0,1,2),false, false, "pedestrian")
    }

    override suspend fun getRoutes(): RouteNamesResponse {
        return RouteNamesResponse(emptyList())
    }

    override suspend fun removeRoute(request: RouteNameRequest) {

    }
}