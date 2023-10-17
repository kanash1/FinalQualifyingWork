// автор: Медведев О. В.

package ru.etu.graduatework.domain.interactor

import ru.etu.graduatework.core.interactor.UseCase
import ru.etu.graduatework.domain.repository.AttractionsRepository
import ru.etu.graduatework.domain.model.Attraction
import javax.inject.Inject

// прецедент для получения информации о достопримечатльности
// и его изображения
class GetAttractionUseCase @Inject constructor(
    private val attractionsRepository: AttractionsRepository,
) : UseCase<Int, Attraction>() {

    override suspend fun run(params: Int): Result<Attraction> {
        val getAttractionResult = attractionsRepository.getAttraction(params)
        if (getAttractionResult.isSuccess) {
            val attraction = getAttractionResult.getOrNull()!!
            val getPictureResult =
                attractionsRepository.getAttractionPicture(params)
            if (getPictureResult.isSuccess) {
                attraction.pictureByteArray = getPictureResult.getOrNull()
                return Result.success(attraction)
            }
            return Result.failure(getPictureResult.exceptionOrNull()!!)
        }
        return Result.failure(getAttractionResult.exceptionOrNull()!!)
    }
}