// автор: Медведев О. В.

package ru.etu.graduatework.domain.interactor

import ru.etu.graduatework.core.exception.Failure
import ru.etu.graduatework.core.interactor.UseCase
import ru.etu.graduatework.domain.model.LiteAttraction
import ru.etu.graduatework.domain.search.AttractionSearch
import javax.inject.Inject

class SearchUseCase @Inject constructor() :
    UseCase<Pair<String, Collection<LiteAttraction>>, Collection<LiteAttraction>>() {
    override suspend fun run(params: Pair<String, Collection<LiteAttraction>>): Result<Collection<LiteAttraction>> {
        val collection = AttractionSearch(params.second).submit(params.first)
        return if (collection.isNotEmpty()) {
            Result.success(collection)
        } else {
            Result.failure(Failure.Search)
        }
    }

}