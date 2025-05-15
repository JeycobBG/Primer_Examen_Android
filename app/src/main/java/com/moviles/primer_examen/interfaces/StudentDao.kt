package com.moviles.primer_examen.interfaces

import StudentWithCourse
import androidx.room.*
import com.moviles.primer_examen.model.Student

@Dao
interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStudents(students: List<Student>)

    @Update
    suspend fun updateStudent(student: Student)

    @Query("DELETE FROM students WHERE id = :studentId")
    suspend fun deleteStudentById(studentId: Int)

    @Query("SELECT * FROM students WHERE id = :studentId")
    suspend fun getStudentById(studentId: Int): Student?

    // Obtener todos los estudiantes con su curso asociado
    @Transaction
    @Query("SELECT * FROM students")
    suspend fun getAllStudents(): List<Student>

    @Query("DELETE FROM students")
    suspend fun clearAll()

    @Transaction
    @Query("SELECT * FROM students WHERE id = :studentId LIMIT 1")
    suspend fun getStudentWithCourseById(studentId: Int): StudentWithCourse
}
