package com.moviles.primer_examen.interfaces

import androidx.room.*
import com.moviles.primer_examen.model.Student
import com.moviles.primer_examen.model.StudentCourse
import com.moviles.primer_examen.model.StudentWithCourses

@Dao
interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)

    @Update
    suspend fun updateStudent(student: Student)

    @Query("DELETE FROM students WHERE id = :studentId")
    suspend fun deleteStudentById(studentId: Int)

    @Query("SELECT * FROM students WHERE id = :studentId")
    suspend fun getStudentById(studentId: Int): Student?

    @Transaction
    @Query("SELECT * FROM students WHERE id = :studentId")
    suspend fun getStudentWithCourses(studentId: Int): StudentWithCourses?

    @Transaction
    @Query("SELECT * FROM students")
    suspend fun getAllStudentsWithCourses(): List<StudentWithCourses>

    @Query("DELETE FROM students")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentCourseRelationship(studentCourseList: List<StudentCourse>)

    // Eliminar relaciones de un estudiante específico
    @Query("DELETE FROM StudentCourse WHERE studentId = :studentId")
    suspend fun clearStudentCourses(studentId: Int)

    // Eliminar las relaciones entre el estudiante y los cursos
    @Query("DELETE FROM StudentCourse WHERE studentId = :studentId")
    suspend fun deleteStudentCoursesByStudentId(studentId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStudents(students: List<Student>)
}

