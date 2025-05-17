package com.moviles.primer_examen.interfaces

import androidx.room.*
import com.moviles.primer_examen.model.Course

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

    @Query("DELETE FROM courses")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCourses(courses: List<Course>)

     @Query("DELETE FROM courses WHERE id = :courseId")
    suspend fun deleteCourseById(courseId: Int)

}