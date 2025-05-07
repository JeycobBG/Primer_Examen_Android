package com.moviles.primer_examen.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class StudentWithCourses(
    @Embedded val student: Student,
    @Relation(
        parentColumn = "id", // La columna 'id' de la tabla 'students'
        entityColumn = "id", // La columna 'courseId' de la tabla 'student_course'
        associateBy = Junction(
            value = StudentCourse::class, // Clase intermedia
            parentColumn = "studentId", // Columna 'studentId' de la tabla 'student_course'
            entityColumn = "courseId" // Columna 'courseId' de la tabla 'student_course'
        )
    )
    val courses: List<Course>
)

data class CourseWithStudents(
    @Embedded val course: Course,
    @Relation(
        parentColumn = "id", // La columna 'id' de la tabla 'courses'
        entityColumn = "id", // La columna 'studentId' de la tabla 'student_course'
        associateBy = Junction(
            value = StudentCourse::class, // Clase intermedia
            parentColumn = "courseId", // Columna 'courseId' de la tabla 'student_course'
            entityColumn = "studentId" // Columna 'studentId' de la tabla 'student_course'
        )
    )
    val students: List<Student>
)
