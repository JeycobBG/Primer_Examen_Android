package com.moviles.primer_examen.model

import androidx.room.Embedded
import androidx.room.Relation

data class CourseWithStudents(
    @Embedded val course: Course,
    @Relation(
        parentColumn = "id",
        entityColumn = "courseId"
    )
    val students: List<Student>
)
