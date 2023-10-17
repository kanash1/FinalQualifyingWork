// автор: Медведев О. В.

package ru.etu.graduatework.domain.interactor

import ru.etu.graduatework.core.interactor.UnitUseCase
import ru.etu.graduatework.domain.repository.AttractionsRepository
import ru.etu.graduatework.domain.model.LiteAttraction
import javax.inject.Inject

// прецедент для получения словаря, где в качестве ключа
// выступает id достопримечательности, а в качестве значения
// сам объект
class GetAttractionsUseCase @Inject constructor(
    private val attractionsRepository: AttractionsRepository
) : UnitUseCase<Map<Int, LiteAttraction>>() {

    override suspend fun run(): Result<Map<Int, LiteAttraction>> {
        val getAttractionsResult = attractionsRepository.getAttractions()
        if (getAttractionsResult.isSuccess) {
            val attractions = getAttractionsResult.getOrNull()!!
            val attractionById = attractions.associateBy { it.id }
            return Result.success(attractionById)
        }
        return Result.failure(getAttractionsResult.exceptionOrNull()!!)
    }
}