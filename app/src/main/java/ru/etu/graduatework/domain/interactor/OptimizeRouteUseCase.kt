// автор: Медведев О. В.

package ru.etu.graduatework.domain.interactor

import com.yandex.mapkit.geometry.Point
import ru.etu.graduatework.core.interactor.UseCase
import ru.etu.graduatework.domain.model.OptimizationData
import ru.etu.graduatework.domain.repository.AttractionsRepository
import javax.inject.Inject

class OptimizeRouteUseCase @Inject constructor(
    private val attractionsRepository: AttractionsRepository
) : UseCase<OptimizeRouteUseCase.Params, List<Int>>() {
    override suspend fun run(params: Params): Result<List<Int>> {
        return attractionsRepository.optimizeRoute(
            OptimizationData(
                params.fixedFirst,
                params.fixedSecond,
                params.ids,
                params.paths
            )
        )
    }

    data class Params(
        val ids: List<Int>,
        val fixedFirst: Boolean,
        val fixedSecond: Boolean,
        val paths: List<List<Int>>,
        val userLocation: Point? = null
    )
}