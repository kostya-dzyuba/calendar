package ru.kostya_dzyuba.calendar.model

import java.time.LocalDate

data class Task(
    val id: Long = 0,
    val name: String,
    val date: LocalDate,
    val completed: Boolean = false
)