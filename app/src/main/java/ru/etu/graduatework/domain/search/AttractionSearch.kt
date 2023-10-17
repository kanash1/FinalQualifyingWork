// автор: Медведев О. В.

package ru.etu.graduatework.domain.search

import ru.etu.graduatework.domain.model.LiteAttraction
import java.util.PriorityQueue

class AttractionSearch(
    private val collection: Collection<LiteAttraction>,
    private val maxResultsCount: UInt = 10u
): Search<String, LiteAttraction> {

    override fun submit(query: String): Collection<LiteAttraction> {
        val results = mutableListOf<LiteAttraction>()
        if (query.isNotEmpty()) {
            val attractionsPriority = PriorityQueue(Comparator())
            val queryParts = query
                .trim()
                .lowercase()
                .split("[\\p{Punct}\\s]+".toRegex())
            for (attraction in collection) {
                val nameParts = attraction.name
                    ?.trim()
                    ?.lowercase()
                    ?.split("[\\p{Punct}\\s]+".toRegex())
                    ?.toMutableList()
                if (nameParts != null) {
                    var hint = 0u
                    var counter: UInt = 0u
                    for (queryPart in queryParts) {
                        if (counter < (queryParts.size - 1).toUInt()) {
                            val pos = nameParts.indexOfFirst { it == queryPart }
                            if (pos != -1) {
                                ++hint
                                nameParts.removeAt(pos)
                            }
                        } else {
                            for (namePart in nameParts) {
                                if (namePart.startsWith(queryPart)) {
                                    ++hint
                                    break
                                }
                            }
                        }
                        ++counter
                    }
                    if (hint == queryParts.size.toUInt())
                        attractionsPriority.add(attraction)
                }
            }
            var counter: UInt = 0u
            for (attractionPriority in attractionsPriority) {
                if (counter >= maxResultsCount)
                    break
                results.add(attractionPriority)
                ++counter
            }
        }
        return results
    }

    class Comparator: java.util.Comparator<LiteAttraction> {
        override fun compare(o1: LiteAttraction, o2: LiteAttraction): Int {
            if (o1.name == null && o2.name == null)
                return 0
            else if (o1.name == null)
                return 1
            else if (o2.name == null || o1.name!! > o2.name!!)
                return -1
            else if (o1.name!! < o2.name!!)
                return 1
            return 0
        }
    }
}