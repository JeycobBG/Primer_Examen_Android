package com.moviles.primer_examen.model

data class StudentApiDto(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val courses: List<Course>
)
