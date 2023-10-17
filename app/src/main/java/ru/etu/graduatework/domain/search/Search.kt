package ru.etu.graduatework.domain.search

interface Search<in Query, out Result> {
    fun submit(query : Query): Collection<Result>
}