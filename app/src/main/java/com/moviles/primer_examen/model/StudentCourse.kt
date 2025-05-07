package com.moviles.primer_examen.model

import androidx.room.Entity

@Entity(
    tableName = "StudentCourse",
    primaryKeys = ["studentId", "courseId"])
data class StudentCourse(
    val studentId: Int,
    val courseId: Int
)
