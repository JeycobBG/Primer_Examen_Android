package com.moviles.primer_examen.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "students",
    foreignKeys = [ForeignKey(
        entity = Course::class,
        parentColumns = ["id"],
        childColumns = ["courseId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["courseId"])]
)
data class Student(
    @PrimaryKey val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val courseId: Int
)