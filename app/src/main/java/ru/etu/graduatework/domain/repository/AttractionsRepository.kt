package ru.etu.graduatework.domain.repository

import ru.etu.graduatework.domain.model.Attraction
import ru.etu.graduatework.domain.model.LiteAttraction
import ru.etu.graduatework.domain.model.OptimizationData
// интерфейс репозитория достопримечательностей
interface AttractionsRepository {
    // получение полной информации о достопримечательности
    suspend fun getAttraction(id: Int): Result<Attraction>
    // получение списка краткой информации о достопримечательности
    suspend fun getAttractions(): Result<List<LiteAttraction>>
    // получение оптимизированного маршрута
    suspend fun optimizeRoute(params: OptimizationData): Result<List<Int>>
    // получение изображения достопримечательности
    suspend fun getAttractionPicture(id: Int): Result<ByteArray>
}