package ru.etu.graduatework.data.repository

import ru.etu.graduatework.core.repository.BaseRepository
import ru.etu.graduatework.data.network.AppService
import ru.etu.graduatework.data.network.IdRequest
import ru.etu.graduatework.data.network.OptimizationRequest
import ru.etu.graduatework.data.network.StubService
import ru.etu.graduatework.domain.model.OptimizationData
import ru.etu.graduatework.domain.repository.AttractionsRepository
import javax.inject.Inject

// реализация интерфейса репозитория достопримечательностей
class AttractionsRepositoryImp @Inject constructor(
    private val service: AppService
) : BaseRepository(), AttractionsRepository {
    override suspend fun getAttraction(id: Int) = execute {
        service.getFullAttraction(IdRequest(id)).toAttraction()
    }

    override suspend fun getAttractions() = execute {
        service.getLiteAttractions().value.map { it.toLiteAttraction()}
    }

    override suspend fun optimizeRoute(
        params: OptimizationData
    ) = execute {
//        service.optimizeRoute(
//            OptimizationRequest(
//                ids = params.ids,
//                paths = params.paths,
//                fixFirst = params.fixFirst,
//                fixLast = params.fixLast,
//                userPaths = params.userPaths,
//                userPosition = params.userPosition
//            )
//        ).ids
        service.optimizeRoute(
            OptimizationRequest(
                ids = params.ids,
                paths = params.paths,
                fixFirst = params.fixFirst,
                fixLast = params.fixLast,
                userPaths = params.userPaths,
                userPosition = params.userPosition
            )
        )
    }

    override suspend fun getAttractionPicture(id: Int) = execute {
        val body = service.getPicture(IdRequest(id))
        return@execute body.bytes()
    }
}