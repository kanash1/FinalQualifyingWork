package ru.etu.graduatework.domain.model

data class Schedule(
    val today: TodayItem,
    val list: List<Item>
) {
    class Item(val day: DayOfWeek, val start: String, val end: String)

    class TodayItem(val day: DayOfWeek, val start: String?, val end: String?, val isWorking: Boolean)

    enum class DayOfWeek(val value: Int, val abbr: String, val genitive: String) {
        MONDAY(0, "Пн", "понедельника"),
        TUESDAY(1, "Вт", "вторника"),
        WEDNESDAY(2, "Ср", "среды"),
        THURSDAY(3, "Чт", "четверга"),
        FRIDAY(4, "Пт", "пятницы"),
        SATURDAY(5, "Сб", "субботы"),
        SUNDAY(6, "Вс", "воскресенья");

        companion object {
            fun fromInt(value: Int) = DayOfWeek.values().first { it.value == value }
        }
    }
}