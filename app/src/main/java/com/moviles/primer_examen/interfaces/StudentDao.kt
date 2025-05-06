package com.moviles.primer_examen.interfaces

import androidx.room.*
import com.moviles.primer_examen.model.Student

@Dao
interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    @Query("SELECT * FROM students WHERE id = :studentId")
    suspend fun getStudentById(studentId: Int): Student?

    @Query("SELECT * FROM students WHERE courseId = :courseId")
    suspend fun getStudentsByCourseId(courseId: Int): List<Student>

    @Query("DELETE FROM students")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<Student>)
}
