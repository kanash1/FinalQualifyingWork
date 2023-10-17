package ru.etu.graduatework.ui.route.router

import android.content.Context
import android.widget.Toast
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.runtime.Error
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import ru.etu.graduatework.R
import ru.etu.graduatework.domain.model.Route
import ru.etu.graduatework.ui.route.navigation.RouteNavigationViewModel

sealed class Router(
    protected val routeCollection: MapObjectCollection,
    protected val context: Context,
    protected val viewModel: RouteNavigationViewModel? = null
) {

    companion object {
        fun create(
            type: Route.Type,
            routeCollection: MapObjectCollection,
            context: Context
        ): Router {
            return when (type) {
                Route.Type.PEDESTRIAN -> PedestrianRouter(routeCollection, context)
                Route.Type.BICYCLE -> BicycleRouter(routeCollection, context)
                Route.Type.MASSTRANSIT -> MasstransitRouter(routeCollection, context)
                Route.Type.DRIVING -> DrivingRouter(routeCollection, context)
            }
        }
    }

    protected var onSuccess: ((timeAndDistance: String) -> Unit)? = null
    private var onError: (() -> Unit)? = null

    protected abstract fun requestInner(requestPoints: List<RequestPoint>)

    fun request(
        requestPoints: List<RequestPoint>,
        onSuccess: ((timeAndDistance: String) -> Unit),
        onError: (() -> Unit)
    ) {
        this.onSuccess = onSuccess
        this.onError = onError
        requestInner(requestPoints)
    }

    protected fun onError(error: Error) {
        val errorMessage = when (error) {
            is RemoteError -> context.resources.getString(R.string.remote_error_message)
            is NetworkError -> context.resources.getString(R.string.network_error_message)
            else -> context.resources.getString(R.string.unknown_error_message)
        }
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        onError?.invoke()
    }
}