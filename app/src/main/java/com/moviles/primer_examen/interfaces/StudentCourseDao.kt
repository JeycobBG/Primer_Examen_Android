package com.moviles.primer_examen.interfaces

import androidx.room.*
import com.moviles.primer_examen.model.StudentCourse

@Dao
interface StudentCourseDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(studentCourse: StudentCourse)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(studentCourses: List<StudentCourse>)

    @Delete
    suspend fun delete(studentCourse: StudentCourse)

    @Query("DELETE FROM StudentCourse WHERE studentId = :studentId")
    suspend fun deleteByStudentId(studentId: Int)

    @Query("DELETE FROM StudentCourse WHERE courseId = :courseId")
    suspend fun deleteByCourseId(courseId: Int)
}