package com.moviles.primer_examen.model

data class CreateStudentRequest(
    val name: String,
    val email: String,
    val phone: String,
    val courseIds: List<Int>
)