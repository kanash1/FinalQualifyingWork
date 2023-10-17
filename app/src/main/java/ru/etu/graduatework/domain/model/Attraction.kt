package ru.etu.graduatework.domain.model

data class Attraction(
    val id: Int,
    val name: String,
    val type: String?,
    val rating: Double?,
    val schedule: Schedule,
    val cost: Int?,
    val description: String?,
    val point: Point,
    val isWorking: Boolean,
    var pictureByteArray: ByteArray? = null
)
