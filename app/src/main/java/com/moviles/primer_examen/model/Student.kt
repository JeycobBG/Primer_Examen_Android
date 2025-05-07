package com.moviles.primer_examen.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey val id: Int? = null,
    val name: String,
    val email: String,
    val phone: String,
    val courseId: Int
)
