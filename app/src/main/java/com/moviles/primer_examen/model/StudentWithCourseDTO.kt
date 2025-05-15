package com.moviles.primer_examen.model

data class StudentWithCourseDTO(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val course: CourseDTO
)

data class CourseDTO(
    val id: Int,
    val name: String,
    val description: String,
    val imageUrl: String,
    val schedule: String,
    val professor: String
)
