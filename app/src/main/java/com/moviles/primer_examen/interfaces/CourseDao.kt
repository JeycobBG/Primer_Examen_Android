package com.moviles.primer_examen.interfaces

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.moviles.primer_examen.model.Course
import com.moviles.primer_examen.model.CourseWithStudents

@Dao
interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: Course)

    @Update
    suspend fun updateCourse(course: Course)

    @Delete
    suspend fun deleteCourse(course: Course)

    @Query("SELECT * FROM courses")
    suspend fun getAllCourses(): List<Course>

    @Query("SELECT * FROM courses WHERE id = :courseId")
    suspend fun getCourseById(courseId: Int): Course?

    @Transaction
    @Query("SELECT * FROM courses WHERE id = :courseId")
    suspend fun getCourseWithStudents(courseId: Int): CourseWithStudents?

    @Transaction
    @Query("SELECT * FROM courses")
    suspend fun getAllCoursesWithStudents(): List<CourseWithStudents>
}